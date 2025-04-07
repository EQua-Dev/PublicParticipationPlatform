package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls.createpoll

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollOption

sealed class CreatePollEvent {
    data class QuestionChanged(val text: String) : CreatePollEvent()
    data class PolicySelected(val policy: Policy) : CreatePollEvent()
    data class ExpiryDaysChanged(val days: Int) : CreatePollEvent()
    data class OptionChanged(val index: Int, val option: PollOption) : CreatePollEvent()
    object AddOption : CreatePollEvent()
    data class RemoveOption(val index: Int) : CreatePollEvent()
    object Submit : CreatePollEvent()
    object LoadPolicies : CreatePollEvent()
    object DismissError : CreatePollEvent()
}