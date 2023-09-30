package se.helagro.postmessenger.network

import se.helagro.postmessenger.taskitem.Task
import se.helagro.postmessenger.taskitem.TaskStatus
import se.helagro.postmessenger.settings.SettingsID
import se.helagro.postmessenger.settings.StorageHandler
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread


class NetworkHandler() {

    companion object{
        private val ENDPOINT = "https://api.todoist.com/rest/v2/tasks"
        private val REQUEST_METHOD = "POST"
        private val CONNECT_TIMEOUT = 7000 // in milliseconds

        fun getEndpoint(): String?{
            val storageHandler = StorageHandler.getInstance()
            return storageHandler.getString(SettingsID.API_KEY)
        }
    }

    private val apiKey: String = StorageHandler.getInstance().getString(SettingsID.API_KEY)!!

    fun sendMessage(task: Task, listener: NetworkHandlerListener) {
        thread{
            val responseCode = makeRequest(task.text)

            if(responseCode == 200) task.status = TaskStatus.SUCCESS
            else task.status = TaskStatus.FAILURE

            listener.onPostItemUpdate(responseCode)
        }
    }

    private fun makeRequest(msg: String): Int {
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        val data = getRequestData(msg)

        try {
            connection = URL(ENDPOINT).openConnection() as HttpURLConnection
            connection.connectTimeout = CONNECT_TIMEOUT
            connection.requestMethod = REQUEST_METHOD
            connection.setRequestProperty("Content-Type", "text/plain")
            connection.doOutput = true

            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(data)
            writer.flush()

            reader = BufferedReader(InputStreamReader(connection.inputStream)) //nothing works without this!
            return connection.responseCode
        } catch (e: Exception) {
            return -1
        } finally {
            try {
                reader?.close()
                connection?.disconnect()
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