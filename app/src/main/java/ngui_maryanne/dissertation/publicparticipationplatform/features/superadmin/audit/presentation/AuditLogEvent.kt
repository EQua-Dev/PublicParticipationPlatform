package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation

sealed class SuperAdminAuditLogEvent {
    object LoadLogs : SuperAdminAuditLogEvent()
    data class RevealUser(val userId: String, val logId: String) : SuperAdminAuditLogEvent()
    object RunDiscrepancyCheck : SuperAdminAuditLogEvent()
    object DismissDiscrepancyDialog : SuperAdminAuditLogEvent()
}
