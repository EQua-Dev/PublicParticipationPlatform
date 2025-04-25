package ngui_maryanne.dissertation.publicparticipationplatform.data.models

data class Petition(
    val id: String = "",
    val hash: String = "",
    val petitionNo: String = "",
    val title: String = "",
    val coverImage: String = "",
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

// Data model extension for UI use
fun Petition.signaturesProgress(): Float {
    val signed = signatures.size.toFloat()
    val target = signatureGoal.toFloatOrNull() ?: return 0f
    return (signed / target).coerceIn(0f, 1f)
}

fun Petition.daysToExpiry(): Long {
    val expiry = expiryDate.toLongOrNull() ?: return 0L
    val diff = expiry - System.currentTimeMillis()
    return (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
}
