package se.helagro.postmessenger.taskitem

import se.helagro.postmessenger.taskitem.property.TaskContent
import se.helagro.postmessenger.taskitem.property.TaskDate
import se.helagro.postmessenger.taskitem.property.TaskLabels
import se.helagro.postmessenger.taskitem.property.TaskPriority
import se.helagro.postmessenger.taskitem.property.TaskProject

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

    // ================= METHODS ===================

    override fun toString(): String {
        return text
    }

    fun toJSON(): String {
        val json = StringBuilder()
        json.append("{")

        TaskContent.addJSON(json, text)
        TaskPriority.addJSON(json, text)
        TaskDate.addJSON(json, text)
        TaskProject.addJSON(json, text)
        TaskLabels.addJSON(json, text)

        json.append("}")

        return json.toString()
    }
}