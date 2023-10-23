package se.helagro.postmessenger.taskitem

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import se.helagro.postmessenger.network.NetworkCallback
import se.helagro.postmessenger.network.NetworkHandler

object Destinations {
    private val TAG = Destinations.javaClass.name
    private val destinations = ArrayList<Destination>()


    fun load(networkHandler: NetworkHandler, errorCallback: (() -> Unit)) {
        networkHandler.getProjects(object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if (code == 200 && body != null) {
                    parseProjects(body)
                    Log.v(TAG, "Loaded projects, total destinations amt: ${destinations.size}")
                } else errorCallback()
            }
        })

        networkHandler.getSections(object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if (code == 200 && body != null) {
                    parseSections(body)
                    Log.v(TAG, "Loaded sections, total destinations amt: ${destinations.size}")
                } else errorCallback()
            }
        })
    }


    fun parseProjects(body: String) {
        synchronized(destinations) {
            destinations.addAll(
                Gson().fromJson(body, Array<Destination>::class.java)
            )
        }
    }


    fun parseSections(body: String) {
        data class Section(
            val id: String,
            @SerializedName("project_id") val projectID: String,
            val name: String
        )

        val sections = Gson().fromJson(body, Array<Section>::class.java)

        synchronized(destinations) {
            destinations.addAll(
                sections.map { Destination(it.id, it.projectID, it.name) }
            )
        }
    }


    // DESTINATIONS MIGHT NOT HAVE BEEN LOADED!
    fun get(name: String): Destination? {
        synchronized(destinations) {
            return destinations.find { it.name == name }
        }
    }
}