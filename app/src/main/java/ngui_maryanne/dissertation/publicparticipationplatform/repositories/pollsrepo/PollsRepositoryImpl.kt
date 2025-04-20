package ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLLS_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
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

    override suspend fun getPollsForPolicy(policyId: String): Flow<List<Poll>> = callbackFlow {
        val listener = firestore.collection(POLLS_REF)
            .whereEqualTo("policyId", policyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val polls = snapshot?.toObjects(Poll::class.java) ?: emptyList()
                trySend(polls)
            }

        awaitClose { listener.remove() }
    }


    override fun getAllPollsListener(onUpdate: (List<Poll>) -> Unit): ListenerRegistration {
        return firestore.collection(POLLS_REF)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val polls = snapshot?.toObjects(Poll::class.java) ?: emptyList()
                onUpdate(polls)
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

    override suspend fun getPollById(pollId: String): Poll? {
        return try {
            val snapshot = firestore.collection(POLLS_REF)
                .document(pollId)
                .get()
                .await()
            snapshot.toObject(Poll::class.java)
        } catch (e: Exception) {
            null
        }
    }

}