package com.example.myapplication.network

import com.example.myapplication.data.ApiResponse
import com.example.myapplication.data.ArticleResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/*this function fetches the data from server and transfers to the main thread*/
fun asyncGetHttpRequest(
    endpoint: String,
    onSuccess: (ApiResponse<ArticleResponse>) -> Unit,
    onError: (Exception) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val url = URL(endpoint)
        val openedConnection = url.openConnection() as HttpURLConnection
        openedConnection.requestMethod = "GET"

        val responseCode = openedConnection.responseCode
        try {
            val reader = BufferedReader(InputStreamReader(openedConnection.inputStream))
            val response = reader.readText()
            val apiResponse = ApiResponse(
                responseCode,
                parseJson<ArticleResponse>(response)
            )
            print(response)
            reader.close()
            onSuccess(apiResponse)
        } catch (e: Exception) {
            onError(Exception("HTTP Request failed with response code $responseCode"))
        }
    }
}

private inline fun <reified T> parseJson(text: String): T =
    Gson().fromJson(text, T::class.java)

