package se.helagro.postmessenger.taskitem

import android.util.Log

class Task(val text: String) {
    companion object{
        val INITIAL_STATUS = TaskStatus.LOADING
        val PROJECT_REGEX = Regex("#(\\w+)")
        val PRIORITY_REGEX = Regex("p([1-4])( |\$)")
    }

    var status = INITIAL_STATUS

    private val project: String? = PROJECT_REGEX.find(text)?.groupValues?.get(1)
    private val priority: Int? = PRIORITY_REGEX.find(text)?.groupValues?.get(1)
        ?.let { 5 - Integer.parseInt(it) }

    private val content: String = text
        .replace(PROJECT_REGEX, "")
        .replace(PRIORITY_REGEX, "")
        .trim()


    init {

        Log.d(
            "taskitem.Task",
            "Headphones $project AND $content AND $text AND $priority " +
                ""
        )
    }


    override fun toString(): String {
        return text
    }


    fun toJSON(): String {
        val json = StringBuilder()
        json.append("{")

        json.append("\"content\":\"${content}\"")
        project?.let { json.append(",\"content\":\"$project\"") }
        priority?.let { json.append(",\"priority\":\"$priority\"") }


        json.append("}")
        return json.toString()
    }
}