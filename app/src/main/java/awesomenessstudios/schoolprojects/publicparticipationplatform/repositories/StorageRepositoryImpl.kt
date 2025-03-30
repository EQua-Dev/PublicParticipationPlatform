package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
): StorageRepository {

    override suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        return try {
            val storageRef = storage.reference.child("profile_images/$userId.jpg")
            storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload image: ${e.message}")
        }
    }

    override suspend fun uploadPolicyImage(imageUri: Uri): String {
        return try {
            val storageRef = storage.reference.child("policy_images/${UUID.randomUUID()}.jpg")
            storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload policy image: ${e.message}")
        }
    }

}