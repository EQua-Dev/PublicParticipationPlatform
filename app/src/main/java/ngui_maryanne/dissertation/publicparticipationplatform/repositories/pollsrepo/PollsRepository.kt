package ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollResponses
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage

interface PollsRepository {
    fun getAllPolls(language: AppLanguage): Flow<List<Poll>>
    suspend fun createPoll(poll: Poll)
    fun getPollsListener(policyId: String, language: AppLanguage, onUpdate: (List<Poll>) -> Unit): ListenerRegistration
    suspend fun getPollsForPolicy(policyId: String, language: AppLanguage): Flow<List<Poll>>
    fun getAllPollsListener(language: AppLanguage, onUpdate: (List<Poll>) -> Unit): ListenerRegistration
    suspend fun getPolicySnapshot(policyId: String, language: AppLanguage): Policy?
    suspend fun getPollById(pollId: String, language: AppLanguage): Poll?
    suspend fun voteForPollOption(pollId: String, updatedResponses: MutableList<PollResponses>)
}