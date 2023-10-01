package se.helagro.postmessenger.taskitem

import android.util.Log
import com.google.gson.Gson
import se.helagro.postmessenger.network.NetworkCallback
import se.helagro.postmessenger.network.NetworkHandler

class Destinations(networkHandler: NetworkHandler) {
    val destinations = ArrayList<Destination>()


    init {
        networkHandler.getProjects(object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if(code == 200 && body != null) parseProjects(body)
            }
        })

        networkHandler.getSections(object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if(code == 200 && body != null) parseSections(body)
            }
        })
    }


    fun parseProjects(body: String){
        destinations.addAll(
            Gson().fromJson(body, Array<Destination>::class.java)
        )

        destinations.forEach{Log.d("TAG", it.toString())}
    }


    fun parseSections(body: String){
        data class Section(
            val id: String,
            val project_id: String,
            val name: String
        )
        val sections = Gson().fromJson(body, Array<Section>::class.java)

        destinations.addAll(
            sections.map { Destination(it.id, it.project_id, it.name) }
        )

        destinations.forEach{Log.d("TAG", it.toString())}
    }

}