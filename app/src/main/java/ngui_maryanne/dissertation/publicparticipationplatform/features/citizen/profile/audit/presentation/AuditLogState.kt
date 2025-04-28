package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.audit.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AuditLog

data class CitizenAuditLogState(
    val logs: List<CitizenAuditLogUIModel> = emptyList(),
    val discrepancyFound: Boolean = false,
    val showDiscrepancyDialog: Boolean = false
)

data class CitizenAuditLogUIModel(
    val log: AuditLog,
    val revealedName: String? = null,
    val userType: String? = null,
    val profileImage: String? = null
)
