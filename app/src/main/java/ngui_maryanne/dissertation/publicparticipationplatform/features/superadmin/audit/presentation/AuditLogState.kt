package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AuditLog

data class SuperAdminAuditLogState(
    val logs: List<SuperAdminAuditLogUIModel> = emptyList(),
    val discrepancyFound: Boolean = false,
    val discrepancies: List<String> = listOf(),
    val showDiscrepancyDialog: Boolean = false
)

data class SuperAdminAuditLogUIModel(
    val log: AuditLog,
    val revealedName: String? = null,
    val userType: String? = null,
    val profileImage: String? = null
)
