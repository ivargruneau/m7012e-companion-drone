package com.example.cdroneapp.utils

import okhttp3.*
import java.io.File
import java.io.IOException

class NetworkHandler {
    private var serverUrl = "test"
    public fun sendImageToServer(sessionId: String, filePath: String) {
        val client = OkHttpClient()

        // Create RequestBody for the image file
        val file = File(filePath)
        val fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file)

        // Create multipart body
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("sessionId", sessionId)
            .addFormDataPart("image", file.name, fileBody)
            .build()

        // Build the request
        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()

        // Asynchronously send the request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    // Handle error
                } else {
                    // Handle success
                    val responseBody = response.body()?.string()
                    // Do something with the response
                }
            }
        })
    }

    private fun handleResponse(){

    }
}