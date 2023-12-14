package com.example.conexionapihuachitos

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiTask(private val callback: ApiCallback) {

    fun execute(urlString: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                performApiRequest(urlString)
            }
            callback.onRequestComplete(result)
        }
    }

    fun executePost(urlString: String, postData: Map<String, String>) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                performApiPostRequest(urlString,postData)
            }
            callback.onRequestComplete(result)
        }
    }


    private suspend fun performApiRequest(urlString: String): String {
        try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"

            return try {
                val responseCode = urlConnection.responseCode
                Log.i("ApiTask", "Response Code: $responseCode")

                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                response.toString()
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
    private suspend fun performApiPostRequest(urlString: String, postData: Map<String, String>): String {
        try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.doOutput = true

            return try {
                // Convert data to JSON string
                val jsonInputString = JSONObject(postData).toString()

                // Write data to the request body for POST
                val outputStream = urlConnection.outputStream
                outputStream.write(jsonInputString.toByteArray(Charsets.UTF_8))
                outputStream.close()

                val responseCode = urlConnection.responseCode
                Log.i("ApiTask", "Response Code: $responseCode")

                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                response.toString()
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}
