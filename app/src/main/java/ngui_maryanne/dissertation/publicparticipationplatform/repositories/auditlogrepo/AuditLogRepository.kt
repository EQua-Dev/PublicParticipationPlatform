package ngui_maryanne.dissertation.publicparticipationplatform.repositories.auditlogrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AuditLog

interface AuditLogRepository {
    fun getAuditLogsRealtime(onResult: (List<AuditLog>) -> Unit)
    suspend fun getUserDetails(userId: String): Triple<String, String, String> // fullName, userType
}
