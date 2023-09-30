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
import kotlin.concurrent.thread


class NetworkHandler() {

    companion object{
        private const val TAG = "NetworkHandler"
        private const val CONNECT_TIMEOUT = 7000 // in milliseconds
    }

    private val apiKey: String = StorageHandler.getInstance().getString(SettingsID.API_KEY) ?: ""

    fun postTask(task: Task, listener: NetworkHandlerListener) {
        thread {
            val responseCode = makeRequest(
                task.toJSON(),
                "https://api.todoist.com/rest/v2/tasks",
                "POST"
            )

            if(responseCode == 200) task.status = TaskStatus.SUCCESS
            else task.status = TaskStatus.FAILURE

            listener.onUpdate(responseCode)
        }
    }

    fun getProjects(listener: NetworkHandlerListener){
        thread {
            val responseCode = makeRequest(
                null,
                "https://api.todoist.com/rest/v2/projects",
                "GET"
            )
        }
    }

    private fun makeRequest(reqBody: String?, endpoint: String, method: String): Int {
        var conn: HttpURLConnection? = null

        try {
            conn = URL(endpoint).openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_TIMEOUT
            conn.requestMethod = method
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("X-Request-Id", java.util.UUID.randomUUID().toString())
            if(method == "POST") conn.doOutput = true

            reqBody?.let {
                conn.setRequestProperty("Content-Type", "application/json")

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(reqBody)
                writer.flush()
            }

            val resBody = readBody(conn)

            Log.v(TAG, conn.responseMessage + "feiaoiehafoiaehoi" + resBody)

            return conn.responseCode
        } catch (e: Exception) {
            val errorBuilder = StringBuilder()
            errorBuilder.appendLine(conn?.requestMethod)
            errorBuilder.appendLine(reqBody)

            errorBuilder.appendLine(e.stackTraceToString())
            errorBuilder.appendLine(conn?.responseCode)
            errorBuilder.appendLine(conn?.responseMessage)

            try {
                errorBuilder.appendLine(readBody(conn))
            } catch(_: Exception){}

            Log.e("NetworkHandler", errorBuilder.toString())
            return -1
        } finally {
            try {
                conn?.disconnect()
            } catch (e: Exception) {
                return -1
            }
        }
    }

    private fun readBody(conn: HttpURLConnection?): String{

        // return if null
        conn ?: run {
            return ""
        }

        val body = StringBuilder()
        val reader = BufferedReader(InputStreamReader(conn.inputStream)) //nothing works without this!
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            body.append(line).append("\n")
        }

        reader.close()

        return body.toString()
    }
}