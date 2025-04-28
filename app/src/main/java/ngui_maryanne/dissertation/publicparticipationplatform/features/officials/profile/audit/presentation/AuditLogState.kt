package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile.audit.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AuditLog

data class OfficialAuditLogState(
    val logs: List<OfficialAuditLogUIModel> = emptyList(),
    val discrepancyFound: Boolean = false,
    val discrepancies: List<String> = listOf(),
    val showDiscrepancyDialog: Boolean = false
)

data class OfficialAuditLogUIModel(
    val log: AuditLog,
    val revealedName: String? = null,
    val userType: String? = null,
    val profileImage: String? = null
)
