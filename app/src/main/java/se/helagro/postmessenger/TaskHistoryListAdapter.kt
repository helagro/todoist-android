package se.helagro.postmessenger

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R.drawable.ic_mtrl_checked_circle
import se.helagro.postmessenger.network.NetworkHandler
import se.helagro.postmessenger.taskhistory.TaskHistory
import se.helagro.postmessenger.taskhistory.TaskHistoryListener
import se.helagro.postmessenger.taskitem.TaskStatus


class TaskHistoryListAdapter(private val activity: Activity, private val taskHistory: TaskHistory) :
    RecyclerView.Adapter<TaskHistoryListAdapter.ViewHolder>(), TaskHistoryListener {


    // --------------------- RECYCLER VIEW ADAPTER METHODS ---------------------

    private val inflater: LayoutInflater = LayoutInflater.from(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.listitem_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postItem = taskHistory[position]
        postItem.onUpdateStatus = {
            activity.runOnUiThread {
                if (postItem.status == TaskStatus.SUCCESS) postItem.onUpdateStatus = null
                drawBtn(postItem.status, holder.statusBtn)
            }
        }

        holder.textView.text = postItem.text
        holder.statusBtn.setOnClickListener {
            NetworkHandler().postTask(postItem)
        }

        drawBtn(postItem.status, holder.statusBtn)
    }

    private fun drawBtn(status: TaskStatus, statusBtn: ImageButton) {
        when (status) {
            TaskStatus.SUCCESS -> {
                statusBtn.setImageResource(ic_mtrl_checked_circle)
                statusBtn.isEnabled = false
            }

            TaskStatus.LOADING -> {
                statusBtn.setImageResource(R.drawable.arrow_up)
                statusBtn.isEnabled = false
            }

            TaskStatus.FAILURE -> {
                statusBtn.setImageResource(R.drawable.retry)
                statusBtn.isEnabled = true
            }
        }
    }


    // --------------------- NETWORK CALLBACK METHODS ---------------------

    override fun onPostHistoryUpdate() {
        activity.runOnUiThread {
            notifyDataSetChanged()
        }
    }

    override fun onPostHistoryAdd() {
        activity.runOnUiThread {
            notifyItemInserted(taskHistory.size - 1)
        }
    }


    // --------------------- VIEW HOLDER ---------------------

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.postLogListText)
        val statusBtn: ImageButton = view.findViewById(R.id.postLogListImgBtn)
    }
}