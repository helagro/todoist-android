package se.helagro.postmessenger.taskhistory

interface TaskHistoryListener {
    fun onPostHistoryUpdate()
    fun onPostHistoryAdd()
}