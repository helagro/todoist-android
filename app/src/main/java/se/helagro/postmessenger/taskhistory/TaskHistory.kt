package se.helagro.postmessenger.taskhistory

import se.helagro.postmessenger.taskitem.Task

class TaskHistory() : ArrayList<Task>() {

    private val listeners = ArrayList<TaskHistoryListener>()

    fun addListener(listener: TaskHistoryListener){
        listeners.add(listener)
    }

    fun removeListener(listener: TaskHistoryListener){
        listeners.remove(listener)
    }

    override fun add(element: Task): Boolean {
        val result = super.add(element)
        alertListeners()
        return result
    }

    fun alertListeners(){
        for(listener in listeners){
            listener.onPostHistoryUpdate()
        }
    }
}