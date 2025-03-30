package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.pollsrepo

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Poll
import com.google.firebase.firestore.ListenerRegistration

interface PollsRepository {
    suspend fun getAllPolls(): List<Poll>
    suspend fun createPoll(poll: Poll)
    fun getPollsListener(policyId: String, onUpdate: (List<Poll>) -> Unit): ListenerRegistration
}