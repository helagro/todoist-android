package se.helagro.postmessenger.taskitem

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import se.helagro.postmessenger.network.NetworkCallback
import se.helagro.postmessenger.network.NetworkHandler

object Destinations {
    private val destinations = ArrayList<Destination>()


    fun load(networkHandler: NetworkHandler) {
        networkHandler.getProjects(object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if (code == 200 && body != null) parseProjects(body)
            }
        })

        networkHandler.getSections(object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if (code == 200 && body != null) parseSections(body)
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