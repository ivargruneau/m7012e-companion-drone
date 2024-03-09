package com.example.cdroneapp.utils

import dji.sdk.keyvalue.key.CameraKey
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.value.camera.CameraMode
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.common.error.RxError
import dji.v5.common.utils.CallbackUtils
import dji.v5.common.utils.RxUtil
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread

class PhotoCapturer {
    private var intervalMillis: Long = 0
    private var timer: Timer? = null

    // Initialization block is no longer necessary if we're directly
    // invoking a method within the class

    fun init(intervalMillis: Long) {
        this.intervalMillis = intervalMillis
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


                        capturePhoto()
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
    fun capturePhoto() {
        takePhoto(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                //sleep(1000)
                //getMediaFromCamera(1, 1)
            }

            override fun onFailure(error: IDJIError) {


            }
        })
    }

    private fun takePhoto(callback: CommonCallbacks.CompletionCallback) {

        RxUtil.setValue(
            KeyTools.createKey<CameraMode>(
                CameraKey.KeyCameraMode
            ), CameraMode.PHOTO_NORMAL)
            .andThen(RxUtil.performActionWithOutResult(KeyTools.createKey(CameraKey.KeyStartShootPhoto)))
            .subscribe({ CallbackUtils.onSuccess(callback) }
            ) { throwable: Throwable ->
                CallbackUtils.onFailure(
                    callback,
                    (throwable as RxError).djiError
                )
            }
    }
}
