package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import java.time.LocalDateTime

data class CitizenPetitionsUiState(
    val searchQuery: String = "",
    val selectedSector: String? = null,
    val currentUserRole: UserRole = UserRole.CITIZEN,
    val allPetitions: List<Petition> = emptyList(),
    val filteredPetitions: List<Petition> = emptyList(),
    val petitionsBySector: Map<String, List<Petition>> = emptyMap(),
    val availableSectors: Set<String> = emptySet(),
    val isCreatingNewPetition: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val lastUpdated: LocalDateTime? = null
)
