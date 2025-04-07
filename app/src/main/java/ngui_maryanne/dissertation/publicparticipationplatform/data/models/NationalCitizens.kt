package ngui_maryanne.dissertation.publicparticipationplatform.data.models

data class NationalCitizen(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val nationalId: String = "",
    val profileImageUrl: String = "",
    val dateCreated: Long = System.currentTimeMillis(),
    val createdBy: String = ""
)
