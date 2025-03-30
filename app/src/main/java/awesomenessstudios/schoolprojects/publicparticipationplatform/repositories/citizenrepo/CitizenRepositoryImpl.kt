package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.citizenrepo

import android.app.Activity
import android.net.Uri
import android.util.Log
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Citizen
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.storagerepo.StorageRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.REGISTERED_CITIZENS_REF
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

// CitizenRepositoryImpl.kt
class CitizenRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val storageRepository: StorageRepository
) : CitizenRepository {
    // Store verification ID for later use
    private var verificationId: String? = null

    override suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<Unit> {
        return try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification (e.g., SMS retriever)
                    val otp = credential.smsCode // Get the OTP from the credential
                    Log.d("OTP", "Auto-retrieved OTP: $otp")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("OTP", "Verification failed", e)
                    // Note: This callback runs on main thread, so we can't directly update state here
                    // Instead, we'll handle the error in the try-catch block
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@CitizenRepositoryImpl.verificationId = verificationId
                    Log.d("OTP", "OTP sent successfully")
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<Unit> {
        return try {
            // Implement OTP verification
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            auth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerCitizen(citizen: Citizen, imageUri: Uri?): Result<Unit> {
        return try {
            // First upload image if provided
            val imageUrl = imageUri?.let { uri ->
                storageRepository.uploadProfileImage(citizen.id, uri)
            }

            // Create citizen document with image URL
            val citizenWithImage = citizen.copy(
                profileImage = imageUrl ?: ""
            )

            // Save to Firestore
            firestore.collection(REGISTERED_CITIZENS_REF)
                .document(citizen.id)
                .set(citizenWithImage)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCitizenDetails(
        citizenId: String,
        details: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestore.collection(REGISTERED_CITIZENS_REF)
                .document(citizenId)
                .update(details)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /*   override suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
           return try {
               val storageRef = storage.reference.child("profile_images/$userId.jpg")
               val uploadTask = storageRef.putFile(imageUri).await()
               val downloadUrl = storageRef.downloadUrl.await().toString()
               Result.success(downloadUrl)
           } catch (e: Exception) {
               Result.failure(e)
           }
       }*/

    override suspend fun getAllCitizens(): List<Citizen> {
        return try {
            firestore.collection(REGISTERED_CITIZENS_REF)
                .get()
                .await()
                .toObjects(Citizen::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch citizens: ${e.message}")
        }
    }

    override suspend fun approveCitizen(citizenId: String) {
        try {
            firestore.collection(REGISTERED_CITIZENS_REF)
                .document(citizenId)
                .update("isApproved", true)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to approve citizen: ${e.message}")
        }
    }


    override fun getCitizenRealtime(
        citizenId: String,
        onUpdate: (Citizen?) -> Unit
    ): ListenerRegistration {
        return firestore.collection(REGISTERED_CITIZENS_REF)
            .document(citizenId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onUpdate(null)
                    return@addSnapshotListener
                }
                onUpdate(snapshot?.toObject(Citizen::class.java))
            }
    }

    override suspend fun getCitizen(citizenId: String): Result<Citizen> {
        return try {
            val snapshot = firestore.collection(REGISTERED_CITIZENS_REF)
                .document(citizenId)
                .get()
                .await()
            Result.success(
                snapshot.toObject(Citizen::class.java) ?: throw Exception("Citizen not found")
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }

}