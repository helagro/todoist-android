package se.helagro.postmessenger.taskitem

import android.util.Log

class Task(val text: String) {
    companion object{
        val INITIAL_STATUS = TaskStatus.LOADING
        val PROJECT_REGEX = Regex("( |^):(\\S+)")
        val PRIORITY_REGEX = Regex("( |^)p([1-4])( |\$)")
        val LABEL_REGEX = Regex("( |^)\\?(\\S+)")
        val TOD_REGEX = Regex(" tod( |\$)")
        val TOM_REGEX = Regex(" tom( |\$)")
        val TIME_REGEX = Regex("( |^)([0-2]?[1-9]:[0-5][0-9])( |\$)")
        val IN_MIN_REGEX = Regex("( |^)(in \\d+ min)( |\$)")
        val IN_HOUR_REGEX = Regex("( |^)(in \\d+ hour)( |\$)")
    }

    var status = INITIAL_STATUS


    // ================= TASK PROPERTIES ===================

    private val project: String? = PROJECT_REGEX.find(text)?.groupValues?.get(2)

    private val priority: Int? = PRIORITY_REGEX.find(text)?.groupValues?.get(2)
        ?.let { 5 - Integer.parseInt(it) }

    private val labels: List<String> =
        LABEL_REGEX.findAll(text).toList().map { match -> match.groupValues[2] }

    private val dateStr: String? = when {
        text.contains(TOD_REGEX) -> {
            "tod"
        }
        text.contains(TOM_REGEX) -> {
            "tom"
        }
        text.contains(TIME_REGEX) -> {
            TIME_REGEX.find(text)?.groupValues?.get(2)
        }
        text.contains(IN_MIN_REGEX) -> {
            IN_MIN_REGEX.find(text)?.groupValues?.get(2)
        }
        text.contains(IN_HOUR_REGEX) -> {
            IN_HOUR_REGEX.find(text)?.groupValues?.get(2)
        }
        else -> {
            null
        }
    }

    private val content: String = text
        .replace(PROJECT_REGEX, "")
        .replace(PRIORITY_REGEX, "")
        .replace(LABEL_REGEX, "")
        .replace(TOD_REGEX, "")
        .replace(TOM_REGEX, "")
        .replace(TIME_REGEX, "")
        .replace(IN_MIN_REGEX, "")
        .replace(IN_HOUR_REGEX, "")
        .replace("\"", "\\\"")
        .trim()



    // ================= METHODS ===================

    override fun toString(): String {
        return text
    }

    fun toJSON(): String {
        val json = StringBuilder()
        json.append("{")

        json.append("\"content\":\"${content}\"")
        priority?.let { json.append(",\"priority\":\"$priority\"") }
        dateStr?.let { json.append(",\"due_string\":\"$dateStr\"") }

        project?.let {
            Destinations.get(project)?.let {
                json.append(",\"project_id\":\"${it.projectID}\"")
                it.sectionID?.let {
                    json.append(",\"section_id\":\"$it\"")
                }
            }
        }

        if (labels.isNotEmpty()) {
            val labelsStr = labels.joinToString(",", transform = { str -> "\"$str\""})
            json.append(",\"labels\":[$labelsStr]")
        }

        json.append("}")

        Log.d("TAG", json.toString())
        return json.toString()
    }
}