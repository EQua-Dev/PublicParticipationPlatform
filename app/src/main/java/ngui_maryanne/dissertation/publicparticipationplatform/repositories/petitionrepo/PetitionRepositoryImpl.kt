package ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetOption
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Signature
import ngui_maryanne.dissertation.publicparticipationplatform.di.TranslatorProvider
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.PETITIONS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LocationUtils
import kotlin.coroutines.resumeWithException

class PetitionRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val blockChainRepository: BlockChainRepository,
    private val translatorProvider: TranslatorProvider
) : PetitionRepository
{

    override fun getAllPetitionsListener(language: AppLanguage, onUpdate: (List<Petition>) -> Unit): ListenerRegistration {

        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }

        return firestore.collection(PETITIONS_REF)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val originalPetitions = snapshot?.documents?.mapNotNull {
                    it.toObject(Petition::class.java)?.copy(id = it.id)
                } ?: emptyList()

                CoroutineScope(Dispatchers.IO).launch {
                    val translatedPetitions = originalPetitions.map { budget ->
                        translatePetitionToLanguage(budget, targetLang)
                    }
                    onUpdate(translatedPetitions)
                }

//                val petitions = snapshot.toObjects(Petition::class.java)

            }
    }

    override suspend fun createPetition(petition: Petition) {
        firestore.collection(PETITIONS_REF)
            .document(petition.id)
            .set(petition)
            .addOnSuccessListener { blockChainRepository.createBlockchainTransaction(
                TransactionTypes.CREATE_PETITION) }

    }

    override fun getPetitionById(id: String, language: AppLanguage): Flow<Petition?> = callbackFlow {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }

        val listenerRegistration = firestore.collection(PETITIONS_REF)
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val originalPetition = snapshot?.toObject(Petition::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    val translatedPetition = originalPetition?.let {
                        translatePetitionToLanguage(it, targetLang)
                    }
                    trySend(translatedPetition)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }


    override suspend fun updatePetition(petitionId: String, name: String, imageUrl: String?, otherDetails: Map<String, Any?>) {
        val updates = mapOf(
            "title" to name,
            "coverImage" to imageUrl,
        ) + otherDetails

        firestore.collection(PETITIONS_REF).document(petitionId)
            .update(updates).addOnSuccessListener { blockChainRepository.createBlockchainTransaction(
                TransactionTypes.UPDATE_PETITION) }
    }

    override suspend fun deletePetition(petitionId: String) {
        firestore.collection(PETITIONS_REF).document(petitionId)
            .delete().addOnSuccessListener { blockChainRepository.createBlockchainTransaction(
                TransactionTypes.DELETE_POLICY) }
    }

    override suspend fun signPetition(petitionId: String, updatedSignatures: MutableList<Signature>) {
        try {
            firestore.collection(PETITIONS_REF)
                .document(petitionId)
                .update("signatures", updatedSignatures)
                .addOnSuccessListener { blockChainRepository.createBlockchainTransaction(
                    TransactionTypes.SIGN_PETITION) }
                .await() // suspends until complete

        } catch (e: Exception) {
            throw e // Can be handled in the ViewModel or use Result wrapper if preferred
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

    suspend fun translatePetitionToLanguage(petition: Petition, targetLang: String): Petition {
        val sourceLang = if (targetLang == TranslateLanguage.ENGLISH) {
            TranslateLanguage.SWAHILI
        } else {
            TranslateLanguage.ENGLISH
        }

        Log.d("translatePollToLanguage", "$targetLang $petition")
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

        return petition.copy(
            title = translateText(petition.title, sourceLang, targetLang),
            description = translateText(petition.description, sourceLang, targetLang),
            sector = translateText(petition.sector, sourceLang, targetLang),
            requestGoals = petition.requestGoals.map { option ->
                translateText(option, sourceLang, targetLang)
            },
        )
    }

    private suspend fun translateBudgetOptionToLanguage(
        option: BudgetOption,
        sourceLang: String,
        targetLang: String
    ): BudgetOption {
        return option.copy(
            optionProjectName = translateText(option.optionProjectName, sourceLang, targetLang),
            optionDescription = translateText(option.optionDescription, sourceLang, targetLang),
            // Don't translate these as they contain IDs, amounts (numbers), and URLs
            optionAssociatedPolicy = option.optionAssociatedPolicy,
            optionAmount = option.optionAmount,
            imageUrl = option.imageUrl
        )
    }


}
