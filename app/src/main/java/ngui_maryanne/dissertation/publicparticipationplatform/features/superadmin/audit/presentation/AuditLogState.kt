package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AuditLog

data class AuditLogState(
    val logs: List<AuditLogUIModel> = emptyList(),
    val discrepancyFound: Boolean = false,
    val showDiscrepancyDialog: Boolean = false
)

data class AuditLogUIModel(
    val log: AuditLog,
    val revealedName: String? = null,
    val userType: String? = null,
    val profileImage: String? = null
)
