package se.helagro.postmessenger

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import se.helagro.postmessenger.network.NetworkHandler
import se.helagro.postmessenger.network.NetworkCallback
import se.helagro.postmessenger.taskhistory.TaskHistory
import se.helagro.postmessenger.taskitem.Task

class InputFieldListener(
    private val networkHandler: NetworkHandler,
    private val taskHistory: TaskHistory
) : TextView.OnEditorActionListener, NetworkCallback {

    val TAG = InputFieldListener::class.java.name

    override fun onEditorAction(textView: TextView?, actionId: Int, p: KeyEvent?): Boolean {
        if (!isUserDone(actionId)) return false
        if (textView == null) return false

        val textInput = textView.text.toString()
        val newTask = Task(textInput)
        taskHistory.add(newTask)

        networkHandler.postTask(newTask, this)
        textView.text = ""
        return true
    }

    private fun isUserDone(actionId: Int): Boolean {
        return (actionId == EditorInfo.IME_ACTION_DONE) ||
                (actionId == EditorInfo.IME_NULL)
    }

    override fun onUpdate(code: Int, body: String?) {
        taskHistory.alertListeners()
    }
}