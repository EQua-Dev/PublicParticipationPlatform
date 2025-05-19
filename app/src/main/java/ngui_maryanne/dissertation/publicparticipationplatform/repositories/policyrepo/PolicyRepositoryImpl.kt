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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.di.TranslatorProvider
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLLS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.translateTextWithMLKit
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class PolicyRepositoryImpl @Inject constructor(
    private val blockChainRepository: BlockChainRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val translatorProvider: TranslatorProvider
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

    override suspend fun updatePolicy(
        policyId: String,
        name: String,
        imageUrl: String,
        otherDetails: Map<String, Any?>
    ) {
        val updates = mapOf(
            "policyTitle" to name,
            "policyCoverImage" to imageUrl,
        ) + otherDetails

        firestore.collection(POLICIES_REF).document(policyId)
            .update(updates).addOnSuccessListener {
                blockChainRepository.createBlockchainTransaction(
                    TransactionTypes.UPDATE_POLICY
                )
            }
    }

    override suspend fun deletePolicy(policyId: String) {
        firestore.collection(POLICIES_REF).document(policyId)
            .delete().addOnSuccessListener {
                blockChainRepository.createBlockchainTransaction(
                    TransactionTypes.DELETE_POLICY
                )
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


    override suspend fun getPublicPolicies(language: AppLanguage): Flow<List<Policy>> =
        callbackFlow {
            val targetLang = when (language) {
                AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
                AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
                else -> TranslateLanguage.ENGLISH
            }

            val listener = firestore.collection(POLICIES_REF)
                .whereIn("policyStatus", PolicyStatus.getPublicStages().map { it.name })
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val originalPolicies = snapshot?.toObjects(Policy::class.java) ?: emptyList()

                    if (snapshot != null && !snapshot.isEmpty) {
//                    trySend(originalPolls)
                        CoroutineScope(Dispatchers.IO).launch {
                            val translatedPolicies = originalPolicies.map { policy ->
                                translatePolicyToLanguage(policy, targetLang)
                            }
                            Log.d("Translate", "getPolicyListener: $language$translatedPolicies")
                            trySend(translatedPolicies)
                        }
                    } else {
                        trySend(emptyList())
                    }
                    /*   val policies = snapshot?.documents?.mapNotNull { document ->
                           document.toObject(Policy::class.java)
                       } ?: emptyList()

                       trySend(policies)*/
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

    override suspend fun getPolicy(policyId: String, language: AppLanguage): Flow<Policy?> = callbackFlow {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }

        val listener = firestore.collection(POLICIES_REF)
            .document(policyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }


                val originalPolicy = snapshot?.toObject(Policy::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    val translatedPolicy =
                        translatePolicyToLanguage(originalPolicy!!, targetLang)

                    Log.d("Translate", "getPolicyListener: $language$translatedPolicy")
                    trySend(translatedPolicy)
                }
              /*  snapshot.toObject(Poll::class.java)?.let { poll ->
                    val translatedPoll = translatePollToLanguage(poll, targetLang)
                    Log.d("Translate", "getPollById: $language $translatedPoll")
                    translatedPoll
                }
                trySend(policy)*/
            }

        awaitClose { listener.remove() }
    }



    /*
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
    */


    suspend fun translateText(text: String, sourceLang: String, targetLang: String): String {
        val translator = translatorProvider.getTranslator(sourceLang, targetLang)
        return suspendCancellableCoroutine { cont ->
            translator.translate(text)
                .addOnSuccessListener { cont.resume(it) {} }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
    }

    suspend fun translatePolicyToLanguage(policy: Policy, targetLang: String): Policy {
        val sourceLang = if (targetLang == TranslateLanguage.ENGLISH) {
            TranslateLanguage.SWAHILI
        } else {
            TranslateLanguage.ENGLISH
        }

        Log.d("translatePollToLanguage", "$targetLang $policy")
        /*  return poll.copy(
              pollQuestion = translateText(poll.pollQuestion, sourceLang, targetLang),
              pollOptions = poll.pollOptions.map { option ->
                  option.copy(
                      optionText = translateTextWithMLKit(option.optionText, targetLang),
                      optionExplanation = translateTextWithMLKit(option.optionExplanation, targetLang)
                  )
              }
          )
  */
        return policy.copy(
            policyName = translateText(policy.policyName, sourceLang, targetLang),
            policyTitle = translateText(policy.policyTitle, sourceLang, targetLang),
            policySector = translateText(policy.policySector, sourceLang, targetLang),
            policyDescription = translateText(policy.policyDescription, sourceLang, targetLang),
            statusHistory = policy.statusHistory.map {
                it.copy(
                    notes = translateText(it.notes, sourceLang, targetLang)
                )
            }
        )
    }


}