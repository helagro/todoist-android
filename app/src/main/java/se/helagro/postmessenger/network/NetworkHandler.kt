package se.helagro.postmessenger.network

import android.util.Log
import se.helagro.postmessenger.taskitem.Task
import se.helagro.postmessenger.taskitem.TaskStatus
import se.helagro.postmessenger.settings.SettingsID
import se.helagro.postmessenger.settings.StorageHandler
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread


class NetworkHandler() {

    companion object{
        private const val ENDPOINT = "https://api.todoist.com/rest/v2/tasks"
        private const val REQUEST_METHOD = "POST"
        private const val CONNECT_TIMEOUT = 7000 // in milliseconds
    }

    private val apiKey: String = StorageHandler.getInstance().getString(SettingsID.API_KEY) ?: ""

    fun sendMessage(task: Task, listener: NetworkHandlerListener) {
        thread{
            val responseCode = makeRequest(task.toJSON())

            if(responseCode == 200) task.status = TaskStatus.SUCCESS
            else task.status = TaskStatus.FAILURE

            listener.onPostItemUpdate(responseCode)
        }
    }

    private fun makeRequest(body: String): Int {
        var conn: HttpURLConnection? = null
        var reader: BufferedReader? = null

        try {
            conn = URL(ENDPOINT).openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_TIMEOUT
            conn.requestMethod = REQUEST_METHOD
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("X-Request-Id", java.util.UUID.randomUUID().toString())
            conn.doOutput = true

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(body)
            writer.flush()

            reader = BufferedReader(InputStreamReader(conn.inputStream)) //nothing works without this!
            return conn.responseCode
        } catch (e: Exception) {
            val errorBuilder = StringBuilder()
            errorBuilder.appendLine(conn?.requestMethod)
            errorBuilder.appendLine(body)

            errorBuilder.appendLine(e.stackTraceToString())
            errorBuilder.appendLine(conn?.responseCode)
            errorBuilder.appendLine(conn?.responseMessage)

            Log.e("NetworkHandler", errorBuilder.toString())
            return -1
        } finally {
            try {
                reader?.close()
                conn?.disconnect()
            } catch (e: Exception) {
                return -1
            }
        }
    }

    private fun getRequestData(msg: String): String{
        if(apiKey.isEmpty()) return msg
        return "&$apiKey=${URLEncoder.encode(msg, "UTF-8")}"
    }

}