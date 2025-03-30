package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import android.net.Uri

interface StorageRepository {
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): String
    suspend fun uploadPolicyImage(imageUri: Uri): String

}