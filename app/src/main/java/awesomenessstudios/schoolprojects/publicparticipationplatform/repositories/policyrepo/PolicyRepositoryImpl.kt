package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.policyrepo

import android.util.Log
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.PolicyStatus
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.POLICIES_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PolicyRepositoryImpl @Inject constructor(
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
}