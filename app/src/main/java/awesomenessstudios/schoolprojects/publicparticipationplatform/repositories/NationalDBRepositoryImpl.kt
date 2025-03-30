package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import android.net.Uri
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.TransactionTypes
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.NationalCitizen
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.NATIONAL_DB_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NationalDBRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val blockChainRepository: BlockChainRepository
) : NationalDBRepository {
    override fun addCitizen(
        citizen: NationalCitizen,
        imageUri: Uri?,
        onResult: (Boolean, String?) -> Unit
    ) {
        val citizensRef = firestore.collection(NATIONAL_DB_REF)

        val documentRef = citizensRef.document()
        val citizenWithId = citizen.copy(id = documentRef.id)

        if (imageUri != null) {
            val imageRef = storage.reference.child("national_database/${documentRef.id}.jpg")
            imageRef.putFile(imageUri)
                .addOnSuccessListener { task ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val updatedCitizen = citizenWithId.copy(profileImageUrl = uri.toString())
                        documentRef.set(updatedCitizen)
                            .addOnSuccessListener {
                                blockChainRepository.createBlockchainTransaction(
                                    auth.currentUser!!.uid,
                                    TransactionTypes.CREATE_CITIZEN_RECORD,
                                )
                                onResult(true, null)
                            }
                            .addOnFailureListener { error -> onResult(false, error.message) }
                    }
                }
                .addOnFailureListener { error -> onResult(false, error.message) }
        } else {
            documentRef.set(citizenWithId)
                .addOnSuccessListener {
                    blockChainRepository.createBlockchainTransaction(
                        auth.currentUser!!.uid,
                        TransactionTypes.CREATE_CITIZEN_RECORD,
                    )
                    onResult(true, null)
                }
                .addOnFailureListener { error -> onResult(false, error.message) }
        }
    }

    override suspend fun getCitizenByNationalId(nationalId: String): NationalCitizen? {
        return try {
            firestore.collection(NATIONAL_DB_REF)
                .whereEqualTo("nationalId", nationalId)
                .limit(1)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.toObject(NationalCitizen::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch national citizen: ${e.message}")
        }
    }

}