package ngui_maryanne.dissertation.publicparticipationplatform.data.models

import java.security.MessageDigest
import java.util.UUID

data class AuditLog(
    val transactionId: String = UUID.randomUUID().toString(),
    val transactionType: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val previousHash: String = "",
    val hash: String = "",
    val location: String = "",
    val createdBy: String = ""
) {
    fun computeHash(): String {
        val input = transactionId + timestamp + previousHash + createdBy
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
