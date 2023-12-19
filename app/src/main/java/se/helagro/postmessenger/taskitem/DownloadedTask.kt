package se.helagro.postmessenger.taskitem

data class DownloadedTask(
    val id: String,
    val content: String,
    val priority: Int,
    val description: String,
    val labels: Array<String>
) {
    override fun toString(): String {
        var text = content
        val labelString = labels.joinToString(" ", transform = { str -> "!$str" })
        val priorityString = priorityToString()

        if (labelString.isNotEmpty()) text += " $labelString"
        if (priorityString.isNotEmpty()) text += " $priorityString"
        if (description.isNotEmpty()) text += " // $description"

        return text
    }

    private fun priorityToString(): String {
        return when (priority) {
            1 -> ""
            3 -> "p3"
            4 -> "p2"
            5 -> "p1"
            else -> throw Exception("Invalid priority: $priority")
        }
    }
}