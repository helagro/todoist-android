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
                if(code != 200 || body == null) return

                parseProjects(body)

                Log.d("TAG", body + "anyway")
            }
        })

        networkHandler.getSections(object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if(code != 200 || body == null) return

                Log.d("TAG", body + "anyway")
            }
        })
    }


    fun parseProjects(body: String){
        destinations.addAll(
            Gson().fromJson(body, Array<Destination>::class.java)
        )

        destinations.forEach {Log.d("TAG", it.toString())}
    }

}