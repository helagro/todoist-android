package se.helagro.postmessenger

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.R.drawable.ic_mtrl_checked_circle
import se.helagro.postmessenger.network.NetworkHandler
import se.helagro.postmessenger.network.NetworkHandlerListener
import se.helagro.postmessenger.taskhistory.TaskHistory
import se.helagro.postmessenger.taskhistory.TaskHistoryListener
import se.helagro.postmessenger.taskitem.Task
import se.helagro.postmessenger.taskitem.TaskStatus


class TaskHistoryListAdapter(val activity: Activity, private val taskHistory: TaskHistory) :
    ArrayAdapter<Task>(activity, -1, taskHistory), TaskHistoryListener, NetworkHandlerListener {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val postItem = taskHistory[position]
        val listItem = inflater.inflate(R.layout.listitem_post, null)
        val textView: TextView = listItem.findViewById(R.id.postLogListText)
        textView.text = postItem.text


        //=========== STATUS BUTTON ===========

        val statusBtn: ImageButton = listItem.findViewById(R.id.postLogListImgBtn)
        statusBtn.setOnClickListener{
            val networkHandler  = NetworkHandler()
            networkHandler .postTask(postItem, this)
        }
        when(postItem.status){
            TaskStatus.SUCCESS -> {
                statusBtn.setImageResource(ic_mtrl_checked_circle)
                statusBtn.clearColorFilter()
                statusBtn.isEnabled = false
            }
            TaskStatus.LOADING -> {
                statusBtn.setImageResource(android.R.color.transparent)
                statusBtn.isEnabled = false
            }
            TaskStatus.FAILURE -> {
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

    override fun onUpdate(code: Int) {
        taskHistory.alertListeners()
    }
}