package ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLLS_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class PollsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PollsRepository {

    override suspend fun getAllPolls(): List<Poll> {
        return try {
            firestore.collection(POLLS_REF)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Poll::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch polls: ${e.message}")
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
        onUpdate: (List<Poll>) -> Unit
    ): ListenerRegistration {
        return firestore.collection(POLLS_REF)
            .whereEqualTo("policyId", policyId)
            .addSnapshotListener { snapshot, _ ->
                val polls = snapshot?.toObjects(Poll::class.java) ?: emptyList()
                onUpdate(polls)
            }
    }
}