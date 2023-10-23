package se.helagro.postmessenger.taskitem.property

import se.helagro.postmessenger.taskitem.Destinations

class TaskProject {
    companion object {
        private val PROJECT_REGEX = Regex("( |^):(\\S+)")

        fun consume(taskString: String): String {
            return taskString.replace(PROJECT_REGEX, "").trim()
        }

        fun exists(taskString: String): Boolean {
            return getProjectString(taskString) != null
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

        fun getDestinationIDs(taskString: String): Pair<String, String?>? {
            val projectString = getProjectString(taskString) ?: return null

            Destinations.get(projectString)?.let {
                return Pair(it.projectID, it.sectionID)
            }

            return null
        }

        private fun getProjectString(taskString: String): String? {
            return PROJECT_REGEX.find(taskString)?.groupValues?.get(2)
        }
    }
}