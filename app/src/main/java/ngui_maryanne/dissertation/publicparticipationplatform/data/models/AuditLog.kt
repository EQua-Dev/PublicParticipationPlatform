package ngui_maryanne.dissertation.publicparticipationplatform.data.models

import java.util.UUID

data class AuditLog(
    val transactionId: String = UUID.randomUUID().toString(),
    val transactionType: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val previousHash: String = "",
    val hash: String = "",
    val createdBy: String = ""
) {
    fun computeHash(): String {
        return (transactionId + timestamp + previousHash + createdBy).hashCode().toString()
    }
}
