package com.example.cdroneapp.utils


import androidx.lifecycle.MutableLiveData
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.datacenter.media.MediaFile
import dji.v5.manager.datacenter.media.MediaFileDownloadListener
import dji.v5.manager.datacenter.media.MediaFileListData
import dji.v5.manager.datacenter.media.MediaFileListState
import dji.v5.manager.datacenter.media.MediaFileListStateListener
import dji.v5.manager.datacenter.media.PullMediaFileListParam
import dji.v5.utils.common.ContextUtil
import dji.v5.utils.common.DiskUtil
import dji.v5.utils.common.LogUtils
import dji.v5.utils.common.StringUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PhotoFetcher {
    private var intervalMillis: Long = 0
    private var timer: Timer? = null

    private lateinit var latestMediaFile : MediaFile
    private var initialized = false
    private var counter = 0
    var mediaFileListData = MutableLiveData<MediaFileListData>()
    var fileListState = MutableLiveData<MediaFileListState>()
    var isPlayBack = MutableLiveData<Boolean?>()
    var latest_file = "Nothing yet"
    private lateinit var networkHandler: NetworkHandler


    fun init(intervalMillis: Long, movementHandler: MovementHandler) {
        this.intervalMillis = intervalMillis
        addMediaFileListStateListener()
        mediaFileListData.value = MediaDataCenter.getInstance().mediaManager.mediaFileListData
        MediaDataCenter.getInstance().mediaManager.addMediaFileListStateListener { mediaFileListState ->
            if (mediaFileListState == MediaFileListState.UP_TO_DATE) {
                val data = MediaDataCenter.getInstance().mediaManager.mediaFileListData;
                mediaFileListData.postValue(data)
            }
        }
        networkHandler = NetworkHandler()
        networkHandler.init(movementHandler)


    }
    private fun addMediaFileListStateListener() {
        MediaDataCenter.getInstance().mediaManager.addMediaFileListStateListener(object :
            MediaFileListStateListener {
            override fun onUpdate(mediaFileListState: MediaFileListState) {
                fileListState.postValue(mediaFileListState)

            }

        })
    }

    fun start() {
        // Ensure that the timer is stopped before starting a new one
        stop()


        // Validate the interval before starting the timer
        if (intervalMillis <= 0) {
            throw IllegalStateException("Interval must be greater than 0")
        }

        // Run the timer task on a separate thread to avoid blocking the main thread
        thread {
            timer = Timer().apply {
                scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {

                        getMediaFromCamera(1, 1) //should be (1,1) to only get the latest

                    }
                }, 0, intervalMillis)
            }
        }
    }


    fun stop() {
        timer?.cancel()
        timer = null
    }


    fun getMediaFromCamera(mediaFileIndex: Int, count: Int) {
        var currentTime = System.currentTimeMillis()
        MediaDataCenter.getInstance().mediaManager.pullMediaFileListFromCamera(
            PullMediaFileListParam.Builder().mediaFileIndex(mediaFileIndex).count(count).build(),
            object :
                CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                    val mediaList = getMediaFileList()
                    if( mediaList.size != 0) {
                        checkNewMediaFile(mediaList[0])
                    }


                }

                override fun onFailure(error: IDJIError) {
                    GlobalScope.launch {
                        LogHandler.log("Function getMediaFromCamera encounted onFailure, error : " + error)
                    }


                }
            })
    }

    fun getMediaFileList(): List<MediaFile> {
        return mediaFileListData.value?.data!!
    }
    fun checkNewMediaFile(newMediaFile : MediaFile) {
        if (this.initialized == false) {
            this.latestMediaFile = newMediaFile
            this.initialized = true
            GlobalScope.launch {
                LogHandler.log("Set the initial latestMediaFile to: " + newMediaFile.fileName)
            }

        }
        else {
            if (this.latestMediaFile != newMediaFile) {
                this.latestMediaFile = newMediaFile
                GlobalScope.launch {
                    LogHandler.log("Received a new photo: " + newMediaFile.fileName)
                }
                downloadFileAndSend(newMediaFile)

            }
        }

    }
    private fun downloadFileAndSend(mediaFile :MediaFile ) {
        val dirs = File(DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(),  "/mediafile"))
        if (!dirs.exists()) {
            dirs.mkdirs()
        }
        val filepath = DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(),  "/mediafile/"  + mediaFile?.fileName)
        val file = File(filepath)
        var offset = 0L
        val outputStream = FileOutputStream(file, true)
        val bos = BufferedOutputStream(outputStream)
        mediaFile?.pullOriginalMediaFileFromCamera(offset, object : MediaFileDownloadListener {
            override fun onStart() {
                LogUtils.i("MediaFile" , "${mediaFile.fileIndex } start download"  )
            }

            override fun onProgress(total: Long, current: Long) {
                val fullSize = offset + total;
                val downloadedSize = offset + current
                val data: Double = StringUtils.formatDouble((downloadedSize.toDouble() / fullSize.toDouble()))
                val result: String = StringUtils.formatDouble(data * 100, "#0").toString() + "%"
                LogUtils.i("MediaFile"  , "${mediaFile.fileIndex}  progress $result")
            }

            override fun onRealtimeDataUpdate(data: ByteArray, position: Long) {
                try {
                    bos.write(data)
                    bos.flush()
                } catch (e: IOException) {
                    LogUtils.e("MediaFile", "write error" + e.message)
                }
            }

            override fun onFinish() {
                try {
                    outputStream.close()
                    bos.close()
                } catch (error: IOException) {
                    LogUtils.e("MediaFile", "close error$error")
                }
                GlobalScope.launch { LogHandler.log("Download finnished") }
                networkHandler.sendImageToServer("1234", filepath )



            }

            override fun onFailure(error: IDJIError?) {

            }

        })


    }

}
