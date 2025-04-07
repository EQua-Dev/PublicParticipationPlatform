package ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import com.google.firebase.firestore.ListenerRegistration

interface PollsRepository {
    suspend fun getAllPolls(): List<Poll>
    suspend fun createPoll(poll: Poll)
    fun getPollsListener(policyId: String, onUpdate: (List<Poll>) -> Unit): ListenerRegistration
}