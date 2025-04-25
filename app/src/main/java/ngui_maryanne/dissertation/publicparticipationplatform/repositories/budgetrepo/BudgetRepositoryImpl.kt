package ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetResponse
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.BUDGETS_REF
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    BudgetRepository {
    override suspend fun createBudget(budget: Budget) {
        firestore.collection(BUDGETS_REF)
            .document(budget.id)
            .set(budget)
            .await()
    }

    override fun getAllBudgets(): Flow<List<Budget>> = callbackFlow {
        val collectionRef = firestore.collection(BUDGETS_REF)

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val budgets =
                snapshot?.documents?.mapNotNull { it.toObject(Budget::class.java) } ?: emptyList()
            trySend(budgets)
        }

        awaitClose { listener.remove() }
    }

    override fun getBudgetById(id: String): Flow<Budget?> = callbackFlow {
        val docRef = firestore.collection(BUDGETS_REF).document(id)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val budget = snapshot?.toObject(Budget::class.java)
            trySend(budget)
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
            firestore.collection("budgets")
                .document(budgetId)
                .update("isActive", isActive)  // Update the 'isActive' field to the new status
                .await()
        } catch (e: Exception) {
            // Handle the exception
            throw Exception("Failed to toggle budget activation status: ${e.message}")
        }
    }


    override suspend fun voteForBudgetOption(budgetId: String, optionVote: BudgetResponse) {
        try {
            // Add vote to Firestore
//            val voteData = mapOf("optionId" to optionId, "timestamp" to FieldValue.serverTimestamp())
            firestore.collection("budgets")
                .document(budgetId)
                .collection("votes")
                .add(optionVote)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to vote for budget option: ${e.message}")
        }
    }


}