package com.example.cdroneapp.utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.File
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis
import kotlin.system.measureNanoTime
class NetworkHandler {
    private lateinit var movementHandler: MovementHandler
    private var startTime by Delegates.notNull<Long>()
    fun init(movementHandler: MovementHandler) {
        this.movementHandler = movementHandler
    }

    public fun timedSendImageToServer(sessionId: String, filePath: String){
        startTime = System.currentTimeMillis()
            // Call your function here
        sendImageToServer(sessionId, filePath)



    }
    public fun sendImageToServer(sessionId: String, filePath: String) {
        val client = OkHttpClient()
        val file = File(filePath)

        // Updated way to get MediaType and create RequestBody
        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val fileBody = file.asRequestBody(mediaType)

        // Create multipart body
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("sessionId", sessionId)
            .addFormDataPart("image", file.name, fileBody)
            .build()

        // Build the request
        val request = Request.Builder()
            .url("http://83.233.46.128:4280/upload")
            .post(requestBody)
            .build()

        val request2 = Request.Builder()
            .url("http://192.168.0.188:4280/upload")
            .post(requestBody)
            .build()

        // Asynchronously send the request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Handle failure
                GlobalScope.launch {
                    LogHandler.log("Error when sending: " +  e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    // Handle error
                    GlobalScope.launch {
                        LogHandler.log("Respons: error ")
                    }
                } else {
                    // Handle success
                    val responseBody = response.body?.string()
                    unpackageResponse(responseBody)

                }
            }
        })
    }
    private fun unpackageResponse(resp : String?){
        val jsonObject = JSONObject(resp)


        val pedestrianDetected = jsonObject.getBoolean("pedestrianDetected")
        val distance = jsonObject.getDouble("distance")
        val horizontalAngle = jsonObject.getDouble("horizontalAngle")
        val verticalAngle = jsonObject.getDouble("verticalAngle")

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime


        GlobalScope.launch { LogHandler.log("Execution time: $duration milliseconds")}
        GlobalScope.launch { LogHandler.log("Respons. pedD: " + pedestrianDetected + ". dist: " + distance.toString())}

    }
}

