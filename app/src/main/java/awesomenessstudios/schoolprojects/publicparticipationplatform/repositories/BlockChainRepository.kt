package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.TransactionTypes
import com.google.firebase.firestore.CollectionReference

interface BlockChainRepository {
    fun createBlockchainTransaction(
        createdById: String,
        transactionType: TransactionTypes,
//        blockchainRef: CollectionReference
    )
}