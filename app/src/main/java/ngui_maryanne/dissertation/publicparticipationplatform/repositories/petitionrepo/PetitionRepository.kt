package ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo

import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Signature
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage

interface PetitionRepository {
    fun getAllPetitionsListener(language: AppLanguage, onUpdate: (List<Petition>) -> Unit): ListenerRegistration
    suspend fun createPetition(petition: Petition)
    fun getPetitionById(id: String, language: AppLanguage): Flow<Petition?>
    suspend fun signPetition(petitionId: String,  updatedSignatures: MutableList<Signature>)
    suspend fun updatePetition(petitionId: String, name: String, imageUrl: String? = null, otherDetails: Map<String, Any?>)
    suspend fun deletePetition(petitionId: String)
}
