package se.helagro.postmessenger.network

import android.util.Log
import se.helagro.postmessenger.settings.SettingsID
import se.helagro.postmessenger.settings.StorageHandler
import se.helagro.postmessenger.taskitem.Task
import se.helagro.postmessenger.taskitem.TaskStatus
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class NetworkHandler() {

    companion object {
        private const val TAG = "NetworkHandler"
        private const val CONNECT_TIMEOUT = 7000 // in milliseconds
    }

    private val apiKey: String = StorageHandler.getInstance().getString(SettingsID.API_KEY) ?: ""

    fun postTask(task: Task) {
        thread {
            val response = makeRequest(
                task.toBody(),
                "https://api.todoist.com/sync/v9/quick/add",
                "POST"
            )

            if (response.first == 200) task.status = TaskStatus.SUCCESS
            else task.status = TaskStatus.FAILURE
        }
    }

    fun getProject(project: String, callback: NetworkCallback) {
        val filter = "!subtask%20%26%20%23${project}"
        thread {
            val response = makeRequest(
                null,
                "https://api.todoist.com/rest/v2/tasks?filter=${filter}",
                "GET"
            )

            Log.v(TAG, "Downloaded tasks; Code: ${response.first}")
            callback.onUpdate(response.first, response.second)
        }
    }

    fun closeTask(id: String, callback: NetworkCallback) {
        thread {
            val response = makeRequest(
                null,
                "https://api.todoist.com/rest/v2/tasks/$id/close",
                "POST"
            )

            callback.onUpdate(response.first, response.second)
        }
    }

    private fun makeRequest(
        reqBody: String?,
        endpoint: String,
        method: String
    ): Pair<Int, String?> {
        var conn: HttpURLConnection? = null

        try {
            conn = URL(endpoint).openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_TIMEOUT
            conn.requestMethod = method
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("X-Request-Id", java.util.UUID.randomUUID().toString())
            if (method == "POST") conn.doOutput = true

            reqBody?.let {
                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(reqBody)
                writer.flush()
            }

            val resBody = readBody(conn)
            return Pair(conn.responseCode, resBody)
        } catch (e: Exception) {
            val details = errorDetails(conn, reqBody, e)
            Log.e(TAG, details)
            return Pair(-1, null)
        } finally {
            try {
                conn?.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
            }
        }
    }

    private fun errorDetails(
        conn: HttpURLConnection?,
        reqBody: String?,
        error: java.lang.Exception
    ): String {
        val builder = StringBuilder()
        builder.appendLine(reqBody)
        builder.appendLine(error.stackTraceToString())

        safeAppend(builder) { conn?.requestMethod }
        safeAppend(builder) { conn?.responseCode }
        safeAppend(builder) { conn?.responseMessage }
        safeAppend(builder) { readBody(conn) }

        return builder.toString()
    }

    private fun safeAppend(builder: StringBuilder, operation: () -> Any?) {
        try {
            val result = operation.invoke()
            builder.appendLine(result)
        } catch (e: Exception) {
        }
    }

    @Throws
    private fun readBody(conn: HttpURLConnection?): String {

        // return if null
        conn ?: run {
            return ""
        }

        val body = StringBuilder()
        val reader =
            BufferedReader(InputStreamReader(conn.inputStream)) //nothing works without this!
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            body.append(line).append("\n")
        }

        reader.close()

        return body.toString()
    }
}