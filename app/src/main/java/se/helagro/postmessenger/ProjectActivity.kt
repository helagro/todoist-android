package se.helagro.postmessenger

import android.os.Bundle
import android.util.Log
import android.view.Menu
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
    //-------------------------------<  VARIABLES  >----------------------------

    // CONSTANTS
    private val TAG = "ProjectActivity"

    // OBJECTS
    private val networkHandler = NetworkHandler()
    private var binding: ActivityProjectBinding? = null

    // TASKS
    private var tasks: Array<DownloadedTask>? = null
    private var taskI = -1

    // OTHER
    private var isAdding = false

    //-------------------------------<  INIT  >----------------------------

    init {
        networkHandler.getProject("inbox", object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if (code != 200) alert("Failed to get project: $code")
                else {
                    tasks = Gson().fromJson(body, Array<DownloadedTask>::class.java)

                    runOnUiThread {
                        nextTask()
                    }
                }
            }
        })
    }

    //-------------------------------<  LIFECYCLE  >----------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProjectBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding!!.sendBtn.setOnClickListener {
            if (isAdding) {
                addTask()
                binding!!.inputField.setText(tasks!![taskI].content)
                isAdding = false
            } else {
                updateTask()
                nextTask()
            }
        }

        binding!!.doneBtn.setOnClickListener {
            closeTask()
            nextTask()
        }

        if (tasks != null && tasks!!.isNotEmpty()) binding!!.inputField.setText(tasks!![0].content)
    }

    //---------------------------------<  TASKS  >------------------------------

    private fun addTask() {
        if (tasks == null) return
        if (binding == null) return

        val content = binding!!.inputField.text.toString().trim()
        val task = Task(content)

        networkHandler.postTask(task)
    }

    private fun updateTask() {
        if (tasks == null) return
        if (binding == null) return
        if (taskI >= tasks!!.size) return

        val content = binding!!.inputField.text.toString().trim()
        val task = tasks!![taskI]

        Log.v(TAG, "$task, $content")
        if (content == task.toString()) {
            Log.v(TAG, "No changes to task \"$content\"")
            return
        }

        val newTask = Task(content)

        networkHandler.updateTask(task.id, newTask, object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if (code != 200) alert("Failed to update task: $code")
                else if (TaskProject.exists(content)) moveTask(task, content)
            }
        })
    }

    private fun moveTask(task: DownloadedTask, content: String) {
        val destinationIDs = TaskProject.getDestinationIDs(content) ?: run {
            return alert("Failed to move task: Could not find destination")
        }

        var projectID: String? = destinationIDs.first
        if (destinationIDs.second != null) projectID = null

        networkHandler.move(
            task.id,
            projectID,
            destinationIDs.second,
            object : NetworkCallback {
                override fun onUpdate(code: Int, body: String?) {
                    if (code == 200) Log.v(
                        TAG,
                        "Moved task \"$content\" to " +
                                "project \"${destinationIDs.first}\", " +
                                "section \"${destinationIDs.second}\""
                    )
                    else alert("Failed to move task: $code")
                }
            })
    }

    private fun closeTask() {
        if (tasks == null) return

        networkHandler.closeTask(tasks!![taskI].id, object : NetworkCallback {
            override fun onUpdate(code: Int, body: String?) {
                if (code == 200 || code == 204) Log.v(TAG, "Closed task")
                else alert("Failed to close task: $code")
            }
        })
    }

    private fun nextTask() {
        if (tasks == null) return alert("No tasks")
        if (binding == null) return
        if (taskI == tasks!!.size - 1) return alert("No more tasks")

        taskI++
        binding!!.inboxCount.text = "${taskI + 1}/${tasks!!.size}"
        val task = tasks!![taskI]

        val text = "$task "

        binding!!.inputField.setText(text)
        binding!!.inputField.setSelection(text.length)
    }

    //----------------------------------<  MENU  >------------------------------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_project_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        if (item.itemId == R.id.action_add) {
            isAdding = true
            binding!!.inputField.setText("")
        }

        return super.onOptionsItemSelected(item)
    }

    //--------------------------------<  HELPERS  >-----------------------------

    private fun alert(msg: String?) {
        if (msg == null) return

        Log.v(TAG, msg)
        runOnUiThread {
            Toast.makeText(
                this@ProjectActivity,
                msg,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}