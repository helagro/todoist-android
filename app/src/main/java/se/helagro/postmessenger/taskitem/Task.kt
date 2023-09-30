package se.helagro.postmessenger.taskitem

class Task(val text: String) {
    companion object{
        val INITIAL_STATUS = TaskStatus.LOADING
    }

    var status = INITIAL_STATUS

    override fun toString(): String {

        return text
    }

    fun toJSON(): String {
        val json = StringBuilder()
        json.append("{")

        json.append("\"content\":\"${text}\"")

        json.append("}")
        return json.toString()
    }
}