package ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AuditLog
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.AUDIT_LOGS_REF
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class BlockChainRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore):
    BlockChainRepository {


    override fun createBlockchainTransaction(
        createdById: String,
        transactionType: TransactionTypes,
//        blockchainRef: CollectionReference
    ) {
        val blockchainRef = firestore.collection(AUDIT_LOGS_REF)
        firestore.collection(AUDIT_LOGS_REF).orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val previousHash = snapshot.documents.firstOrNull()?.getString("hash") ?: "0"
                val transaction = AuditLog(
                    createdBy = createdById,
                    previousHash = previousHash,
                    transactionType = transactionType.name
                ).copy(hash = AuditLog().computeHash())

                blockchainRef.add(transaction)
            }
    }

}