package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.audit.presentation

sealed class CitizenAuditLogEvent {
    object LoadLogs : CitizenAuditLogEvent()
    data class RevealUser(val userId: String, val index: Int) : CitizenAuditLogEvent()
    object RunDiscrepancyCheck : CitizenAuditLogEvent()
    object DismissDiscrepancyDialog : CitizenAuditLogEvent()
}
