package ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo

import com.google.firebase.auth.FirebaseAuth
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AuditLog
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.AUDIT_LOGS_REF
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LocationUtils
import javax.inject.Inject

class BlockChainRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val locationUtils: LocationUtils
) :
    BlockChainRepository {


    override fun createBlockchainTransaction(
        transactionType: TransactionTypes,
//        blockchainRef: CollectionReference
    ) {
        val blockchainRef = firestore.collection(AUDIT_LOGS_REF)
        firestore.collection(AUDIT_LOGS_REF).orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val previousHash = snapshot.documents.firstOrNull()?.getString("hash") ?: "0"
               /* val transaction = AuditLog(
                    createdBy = createdById,
                    previousHash = previousHash,
                    transactionType = transactionType.name
                ).copy(hash = AuditLog().computeHash())
*/

                try {
                    // Get the current location
                    locationUtils.getCurrentLocation()
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                val locationAddress = locationUtils.getLocationAddress(location)
                                // Proceed with wallet creation

                                val transaction = AuditLog(
                                    createdBy = auth.currentUser!!.uid,
                                    previousHash = previousHash,
                                    location = locationAddress,
                                    transactionType = transactionType.name
                                ).copy(hash = AuditLog().computeHash())

                                blockchainRef.add(transaction)
                            }else{

                                val transaction = AuditLog(
                                    createdBy = auth.currentUser!!.uid,
                                    previousHash = previousHash,
                                    location = "Unknown Location",
                                    transactionType = transactionType.name
                                ).copy(hash = AuditLog().computeHash())

                                blockchainRef.add(transaction)
                            }
                        }
                        .addOnFailureListener { e ->
                           throw e
                        }
                } catch (e: Exception) {
                    throw e
                }

            }
    }

}