package ngui_maryanne.dissertation.publicparticipationplatform.data.models

data class Budget(
    val id: String = "",
    val hash: String = "",
    val budgetNo: String = "",
    val amount: String = "",
    val budgetNote: String = "",
    val budgetOptions: List<BudgetOption> = listOf(),
    val responses: List<BudgetResponse> = listOf(),
    val createdBy: String = "",
    val dateCreated: String = "",
    val budgetExpiry: String = "",
    val impact: String = "",
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val isActive: Boolean = true

)

data class BudgetOption(
    val optionId: String = "",
    val optionProjectName: String = "",
    val optionDescription: String = "",
    val optionAssociatedPolicy: String = "",
    val optionAmount: String = "",
    val imageUrl: String = ""
)

data class BudgetResponse(
    val answerId: String = "",
    val answerHash: String = "",
    val userId: String = "",
    val optionId: String = "",
    val isAnonymous: String = "",
    val dateCreated: String = ""
)
