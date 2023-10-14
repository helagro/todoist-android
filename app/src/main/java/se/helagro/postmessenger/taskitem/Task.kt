package se.helagro.postmessenger.taskitem

class Task(val text: String) {
    companion object {
        val INITIAL_STATUS = TaskStatus.LOADING
        val PRIORITY_REGEX = Regex("( |^)p([1-4])( |\$)")
        val LABEL_REGEX = Regex("( |^)\\?(\\S+)")
    }

    var status = INITIAL_STATUS


    // ================= TASK PROPERTIES ===================

    private val priority: Int? = PRIORITY_REGEX.find(text)?.groupValues?.get(2)
        ?.let { 5 - Integer.parseInt(it) }

    private val labels: List<String> =
        LABEL_REGEX.findAll(text).toList().map { match -> match.groupValues[2] }

    private val content: String

    init {
        var content = text
        content = TaskDate.consume(content)
        content = TaskProject.consume(content)
        content = content.replace(PRIORITY_REGEX, "")
        content = content.replace(LABEL_REGEX, "")
        content = content.replace("\"", "\\\"")
        this.content = content.trim()
    }

    // ================= METHODS ===================

    override fun toString(): String {
        return text
    }

    fun toJSON(): String {
        val json = StringBuilder()
        json.append("{")

        json.append("\"content\":\"${content}\"")
        priority?.let { json.append(",\"priority\":\"$priority\"") }

        TaskDate.addJSON(json, text)
        TaskProject.addJSON(json, text)

        if (labels.isNotEmpty()) {
            val labelsStr = labels.joinToString(",", transform = { str -> "\"$str\"" })
            json.append(",\"labels\":[$labelsStr]")
        }

        json.append("}")

        return json.toString()
    }
}