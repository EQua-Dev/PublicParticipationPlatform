package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.TransactionTypes
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.AuditLog
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.AUDIT_LOGS_REF
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class BlockChainRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore): BlockChainRepository {


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