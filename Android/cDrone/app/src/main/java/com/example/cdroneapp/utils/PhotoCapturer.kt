package com.example.cdroneapp.utils

import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread

class PhotoCapturer {
    private var intervalMillis: Long = 0
    private var timer: Timer? = null

    // Initialization block is no longer necessary if we're directly
    // invoking a method within the class

    fun configure(intervalMillis: Long) {
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
                    override fun run() {7-

                            
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
        // Place your code here that you want to execute periodically
        println("Executing specific method on each tick")
    }
}
