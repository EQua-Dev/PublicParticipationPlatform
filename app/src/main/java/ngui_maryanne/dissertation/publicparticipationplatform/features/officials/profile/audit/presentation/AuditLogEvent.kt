package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile.audit.presentation

sealed class OfficialAuditLogEvent {
    object LoadLogs : OfficialAuditLogEvent()
    data class RevealUser(val userId: String, val index: Int) : OfficialAuditLogEvent()
    object RunDiscrepancyCheck : OfficialAuditLogEvent()
    object DismissDiscrepancyDialog : OfficialAuditLogEvent()
}
