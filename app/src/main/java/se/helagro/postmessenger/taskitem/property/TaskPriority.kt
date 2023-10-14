package se.helagro.postmessenger.taskitem.property

class TaskPriority {
    companion object {
        private val PRIORITY_REGEX = Regex("( |^)p([1-4])( |\$)")

        fun consume(text: String): String {
            return text.replace(PRIORITY_REGEX, "")
        }

        fun addJSON(json: StringBuilder, text: String) {
            val priority: Int? = PRIORITY_REGEX.find(text)?.groupValues?.get(2)
                ?.let { 5 - Integer.parseInt(it) }
            priority?.let { json.append(",\"priority\":\"$priority\"") }
        }
    }
}