package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class WalletHistory(
    val id: String = "",
    val walletId: String = "",
    val transactionType: String = "",
    val sender: String = "",
    val receiver: String = "",
    val walletOwner: String = "",
    val dateCreated: String = ""
)
