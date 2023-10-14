package se.helagro.postmessenger.taskitem.property

class TaskDate() {
    companion object {
        private val TOD_REGEX = Regex(" tod( |\$)")
        private val TOM_REGEX = Regex(" tom( |\$)")
        private val TIME_REGEX = Regex("( |^)([0-2]?[1-9]:[0-5][0-9])( |\$)")
        private val IN_MIN_REGEX = Regex("( |^)(in \\d+ min)( |\$)")
        private val IN_HOUR_REGEX = Regex("( |^)(in \\d+ hour)( |\$)")

        fun consume(taskString: String): String {
            return taskString
                .replace(TOD_REGEX, "")
                .replace(TOM_REGEX, "")
                .replace(TIME_REGEX, "")
                .replace(IN_MIN_REGEX, "")
                .replace(IN_HOUR_REGEX, "")
                .trim()
        }

        fun addJSON(json: StringBuilder, taskString: String) {
            val dateStr = getDateStr(taskString) ?: return

            json.append(",\"due_string\":\"$dateStr\"")
        }

        private fun getDateStr(taskString: String): String? {
            when {
                taskString.contains(TOD_REGEX) -> {
                    return "tod"
                }

                taskString.contains(TOM_REGEX) -> {
                    return "tom"
                }

                taskString.contains(TIME_REGEX) -> {
                    return TIME_REGEX.find(taskString)?.groupValues?.get(2)
                }

                taskString.contains(IN_MIN_REGEX) -> {
                    return IN_MIN_REGEX.find(taskString)?.groupValues?.get(2)
                }

                taskString.contains(IN_HOUR_REGEX) -> {
                    return IN_HOUR_REGEX.find(taskString)?.groupValues?.get(2)
                }

                else -> {
                    return null
                }
            }
        }

    }

}