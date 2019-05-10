package org.itsavesplanet.imagecollector.uploadres

import org.itsavesplanet.imagecollector.BuildConfig
import org.itsavesplanet.imagecollector.apis.ServerAPI
import java.io.File
//import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class AwsUploader(val session: String, val file: File) {
    private var uploadStatus = UploadStatus.NOT_INITIALIZED
    private var progress: Int? = null

    suspend fun upload(uploadStatusFun: (status: UploadStatus, fileSize: Long, bytesSent: Long) -> Int) {
        val fileSize = file.length()

        uploadStatusFun(UploadStatus.PENDING, fileSize, 0)

        val uri = requestUploadUrl()

        val http = uri.openConnection() as HttpsURLConnection
        http.setDoOutput(true)
        http.setRequestMethod("PUT")
        http.setRequestProperty("Content-Type", " ") // remove Content-Type header

        val os = http.getOutputStream()
        uploadStatusFun(UploadStatus.IN_PROGRESS, fileSize, 0)
        //        TODO:
        //        do assync
        //        os.write(_bytes)
        os.flush()
        os.close()

        uploadStatusFun(UploadStatus.FINISHED, fileSize, 0)
        uploadStatus = UploadStatus.FINISHED
    }

    fun requestUploadUrl(): URL {
        val BASE_URL = ServerAPI.UPLOAD_LINK_REQUEST_URL
        var url = ""
        return URL(url)
    }
}