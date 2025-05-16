package ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo

import android.util.Log
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo.AnnouncementRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.translateTextWithMLKit
import java.util.UUID
import javax.inject.Inject

class PolicyRepositoryImpl @Inject constructor(
    private val blockChainRepository: BlockChainRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PolicyRepository {

   override fun getAllPolicies(language: AppLanguage): Flow<List<Policy>> = callbackFlow {
       val targetLang = when (language) {
           AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
           AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
           else -> TranslateLanguage.ENGLISH
       }
        val listenerRegistration = firestore.collection(POLICIES_REF)
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(Exception("Failed to listen for policy changes: ${error.message}", error))
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val originalPolicies = snapshot.toObjects(Policy::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        val translatedPolicies = originalPolicies.map { policy ->
                            Log.d("PolicyRepository", "getAllPolicies: $policy")
                            translatePolicyToLanguage(policy, targetLang)
                        }
                        Log.d("Translate", "getPolicyListener: $language$translatedPolicies")
                        trySend(translatedPolicies).isSuccess
                    }
                } else {
                    trySend(emptyList()).isSuccess
                }
            }

        awaitClose {
            listenerRegistration.remove()
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

        firestore.collection(POLICIES_REF).document(policyId)
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
        language: AppLanguage,
        onUpdate: (Policy?) -> Unit
    ): ListenerRegistration {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }

        return firestore.collection(POLICIES_REF)
            .document(policyId)
            .addSnapshotListener { snapshot, _ ->
                val policy = snapshot?.toObject(Policy::class.java)
                if (policy != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val translatedPolicy = translatePolicyToLanguage(policy, targetLang)
                        Log.d("Translate", "getPolicyListener: $language$translatedPolicy")
                        withContext(Dispatchers.Main) {
                            onUpdate(translatedPolicy)
                        }
                    }
                } else {
                    onUpdate(null)
                }
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

    private suspend fun translatePolicyToLanguage(policy: Policy, targetLang: String): Policy {
        return policy.copy(
            policyName = translateTextWithMLKit(policy.policyName, targetLang),
            policyTitle = translateTextWithMLKit(policy.policyTitle, targetLang),
            policySector = translateTextWithMLKit(policy.policySector, targetLang),
            policyDescription = translateTextWithMLKit(policy.policyDescription, targetLang),
            statusHistory = policy.statusHistory.map {
                it.copy(
                    notes = translateTextWithMLKit(it.notes, targetLang)
                )
            }
        )
    }


}