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
    private val messageCountCap = 12
    val logMessages = _logMessages.asSharedFlow()

    suspend fun log(message: String) {

        val newLogs: List<String>
        synchronized(logs) {
            logs.add(message)

            if (logs.size > messageCountCap) {
                logs.removeAt(0) // Remove oldest
            }

            newLogs = ArrayList(logs)
        }

        _logMessages.emit(newLogs)
    }
}