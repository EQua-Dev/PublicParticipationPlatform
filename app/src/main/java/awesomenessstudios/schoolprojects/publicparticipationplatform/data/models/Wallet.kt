package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class Wallet(
    val id: String = "",
    val ownerId: String = "",
    val balance: String = "0.0",
    val dateCreated: String = "",
    val creationLocation: String = "",
    val securityHash: String = "" // Hash of the security questions and answers

)
