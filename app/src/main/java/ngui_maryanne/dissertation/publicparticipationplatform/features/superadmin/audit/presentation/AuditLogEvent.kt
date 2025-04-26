package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation

sealed class AuditLogEvent {
    object LoadLogs : AuditLogEvent()
    data class RevealUser(val userId: String, val index: Int) : AuditLogEvent()
    object RunDiscrepancyCheck : AuditLogEvent()
    object DismissDiscrepancyDialog : AuditLogEvent()
}
