package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets

import android.net.Uri

sealed class OfficialBudgetEvent {
    data class OnAmountChanged(val value: String) : OfficialBudgetEvent()
    data class OnNoteChanged(val value: String) : OfficialBudgetEvent()
    data class OnImpactChanged(val value: String) : OfficialBudgetEvent()
    data class OnBudgetOptionImageSelected(val index: Int, val uri: Uri) : OfficialBudgetEvent()
    data class OnBudgetOptionChanged(val index: Int, val option: BudgetOptionInput) : OfficialBudgetEvent()
    object OnAddBudgetOption : OfficialBudgetEvent()
    object SubmitBudget : OfficialBudgetEvent()
    object OnResetCreateState : OfficialBudgetEvent()
    object OnFabClicked : OfficialBudgetEvent()
    object OnNavigateDone : OfficialBudgetEvent() // resets the navigation flag
}
