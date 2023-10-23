package se.helagro.postmessenger.taskitem

data class DownloadedTask(
    val id: String,
    val content: String,
    val due: String?,
    val priority: Int,
    val labels: Array<String>
)