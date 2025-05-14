package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.officialdetail

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official

sealed class OfficialDetailEvent {
    data class LoadOfficial(val id: String) : OfficialDetailEvent()

    data class UpdateOfficial(val updatedOfficial: Official) : OfficialDetailEvent()
    object DeactivateOfficial : OfficialDetailEvent()
    object ActivateOfficial : OfficialDetailEvent()
    object OfficialUpdated : OfficialDetailEvent()
    object OfficialDeactivated : OfficialDetailEvent()
    object OfficialActivated : OfficialDetailEvent()
    data class ShowError(val message: String) : OfficialDetailEvent()
}