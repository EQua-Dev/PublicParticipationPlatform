package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition

data class PetitionDetailsState(
    val petition: Petition? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSigned: Boolean = false,
    val currentUserId: String = ""
)