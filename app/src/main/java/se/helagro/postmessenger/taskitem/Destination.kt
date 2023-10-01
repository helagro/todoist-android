package se.helagro.postmessenger.taskitem

import com.google.gson.annotations.SerializedName

data class Destination(
    val sectionID: String?,
    @SerializedName("id") val projectID: String,
    val name: String
)
