package ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo

import android.util.Log
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLLS_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetResponse
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollResponses
import ngui_maryanne.dissertation.publicparticipationplatform.di.TranslatorProvider
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.BUDGETS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.toTargetLang
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.translateTextWithMLKit
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class PollsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val blockChainRepository: BlockChainRepository,
    private val translatorProvider: TranslatorProvider
) : PollsRepository {

    override fun getAllPolls(language: AppLanguage): Flow<List<Poll>> = callbackFlow {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }
        val listenerRegistration = firestore.collection(POLLS_REF)
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(Exception("Failed to listen for poll changes: ${error.message}", error))
                    return@addSnapshotListener
                }
                val originalPolls = snapshot?.toObjects(Poll::class.java) ?: emptyList()
                if (snapshot != null && !snapshot.isEmpty) {
//                    trySend(originalPolls)
                    CoroutineScope(Dispatchers.IO).launch {
                        val translatedPolls = originalPolls.map { poll ->
                            translatePollToLanguage(poll, targetLang)
                        }
                        Log.d("Translate", "getPolicyListener: $language$translatedPolls")
                        trySend(translatedPolls)
                    }
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun createPoll(poll: Poll) {
        try {
            // Generate a unique ID if not provided
            val pollId = poll.id.ifEmpty { UUID.randomUUID().toString() }

            // Add createdBy and timestamp if empty
            val completePoll = poll.copy(
                id = pollId,
                createdBy = auth.currentUser?.uid ?: "",
                dateCreated = System.currentTimeMillis().toString()
            )

            firestore.collection(POLLS_REF)
                .document(pollId)
                .set(completePoll)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to create poll: ${e.message}")
        }
    }

    override fun getPollsListener(
        policyId: String,
        language: AppLanguage,
        onUpdate: (List<Poll>) -> Unit
    ): ListenerRegistration {
        val targetLang = language.toTargetLang()

        return firestore.collection(POLLS_REF)
            .whereEqualTo("policyId", policyId)
            .addSnapshotListener { snapshot, _ ->
                val originalPolls = snapshot?.toObjects(Poll::class.java) ?: emptyList()
                CoroutineScope(Dispatchers.IO).launch {
                    val translatedPolls = originalPolls.map { poll ->
                        translatePollToLanguage(poll, targetLang)
                    }
                    withContext(Dispatchers.Main) {
                        onUpdate(translatedPolls)
                    }
                }
            }
    }

    override suspend fun getPollsForPolicy(policyId: String, language: AppLanguage): Flow<List<Poll>> = callbackFlow {
        val targetLang = language.toTargetLang()

        val listener = firestore.collection(POLLS_REF)
            .whereEqualTo("policyId", policyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val originalPolls = snapshot?.toObjects(Poll::class.java) ?: emptyList()
                CoroutineScope(Dispatchers.IO).launch {
                    val translatedPolls = originalPolls.map { poll ->
                        translatePollToLanguage(poll, targetLang)
                    }
                    trySend(translatedPolls)
                }
            }

        awaitClose { listener.remove() }
    }


    override fun getAllPollsListener(language: AppLanguage, onUpdate: (List<Poll>) -> Unit): ListenerRegistration {
        val targetLang = language.toTargetLang()

        return firestore.collection(POLLS_REF)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val originalPolls = snapshot?.toObjects(Poll::class.java) ?: emptyList()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val deferredTranslations = originalPolls.map { poll ->
                            async { translatePollToLanguage(poll, targetLang) }
                        }

                        val translatedPolls = deferredTranslations.awaitAll()
                        Log.d("Polls Repo", "getAllPollsListener: $translatedPolls")

                        withContext(Dispatchers.Main) {
                            onUpdate(translatedPolls)
                        }
                    } catch (e: Exception) {
                        // handle error or send empty list
                        withContext(Dispatchers.Main) {
                            onUpdate(emptyList())
                        }
                    }
                }
            }
    }

    override suspend fun getPolicySnapshot(policyId: String): Policy? {
        return try {
            val snapshot = firestore.collection(POLICIES_REF)
                .document(policyId)
                .get()
                .await()
            snapshot.toObject(Policy::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPollById(pollId: String, language: AppLanguage): Poll? {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }

        return try {
            val snapshot = firestore.collection(POLLS_REF)
                .document(pollId)
                .get()
                .await()

            snapshot.toObject(Poll::class.java)?.let { poll ->
                val translatedPoll = translatePollToLanguage(poll, targetLang)
                Log.d("Translate", "getPollById: $language $translatedPoll")
                translatedPoll
            }
        } catch (e: Exception) {
            null
        }
    }


    override suspend fun voteForPollOption(pollId: String, updatedResponses: MutableList<PollResponses>) {
        try {
            // Add vote to Firestore
//            val voteData = mapOf("optionId" to optionId, "timestamp" to FieldValue.serverTimestamp())
            firestore.collection(POLLS_REF)
                .document(pollId)
                .update("responses", updatedResponses)
                .addOnSuccessListener { blockChainRepository.createBlockchainTransaction(
                    TransactionTypes.VOTE_ON_POLL) }
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to vote for poll option: ${e.message}")
        }
    }


    suspend fun translateText(text: String, sourceLang: String, targetLang: String): String {
        val translator = translatorProvider.getTranslator(sourceLang, targetLang)
        return suspendCancellableCoroutine { cont ->
            translator.translate(text)
                .addOnSuccessListener { cont.resume(it) {} }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
    }

    suspend fun translatePollToLanguage(poll: Poll, targetLang: String): Poll {
        val sourceLang = if (targetLang == TranslateLanguage.ENGLISH) {
            TranslateLanguage.SWAHILI
        } else {
            TranslateLanguage.ENGLISH
        }

        Log.d("translatePollToLanguage", "$targetLang $poll")
        return poll.copy(
            pollQuestion = translateText(poll.pollQuestion, sourceLang, targetLang),
            pollOptions = poll.pollOptions.map { option ->
                option.copy(
                    optionText = translateTextWithMLKit(option.optionText, targetLang),
                    optionExplanation = translateTextWithMLKit(option.optionExplanation, targetLang)
                )
            }
        )
    }

}