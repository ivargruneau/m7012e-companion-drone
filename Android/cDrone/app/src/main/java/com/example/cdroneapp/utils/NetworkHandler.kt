package com.example.cdroneapp.utils
import okhttp3.*
import java.io.File
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

class NetworkHandler {
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
            .url("YOUR_ENDPOINT_URL")
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
                    val responseBody = response.body?.string()
                    // Do something with the response
                }
            }
        })
    }
}

