package awesomenessstudios.schoolprojects.publicparticipationplatform.data.models

data class Comment(
    val id: String = "",
    val userId: String = "",
    val comment: String = "",
    val isAnonymous: Boolean = false,
    val dateCreated: String = ""
)