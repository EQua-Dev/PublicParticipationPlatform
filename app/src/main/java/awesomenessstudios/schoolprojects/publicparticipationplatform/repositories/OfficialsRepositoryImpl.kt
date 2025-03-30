package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import android.net.Uri
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Official
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.OFFICIALS_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OfficialsRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : OfficialsRepository {

    override suspend fun createOfficial(
        official: Official,
        profileImageUri: Uri?,
        onResult: (Boolean, String?) -> Unit
    ) {
        try {
            val password = "${official.firstName}${(0..9).random()}"

            val result = auth.createUserWithEmailAndPassword(official.email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")

            var imageUrl: String? = null
            profileImageUri?.let {
                val imageRef = storage.reference.child("profile_images/$userId.jpg")
                imageRef.putFile(it).await()
                imageUrl = imageRef.downloadUrl.await().toString()
            }

            val officialData = official.copy(id = userId, profileImageUrl = imageUrl, initialPassword = password)

            firestore.collection(OFFICIALS_REF).document(userId).set(officialData).await()
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
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update official: ${e.message}")
        }
    }
}
