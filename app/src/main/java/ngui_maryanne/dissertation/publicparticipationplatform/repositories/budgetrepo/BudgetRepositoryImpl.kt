package ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetResponse
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.di.TranslatorProvider
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.BUDGETS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.toTargetLang
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.translateTextWithMLKit
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class BudgetRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val blockChainRepository: BlockChainRepository,
    private val translatorProvider: TranslatorProvider
) :
    BudgetRepository {
    override suspend fun createBudget(budget: Budget) {
        firestore.collection(BUDGETS_REF)
            .document(budget.id)
            .set(budget)
            .addOnSuccessListener {
                blockChainRepository.createBlockchainTransaction(
                    TransactionTypes.CREATE_BUDGET
                )
            }
            .await()
    }

    override fun getAllBudgets(language: AppLanguage): Flow<List<Budget>> = callbackFlow {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }
        val collectionRef = firestore.collection(BUDGETS_REF)

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val originalBudgets = snapshot?.documents?.mapNotNull {
                it.toObject(Budget::class.java)?.copy(id = it.id)
            } ?: emptyList()

            CoroutineScope(Dispatchers.IO).launch {
                val translatedBudgets = originalBudgets.map { budget ->
                    translateBudgetToLanguage(budget, targetLang)
                }
                trySend(translatedBudgets)
            }
        }

        awaitClose { listener.remove() }
    }

    override fun getBudgetById(id: String, language: AppLanguage): Flow<Budget?> = callbackFlow {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }

        val docRef = firestore.collection(BUDGETS_REF).document(id)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val originalBudget = snapshot?.toObject(Budget::class.java)?.copy(id = snapshot.id)

            CoroutineScope(Dispatchers.IO).launch {
                val translatedBudget = originalBudget?.let {
                    translateBudgetToLanguage(it, targetLang)
                }
                trySend(translatedBudget)
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun submitBudgetResponse(response: BudgetResponse) {
        val budgetDoc = firestore.collection(BUDGETS_REF).document(response.answerId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(budgetDoc)
            val existing = snapshot.toObject(Budget::class.java)
            if (existing != null) {
                val updatedResponses = existing.responses + response
                transaction.update(budgetDoc, "responses", updatedResponses)
            }
        }.await()
    }

    override suspend fun toggleBudgetActivation(budgetId: String, isActive: Boolean) {
        try {
            firestore.collection(BUDGETS_REF)
                .document(budgetId)
                .update("isActive", isActive)  // Update the 'isActive' field to the new status
                .addOnSuccessListener {
                    blockChainRepository.createBlockchainTransaction(
                        TransactionTypes.TOGGLE_BUDGET_ACTIVATION
                    )
                }
                .await()
        } catch (e: Exception) {
            // Handle the exception
            throw Exception("Failed to toggle budget activation status: ${e.message}")
        }
    }


    override suspend fun voteForBudgetOption(
        budgetId: String,
        updatedResponses: MutableList<BudgetResponse>
    ) {
        try {
            // Add vote to Firestore
//            val voteData = mapOf("optionId" to optionId, "timestamp" to FieldValue.serverTimestamp())
            firestore.collection(BUDGETS_REF)
                .document(budgetId)
                .update("responses", updatedResponses)
                .addOnSuccessListener {
                    blockChainRepository.createBlockchainTransaction(
                        TransactionTypes.VOTE_ON_BUDGET
                    )
                }
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to vote for budget option: ${e.message}")
        }
    }

    override suspend fun updateBudgetDetails(budgetId: String, updatedFields: Map<String, Any>) {
        try {
            firestore.collection(BUDGETS_REF)
                .document(budgetId)
                .update(updatedFields)
                .addOnSuccessListener {
                    blockChainRepository.createBlockchainTransaction(TransactionTypes.EDIT_BUDGET)
                }
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update budget details: ${e.message}")
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

    suspend fun translateBudgetToLanguage(budget: Budget, targetLang: String): Budget {
        val sourceLang = if (targetLang == TranslateLanguage.ENGLISH) {
            TranslateLanguage.SWAHILI
        } else {
            TranslateLanguage.ENGLISH
        }

        Log.d("translatePollToLanguage", "$targetLang $budget")
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

        return budget.copy(
            budgetNote = translateText(budget.budgetNote, sourceLang, targetLang),
            impact = translateText(budget.impact, sourceLang, targetLang),
            budgetOptions = budget.budgetOptions.map { option ->
                translateBudgetOptionToLanguage(option, sourceLang, targetLang)
            },
            // Responses typically don't need translation as they contain user-generated content
            // and system-generated IDs/dates
            responses = budget.responses
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