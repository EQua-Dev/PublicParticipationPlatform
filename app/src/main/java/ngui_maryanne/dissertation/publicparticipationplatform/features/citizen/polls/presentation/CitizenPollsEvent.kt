package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation

sealed class CitizenPollsEvent {
    data class OnPollClicked(val poll: PollWithPolicyName) : CitizenPollsEvent()
    data class OnSearchQueryChanged(val query: String) : CitizenPollsEvent()
    data class OnStatusFilterChanged(val status: PollStatus?) : CitizenPollsEvent()
    object RefreshPolls : CitizenPollsEvent()
    object OnErrorShown : CitizenPollsEvent()
}
