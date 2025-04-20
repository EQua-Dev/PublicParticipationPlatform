package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition

data class CitizenPetitionsUiState(
    val searchQuery: String = "",
    val allPetitions: List<Petition> = emptyList(),
    val petitionsBySector: Map<String, List<Petition>> = emptyMap(),
    val isCreatingNewPetition: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
