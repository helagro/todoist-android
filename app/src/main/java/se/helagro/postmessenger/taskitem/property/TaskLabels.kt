package se.helagro.postmessenger.taskitem.property

class TaskLabels {
    companion object {
        private val LABEL_REGEX = Regex("( |^)\\?(\\S+)")

        fun consume(text: String): String {
            return text.replace(LABEL_REGEX, "")
        }

        fun addJSON(json: StringBuilder, text: String) {
            val labels: List<String> =
                LABEL_REGEX.findAll(text).toList().map { match -> match.groupValues[2] }
            if (labels.isNotEmpty()) {
                val labelsStr = labels.joinToString(",", transform = { str -> "\"$str\"" })
                json.append(",\"labels\":[$labelsStr]")
            }
        }
    }
}