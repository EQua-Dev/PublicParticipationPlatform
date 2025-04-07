package ngui_maryanne.dissertation.publicparticipationplatform.data.models

data class Citizen(
    val id: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val email: String = "",
    val nationalID: String = "",
    val phoneNumber: String = "",
    val profileImage: String = "",
    val occupation: String = "",
    val countyOfResidence: String = "",
    val countyOfBirth: String = "",
    val securityHash: String = "",
    val registrationLocation: String = "",
    val dateCreated: String = "",
    val approved: String = "false"
)
