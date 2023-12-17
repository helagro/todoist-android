package se.helagro.postmessenger.taskitem

class Task(val text: String) {
    companion object {
        val INITIAL_STATUS = TaskStatus.LOADING
    }

    var onUpdateStatus: ((TaskStatus) -> Unit)? = null

    var status = INITIAL_STATUS
        set(value) {
            field = value
            onUpdateStatus?.invoke(value)
        }

    //--------------------------------<  METHODS  >-----------------------------

    override fun toString(): String {
        return text
    }

    fun toBody(): String {
        var content = text.replace(":", "#")
        content = content.replace("!", "@")

        return "text=$content"
    }
}