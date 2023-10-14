package se.helagro.postmessenger.taskhistory

import se.helagro.postmessenger.taskitem.Task

class TaskHistory() : ArrayList<Task>() {

    private val listeners = ArrayList<TaskHistoryListener>()

    fun addListener(listener: TaskHistoryListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: TaskHistoryListener) {
        listeners.remove(listener)
    }

    override fun add(element: Task): Boolean {
        val result = super.add(element)

        for (listener in listeners) {
            listener.onPostHistoryAdd()
        }
        return result
    }

    override fun clear() {
        super.clear()
        alertListeners()
    }

    private fun alertListeners() {
        for (listener in listeners) {
            listener.onPostHistoryUpdate()
        }
    }

    fun alertItemUpdate(pos: Int) {
        for (listener in listeners) {
            listener.onItemUpdate(pos)
        }
    }
}