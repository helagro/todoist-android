package se.helagro.postmessenger.taskhistory

interface TaskHistoryListener {
    fun onPostHistoryUpdate()
    fun onPostHistoryAdd()
    fun onItemUpdate(pos: Int)
}