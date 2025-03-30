package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.blockchainrepo

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.TransactionTypes

interface BlockChainRepository {
    fun createBlockchainTransaction(
        createdById: String,
        transactionType: TransactionTypes,
//        blockchainRef: CollectionReference
    )
}