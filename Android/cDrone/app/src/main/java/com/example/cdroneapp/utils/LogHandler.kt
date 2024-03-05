package com.example.cdroneapp.utils
import android.util.Log
import com.example.cdroneapp.R
import java.util.Collections
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

object LogHandler {
    private val _logMessages = MutableSharedFlow<List<String>>(replay = 1)
    private val logs = mutableListOf<String>()
    val logMessages = _logMessages.asSharedFlow()

    suspend fun log(message: String) {
        // Create a temporary list to hold the new state
        val newLogs: List<String>
        synchronized(logs) {
            logs.add(message)
            // Ensure only the most recent 20 entries are kept
            if (logs.size > 20) {
                logs.removeAt(0) // Remove the oldest log entry
            }
            // Prepare the snapshot of logs to emit
            newLogs = ArrayList(logs)
        }
        // Emit outside the synchronized block to avoid suspension inside critical section
        _logMessages.emit(newLogs)
    }
}