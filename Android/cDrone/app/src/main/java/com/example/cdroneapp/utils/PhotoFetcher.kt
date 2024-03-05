package com.example.cdroneapp.utils


import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread
import com.example.cdroneapp.utils.MediaVM
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.datacenter.media.MediaFile
import dji.v5.manager.datacenter.media.PullMediaFileListParam


class PhotoFetcher {
    private var intervalMillis: Long = 0
    private var timer: Timer? = null
    private lateinit var mediaVM : MediaVM
    private lateinit var latestMediaFile : MediaFile
    private var initialized = false

    fun init(intervalMillis: Long, mediaVM :MediaVM ) {
        this.intervalMillis = intervalMillis
        this.mediaVM = mediaVM
        getMediaFromCamera(1, 1)
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
                    }
                }, 0, intervalMillis)
            }
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
                    val mediaList = mediaVM.getMediaFileList()

                    checkNewMediaFile(mediaList[0])

                }

                override fun onFailure(error: IDJIError) {

                    //LogUtils.e(logTag, "fetch failed$error")
                }
            })
    }
    fun checkNewMediaFile(newMediaFile : MediaFile) {
        if (this.initialized == false) {
            this.latestMediaFile = newMediaFile
            newMediaFile.fileName
            Thread {
                LogHandler.log("This is a message from a background thread.")
            }.start()
        }
        else {
            if (this.latestMediaFile != newMediaFile) {
                this.latestMediaFile = newMediaFile
                Thread {
                    LogHandler.log("This is a message from a background thread.")
                }.start()
            }
        }

    }

}
