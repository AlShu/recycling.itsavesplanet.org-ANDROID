package org.itsavesplanet.imagecollector.apis

import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.Charsets.UTF_8

class ServerAPI(private val base_url: String) {
    val UPLOAD_LINK_REQUEST_URL = "upload_link_request"

    fun getUploadLink(fileName: String): String {

        val serverURL = "$base_url/$UPLOAD_LINK_REQUEST_URL"

        val url = URL(serverURL)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 300000
        connection.connectTimeout = 300000
        connection.doOutput = true

        val postData: ByteArray =
            JSONObject(
                mapOf(
                    "fileName" to fileName
                )
            )
                .toString()
                .toByteArray(UTF_8)

        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-length", postData.size.toString())
        connection.setRequestProperty("Content-Type", "application/json")

        try {
            val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()
            val reader: BufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
//            TODO: parse json, get url
            val output: String = reader.readLine()
            return ""
        } catch (exception: Exception) {

        }

        if (connection.responseCode != HttpURLConnection.HTTP_OK && connection.responseCode != HttpURLConnection.HTTP_CREATED) {
            try {

                println("There was error while connecting ")
                System.exit(0)

            } catch (exception: Exception) {
                throw Exception("Exception while ")
            }
        }
        return ""
    }
}