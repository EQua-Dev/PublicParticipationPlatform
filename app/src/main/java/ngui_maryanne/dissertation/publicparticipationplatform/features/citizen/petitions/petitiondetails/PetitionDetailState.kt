package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import java.time.LocalDateTime

data class PetitionDetailsState(
    val petition: Petition? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSigned: Boolean = false,
    val currentUserId: String = "",
    val currentUserRole: UserRole = UserRole.CITIZEN,
    val lastUpdated: LocalDateTime? = null
)