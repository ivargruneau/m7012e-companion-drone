package com.example.cdroneapp.utils


import androidx.lifecycle.MutableLiveData
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread
import com.example.cdroneapp.utils.MediaVM
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.datacenter.media.MediaFile
import dji.v5.manager.datacenter.media.MediaFileListData
import dji.v5.manager.datacenter.media.MediaFileListState
import dji.v5.manager.datacenter.media.PullMediaFileListParam
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PhotoFetcher {
    private var intervalMillis: Long = 0
    private var timer: Timer? = null
    private lateinit var mediaVM : MediaVM
    private lateinit var latestMediaFile : MediaFile
    private var initialized = false
    private var counter = 0
    var mediaFileListData = MutableLiveData<MediaFileListData>()
    var fileListState = MutableLiveData<MediaFileListState>()
    var isPlayBack = MutableLiveData<Boolean?>()
    var latest_file = "Nothing yet"
    private lateinit var networkHandler: NetworkHandler
    fun init2() {

        mediaFileListData.value = MediaDataCenter.getInstance().mediaManager.mediaFileListData
        MediaDataCenter.getInstance().mediaManager.addMediaFileListStateListener { mediaFileListState ->
            if (mediaFileListState == MediaFileListState.UP_TO_DATE) {
                val data = MediaDataCenter.getInstance().mediaManager.mediaFileListData;
                mediaFileListData.postValue(data)
            }
        }
        networkHandler = NetworkHandler()

    }

    fun init(intervalMillis: Long, mediaVM :MediaVM ) {
        this.intervalMillis = intervalMillis
        this.mediaVM = mediaVM

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

                        specificMethodToExecute()
                        //testPrintCounter(counter)
                        //counter += 1
                    }
                }, 0, intervalMillis)
            }
        }
    }
    fun testPrintCounter(c : Int) {
        GlobalScope.launch {
            LogHandler.log("Counter " + counter.toString())
        }

    }

    fun stop() {
        timer?.cancel()
        timer = null
    }

    // Define the specific method you want to execute on each tick
    private fun specificMethodToExecute() {
        getMediaFromCamera(1, 1)
        // Place your code here that you want to execute periodically

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

                    //LogUtils.e(logTag, "fetch failed$error")
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

            }
        }

    }

}
