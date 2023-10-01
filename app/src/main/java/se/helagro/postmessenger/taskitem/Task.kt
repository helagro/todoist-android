package se.helagro.postmessenger.taskitem

import android.util.Log

class Task(val text: String) {
    companion object{
        val INITIAL_STATUS = TaskStatus.LOADING
        val PROJECT_REGEX = Regex("#(\\w+)")
        val PRIORITY_REGEX = Regex("p([1-4])( |\$)")
        val TOD_REGEX = Regex(" tod( |\$)")
        val TOM_REGEX = Regex(" tom( |\$)")
    }

    var status = INITIAL_STATUS


    // ================= TASK PROPERTIES ===================

    private val project: String? = PROJECT_REGEX.find(text)?.groupValues?.get(1)

    private val priority: Int? = PRIORITY_REGEX.find(text)?.groupValues?.get(1)
        ?.let { 5 - Integer.parseInt(it) }

    private val dateStr: String? = when {
        text.contains(TOD_REGEX) -> {
            "tod"
        }
        text.contains(TOM_REGEX) -> {
            "tom"
        }
        else -> {
            null
        }
    }

    private val content: String = text
        .replace(PROJECT_REGEX, "")
        .replace(PRIORITY_REGEX, "")
        .replace(TOD_REGEX, "")
        .replace(TOM_REGEX, "")
        .trim()



    // ================= METHODS ===================

    override fun toString(): String {
        return text
    }

    fun toJSON(): String {
        val json = StringBuilder()
        json.append("{")

        json.append("\"content\":\"${content}\"")
        // project?.let { json.append(",\"content\":\"$project\"") }
        priority?.let { json.append(",\"priority\":\"$priority\"") }
        dateStr?.let { json.append(",\"due_string\":\"$dateStr\"") }

        json.append("}")

        Log.d("TAG", json.toString())
        return json.toString()
    }
}