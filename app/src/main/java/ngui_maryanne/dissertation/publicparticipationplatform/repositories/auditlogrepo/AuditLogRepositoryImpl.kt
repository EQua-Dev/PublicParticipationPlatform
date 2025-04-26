package ngui_maryanne.dissertation.publicparticipationplatform.repositories.auditlogrepo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AuditLog
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.AUDIT_LOGS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.OFFICIALS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.REGISTERED_CITIZENS_REF
import javax.inject.Inject

class AuditLogRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    AuditLogRepository {

    override fun getAuditLogsRealtime(onResult: (List<AuditLog>) -> Unit) {
        firestore.collection(AUDIT_LOGS_REF)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val logs = snapshot?.toObjects(AuditLog::class.java).orEmpty()
                onResult(logs)
            }
    }

    override suspend fun getUserDetails(userId: String): Triple<String, String, String> {
        val citizenSnap =
            firestore.collection(REGISTERED_CITIZENS_REF).document(userId).get().await()
        if (citizenSnap.exists()) {
            val citizen = citizenSnap.toObject(Citizen::class.java)
            return Triple(
                "Citizen",
                "${citizen?.firstName ?: ""} ${citizen?.lastName ?: ""}".trim(),
                citizen?.profileImage ?: ""
            )        }

        val officialSnap = firestore.collection(OFFICIALS_REF).document(userId).get().await()
        if (officialSnap.exists()) {
            val official = officialSnap.toObject(Official::class.java)
            return Triple(
                "Official",
                "${official?.firstName ?: ""} ${official?.lastName ?: ""}".trim(),
                official?.profileImageUrl ?: ""
            )
        }

        return Triple("Unknown", "Unknown" ,"Unknown")
    }
}
