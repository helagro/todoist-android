package se.helagro.postmessenger

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import se.helagro.postmessenger.databinding.ActivityProjectBinding
import se.helagro.postmessenger.network.NetworkCallback
import se.helagro.postmessenger.network.NetworkHandler
import se.helagro.postmessenger.taskitem.DownloadedTask
import se.helagro.postmessenger.taskitem.Task
import se.helagro.postmessenger.taskitem.property.TaskProject

class ProjectActivity() : AppCompatActivity() {
    private val networkHandler = NetworkHandler()
    private var tasks: Array<DownloadedTask>? = null
    private var taskI = -1
    private var binding: ActivityProjectBinding? = null
    private val TAG = "ProjectActivity"

    init {
        networkHandler.getProject("2302810404", object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                tasks = Gson().fromJson(body, Array<DownloadedTask>::class.java)

                tasks?.forEach { println(it) }
                runOnUiThread {
                    nextTask()
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProjectBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding!!.sendBtn.setOnClickListener {
            updateTask()
            nextTask()
        }

        if (tasks != null && tasks!!.isNotEmpty()) binding!!.inputField.setText(tasks!![0].content)
    }

    private fun updateTask() {
        if (tasks == null) return
        if (binding == null) return
        if (taskI >= tasks!!.size) return

        val content = binding!!.inputField.text.toString().trim()
        val task = tasks!![taskI]

        val newTask = Task(content)

        networkHandler.updateTask(task.id, newTask, object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if (code != 200) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ProjectActivity,
                            "Failed to update task: $code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                if (TaskProject.exists(content)) moveTask(task, content)
            }
        })
    }

    private fun moveTask(task: DownloadedTask, content: String) {
        val destinationIDs = TaskProject.getDestinationIDs(content) ?: run {
            runOnUiThread {
                Toast.makeText(
                    this@ProjectActivity,
                    "Failed to move task: Could not find destination",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Log.v(TAG, "Failed to move task: Could not find destination")
            return
        }

        networkHandler.move(
            task.id,
            destinationIDs.first,
            destinationIDs.second,
            object : NetworkCallback {
                override fun onUpdate(code: Int, body: String?) {
                    if (code == 200) Log.v(TAG, "Moved task \"$content\"")
                    else {
                        runOnUiThread {
                            Toast.makeText(
                                this@ProjectActivity,
                                "Failed to move task: $code",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
    }

    private fun nextTask() {
        if (tasks == null) return
        if (binding == null) return
        if (taskI == tasks!!.size - 1) return

        taskI++
        val task = tasks!![taskI]

        var text = task.content
        val labelString = task.labels.joinToString(" ", transform = { str -> "?$str" })
        val priorityString = task.priority.let {
            if (it == 1) ""
            else
                "p${5 - it}"
        } ?: ""

        if (labelString.isNotEmpty()) text += " $labelString"
        if (priorityString.isNotEmpty()) text += " $priorityString"
        text += " "

        binding!!.inputField.setText(text)
        binding!!.inputField.setSelection(text.length)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()

        return super.onOptionsItemSelected(item)
    }
}