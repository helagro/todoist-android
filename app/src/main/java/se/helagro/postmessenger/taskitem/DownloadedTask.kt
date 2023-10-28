package se.helagro.postmessenger.taskitem

data class DownloadedTask(
    val id: String,
    val content: String,
    val due: String?,
    val priority: Int,
    val labels: Array<String>
) {
    override fun toString(): String {
        var text = content
        val labelString = labels.joinToString(" ", transform = { str -> "?$str" })
        val priorityString = priority.let {
            if (it == 1) ""
            else
                "p${5 - it}"
        } ?: ""

        if (labelString.isNotEmpty()) text += " $labelString"
        if (priorityString.isNotEmpty()) text += " $priorityString"
        text += " "

        return text
    }
}