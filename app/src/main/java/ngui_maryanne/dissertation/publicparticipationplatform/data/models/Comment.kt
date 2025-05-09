package ngui_maryanne.dissertation.publicparticipationplatform.data.models

data class Comment(
    val id: String = "",
    val userId: String = "",
    val comment: String = "",
    val anonymous: Boolean = false,
    val dateCreated: String = ""
)