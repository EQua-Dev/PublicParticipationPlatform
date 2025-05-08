package ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.OFFICIALS_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Common.mAuth
import javax.inject.Inject

class OfficialsRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val blockChainRepository: BlockChainRepository
) : OfficialsRepository {

    override suspend fun createOfficial(
        official: Official,
        profileImageUri: Uri?,
        onResult: (Boolean, String?) -> Unit
    ) {
        try {
//            val password = "${official.firstName}${(0..9).random()}"
            val password = "!Test1234"

            val result = auth.createUserWithEmailAndPassword(official.email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")

            var imageUrl: String? = null
            profileImageUri?.let {
                val imageRef = storage.reference.child("profile_images/$userId.jpg")
                imageRef.putFile(it).await()
                imageUrl = imageRef.downloadUrl.await().toString()
            }

            val officialData =
                official.copy(id = userId, profileImageUrl = imageUrl, initialPassword = password)

            firestore.collection(OFFICIALS_REF).document(userId).set(officialData)
                .addOnSuccessListener {
                    blockChainRepository.createBlockchainTransaction(
                        TransactionTypes.CREATE_OFFICIAL
                    )
                    auth.sendPasswordResetEmail(official.email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                            } else {

                            }
                        }
                }.await()
            onResult(true, null)
        } catch (e: Exception) {
            onResult(false, e.message)
        }
    }

    override fun getOfficialsRealtime(onResult: (List<Official>, String?) -> Unit) {
        firestore.collection(OFFICIALS_REF)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    onResult(emptyList(), exception.message)
                    return@addSnapshotListener
                }

                val officials = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Official::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                onResult(officials, null)
            }
    }

    override suspend fun getCurrentOfficial(): Official {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            firestore.collection(OFFICIALS_REF)
                .document(uid)
                .get()
                .await()
                .toObject(Official::class.java)
                ?: throw Exception("Official not found")
        } catch (e: Exception) {
            throw Exception("Failed to fetch official: ${e.message}")
        }
    }

    override suspend fun updateOfficial(official: Official) {
        try {
            firestore.collection(OFFICIALS_REF)
                .document(official.id)
                .set(official)
                .addOnSuccessListener {
                    blockChainRepository.createBlockchainTransaction(
                        TransactionTypes.UPDATE_PROFILE
                    )
                }
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update official: ${e.message}")
        }
    }


    override fun getOfficialByIdRealtime(officialId: String) = callbackFlow {
        val registration = FirebaseFirestore.getInstance()
            .collection(OFFICIALS_REF)
            .document(officialId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val official = snapshot?.toObject(Official::class.java)?.copy(id = snapshot.id)
                trySend(official)
            }

        awaitClose {
            registration.remove()
        }
    }
}
