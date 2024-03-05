package com.example.cdroneapp.utils
import android.util.Log
import java.util.Collections


object LogHandler {
    private val logs = Collections.synchronizedList(mutableListOf<String>())

    fun log(message: String) {
        synchronized(logs) {
            logs.add(message)
            // Optionally, limit the size of logs to avoid memory issues
            if (logs.size > 1000) logs.removeAt(0)
        }
    }

    fun getLogs(): List<String> {
        synchronized(logs) {
            return ArrayList(logs) // Return a copy to avoid concurrent modification issues
        }
    }
}