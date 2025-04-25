package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition

import android.net.Uri

data class NewPetitionState(
    val sector: String = "",
    val title: String = "",
    val description: String = "",
    val county: String = "",
    val coverImageUri: Uri? = null,
    val requestGoals: List<String> = listOf(""),
    val supportingReasons: List<String> = listOf(""),
    val targetSignatures: Int = 200,
    val isLoading: Boolean = false,
    val error: String? = null
)

