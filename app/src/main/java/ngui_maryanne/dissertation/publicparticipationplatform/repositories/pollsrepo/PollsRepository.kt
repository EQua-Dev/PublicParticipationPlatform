package ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy

interface PollsRepository {
    suspend fun getAllPolls(): List<Poll>
    suspend fun createPoll(poll: Poll)
    fun getPollsListener(policyId: String, onUpdate: (List<Poll>) -> Unit): ListenerRegistration
    suspend fun getPollsForPolicy(policyId: String): Flow<List<Poll>>
    fun getAllPollsListener(onUpdate: (List<Poll>) -> Unit): ListenerRegistration
    suspend fun getPolicySnapshot(policyId: String): Policy?
    suspend fun getPollById(pollId: String): Poll?
}