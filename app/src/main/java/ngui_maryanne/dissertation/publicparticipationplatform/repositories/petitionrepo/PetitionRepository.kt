package ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo

import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Signature

interface PetitionRepository {
    fun getAllPetitionsListener(onUpdate: (List<Petition>) -> Unit): ListenerRegistration
    suspend fun createPetition(petition: Petition)
    fun getPetitionById(id: String): Flow<Petition?>
    suspend fun signPetition(petitionId: String,  updatedSignatures: MutableList<Signature>)
}
