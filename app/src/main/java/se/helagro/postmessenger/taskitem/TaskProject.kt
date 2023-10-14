package se.helagro.postmessenger.taskitem

class TaskProject {
    companion object {
        private val PROJECT_REGEX = Regex("( |^):(\\S+)")

        fun consume(taskString: String): String {
            return taskString.replace(PROJECT_REGEX, "").trim()
        }

        fun addJSON(json: StringBuilder, taskString: String) {
            val project = getProjectString(taskString) ?: return

            Destinations.get(project)?.let {
                json.append(",\"project_id\":\"${it.projectID}\"")
                
                it.sectionID?.let {
                    json.append(",\"section_id\":\"$it\"")
                }
            }
        }

        private fun getProjectString(taskString: String): String? {
            return PROJECT_REGEX.find(taskString)?.groupValues?.get(2)
        }
    }
}