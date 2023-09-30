package se.helagro.postmessenger.taskitem

class Task(val text: String) {
    companion object{
        val INITIAL_STATUS = TaskStatus.LOADING
        val projectRegex = Regex("#(\\w+)")
    }

    var status = INITIAL_STATUS

    private val content: String
    private val project: String?


    init {
        content = text

        project = projectRegex.find(content)?.groupValues?.get(1)
    }


    override fun toString(): String {
        return text
    }


    fun toJSON(): String {
        val json = StringBuilder()
        json.append("{")

        json.append("\"content\":\"${content}\"")
        project?.let { json.append("\"content\":\"${project}\"") }


        json.append("}")
        return json.toString()
    }
}