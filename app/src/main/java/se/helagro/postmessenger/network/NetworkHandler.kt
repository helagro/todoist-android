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



    fun postTask(task: Task) {
        thread {
            val response = makeRequest(
                task.toJSON(),
                "https://api.todoist.com/rest/v2/tasks",
                "POST"
            )

            if(response.first == 200) task.status = TaskStatus.SUCCESS
            else task.status = TaskStatus.FAILURE
        }
    }


    fun getProjects(callback: NetworkCallback){
        thread {
            val response = makeRequest(
                null,
                "https://api.todoist.com/rest/v2/projects",
                "GET"
            )

            callback.onUpdate(response.first, response.second)
        }
    }


    fun getSections(callback: NetworkCallback){
        thread {
            val response = makeRequest(
                null,
                "https://api.todoist.com/rest/v2/sections",
                "GET"
            )

            callback.onUpdate(response.first, response.second)
        }
    }


    private fun makeRequest(reqBody: String?, endpoint: String, method: String): Pair<Int, String?> {
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
            return Pair(conn.responseCode, resBody)

        } catch (e: Exception) {
            val details = errorDetails(conn, reqBody, e)
            Log.e(TAG, details)
            return Pair(-1, null)
        } finally {
            try {
                conn?.disconnect()
            } catch (e: Exception) {
                return Pair(-1, null)
            }
        }
    }


    private fun errorDetails(conn: HttpURLConnection?, reqBody: String?, error: java.lang.Exception): String{
        val builder = StringBuilder()
        builder.appendLine(conn?.requestMethod)
        builder.appendLine(reqBody)

        builder.appendLine(error.stackTraceToString())
        builder.appendLine(conn?.responseCode)
        builder.appendLine(conn?.responseMessage)

        try {
            builder.appendLine(readBody(conn))
        } catch(_: Exception){}

        return builder.toString()
    }


    @Throws
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