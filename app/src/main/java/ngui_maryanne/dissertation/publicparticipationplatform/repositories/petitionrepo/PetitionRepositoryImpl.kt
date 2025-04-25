package ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Signature
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.PETITIONS_REF

class PetitionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PetitionRepository
{

    override fun getAllPetitionsListener(onUpdate: (List<Petition>) -> Unit): ListenerRegistration {
        return firestore.collection(PETITIONS_REF)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }
                val petitions = snapshot.toObjects(Petition::class.java)
                onUpdate(petitions)
            }
    }

    override suspend fun createPetition(petition: Petition) {
        firestore.collection(PETITIONS_REF)
            .document(petition.id)
            .set(petition)
    }

    override fun getPetitionById(id: String): Flow<Petition?> = callbackFlow {
        val listenerRegistration = firestore.collection(PETITIONS_REF)
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val petition = snapshot?.toObject(Petition::class.java)
                trySend(petition)
            }

        awaitClose { listenerRegistration.remove() }
    }


    override suspend fun signPetition(petitionId: String, updatedSignatures: MutableList<Signature>) {
        try {
            firestore.collection(PETITIONS_REF)
                .document(petitionId)
                .update("signatures", updatedSignatures)
                .await() // suspends until complete
        } catch (e: Exception) {
            throw e // Can be handled in the ViewModel or use Result wrapper if preferred
        }
    }

}
