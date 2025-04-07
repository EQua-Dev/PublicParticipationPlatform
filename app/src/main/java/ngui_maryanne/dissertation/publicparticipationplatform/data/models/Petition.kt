package ngui_maryanne.dissertation.publicparticipationplatform.data.models

data class Petition(
    val id: String = "",
    val hash: String = "",
    val petitionNo: String = "",
    val title: String = "",
    val description: String = "",
    val county: String = "",
    val requestGoals: List<String> = listOf(),
    val signatureGoal: String = "",
    val sector: String = "",
    val createdBy: String = "",
    val expiryDate: String = "",
    val signatures: List<Signature> = listOf()
)

data class Signature(
    val id: String = "",
    val hash: String = "",
    val userId: String = "",
    val dateCreated: String = "",
    val isAnonymous: Boolean = false,

)
