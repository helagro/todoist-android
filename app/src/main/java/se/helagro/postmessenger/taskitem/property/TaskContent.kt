package se.helagro.postmessenger.taskitem.property

class TaskContent {
    companion object {
        fun addJSON(json: StringBuilder, text: String) {
            val content = this.getContentString(text)

            json.append("\"content\":\"${content}\"")
        }

        private fun getContentString(text: String): String {
            var content = TaskDate.consume(text)
            content = TaskProject.consume(content)
            content = TaskLabels.consume(content)
            content = TaskPriority.consume(content)
            content = content.replace("\"", "\\\"")
            content = content.replace("\n", "\\n")
            return content.trim()
        }
    }
}