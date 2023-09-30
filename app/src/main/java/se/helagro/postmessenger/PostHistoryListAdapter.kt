package se.helagro.postmessenger

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.R.drawable.ic_mtrl_checked_circle
import se.helagro.postmessenger.network.NetworkHandler
import se.helagro.postmessenger.network.NetworkHandlerListener
import se.helagro.postmessenger.posthistory.PostHistory
import se.helagro.postmessenger.posthistory.PostHistoryListener
import se.helagro.postmessenger.postitem.PostItem
import se.helagro.postmessenger.postitem.PostItemStatus


class PostHistoryListAdapter(val activity: Activity, private val postHistory: PostHistory) :
    ArrayAdapter<PostItem>(activity, -1, postHistory), PostHistoryListener, NetworkHandlerListener {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val postItem = postHistory[position]
        val listItem = inflater.inflate(R.layout.listitem_post, null)
        val textView: TextView = listItem.findViewById(R.id.postLogListText)
        textView.text = postItem.msg


        //=========== STATUS BUTTON ===========

        val statusBtn: ImageButton = listItem.findViewById(R.id.postLogListImgBtn)
        statusBtn.setOnClickListener{
            val networkHandler  = NetworkHandler(NetworkHandler.getEndpoint()!!)
            networkHandler .sendMessage(postItem, this)
        }
        when(postItem.status){
            PostItemStatus.SUCCESS -> {
                statusBtn.setImageResource(ic_mtrl_checked_circle)
                statusBtn.clearColorFilter()
                statusBtn.isEnabled = false
            }
            PostItemStatus.LOADING -> {
                statusBtn.setImageResource(android.R.color.transparent)
                statusBtn.isEnabled = false
            }
            PostItemStatus.FAILURE -> {
                statusBtn.setImageResource(android.R.drawable.stat_notify_sync_noanim)
                statusBtn.setColorFilter(Color.argb(255, 255, 0, 0))
                statusBtn.isEnabled = true
            }
        }

        return listItem
    }

    override fun onPostHistoryUpdate() {
        activity.runOnUiThread {
            notifyDataSetChanged()
        }
    }

    override fun onPostItemUpdate(code: Int) {
        postHistory.alertListeners()
    }
}