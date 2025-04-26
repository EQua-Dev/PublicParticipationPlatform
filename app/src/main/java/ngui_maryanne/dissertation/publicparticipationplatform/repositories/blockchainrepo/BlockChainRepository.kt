package ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes

interface BlockChainRepository {
    fun createBlockchainTransaction(
        transactionType: TransactionTypes,
//        blockchainRef: CollectionReference
    )
}