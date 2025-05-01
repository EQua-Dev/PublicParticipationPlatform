package ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo.AnnouncementRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import java.util.UUID
import javax.inject.Inject

class PolicyRepositoryImpl @Inject constructor(
    private val blockChainRepository: BlockChainRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PolicyRepository {

    override suspend fun getAllPolicies(): List<Policy> {
        return try {
            firestore.collection(POLICIES_REF)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Policy::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch policies: ${e.message}")
        }
    }

    override suspend fun createPolicy(policy: Policy) {
        try {
            firestore.collection(POLICIES_REF)
                .document(policy.id)
                .set(policy)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to create policy: ${e.message}")
        }
    }

    override suspend fun updatePolicy(policyId: String, name: String, imageUrl: String, otherDetails: Map<String, Any?>) {
        val updates = mapOf(
            "policyTitle" to name,
            "policyCoverImage" to imageUrl,
        ) + otherDetails

        firestore.collection("policies").document(policyId)
            .update(updates).addOnSuccessListener { blockChainRepository.createBlockchainTransaction(
                TransactionTypes.UPDATE_POLICY) }
    }

    override suspend fun deletePolicy(policyId: String) {
        firestore.collection(POLICIES_REF).document(policyId)
            .delete().addOnSuccessListener { blockChainRepository.createBlockchainTransaction(
                TransactionTypes.DELETE_POLICY) }
    }

    override suspend fun getPoliciesBeforePublicOpinion(): List<Policy> {
        return try {
            firestore.collection(POLICIES_REF)
                .whereIn(
                    "policyStatus",
                    listOf(
                        PolicyStatus.DRAFT.name,
                        PolicyStatus.INTERNAL_REVIEW.name,
                        PolicyStatus.MINISTERIAL_APPROVAL.name,
                        PolicyStatus.PUBLIC_CONSULTATION
                    )
                )
                .get()
                .await()
                .toObjects(Policy::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch policies: ${e.message}")
        }
    }

    override fun getPolicyListener(
        policyId: String,
        onUpdate: (Policy?) -> Unit
    ): ListenerRegistration {
        return firestore.collection(POLICIES_REF)
            .document(policyId)
            .addSnapshotListener { snapshot, _ ->
                onUpdate(snapshot?.toObject(Policy::class.java))
            }
    }

    override suspend fun updatePolicyStage(policyId: String, newStage: PolicyStatus) {
        try {
            val updateData = mapOf(
                "policyStatus" to newStage.name,
                "statusHistory" to FieldValue.arrayUnion(
                    mapOf(
                        "status" to newStage.name,
                        "changedAt" to System.currentTimeMillis().toString(),
                        "changedBy" to auth.currentUser!!.uid, // Replace with actual user
                        "notes" to "Stage advanced"
                    )
                )
            )

            firestore.collection(POLICIES_REF)
                .document(policyId)
                .update(updateData)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update policy stage: ${e.message}")
        }
    }

    override suspend fun getPublicPolicies(): Flow<List<Policy>> = callbackFlow {
        val listener = firestore.collection(POLICIES_REF)
            .whereIn("policyStatus", PolicyStatus.getPublicStages().map { it.name })
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val policies = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Policy::class.java)
                } ?: emptyList()

                trySend(policies)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun searchPolicies(query: String): Flow<List<Policy>> = callbackFlow {
        val listener = firestore.collection(POLICIES_REF)
            .whereIn("policyStatus", PolicyStatus.getPublicStages().map { it.name })
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val policies = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Policy::class.java)
                }?.filter { policy ->
                    policy.policyTitle.contains(query, ignoreCase = true) ||
                            policy.policyDescription.contains(query, ignoreCase = true) ||
                            policy.policySector.contains(query, ignoreCase = true)
                } ?: emptyList()

                trySend(policies)
            }

        awaitClose { listener.remove() }


    }

    override suspend fun getPolicy(policyId: String): Flow<Policy?> = callbackFlow {
        val listener = firestore.collection(POLICIES_REF)
            .document(policyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val policy = snapshot?.toObject(Policy::class.java)
                trySend(policy)
            }

        awaitClose { listener.remove() }
    }

}