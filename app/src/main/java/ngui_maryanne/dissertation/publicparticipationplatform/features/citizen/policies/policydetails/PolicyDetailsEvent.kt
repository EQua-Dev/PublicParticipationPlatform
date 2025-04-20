package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.policydetails


sealed class PolicyDetailsAction {
    data class LoadPolicy(val policyId: String) : PolicyDetailsAction()
    object OnBackClicked : PolicyDetailsAction()
    data class OnPollClicked(val pollId: String) : PolicyDetailsAction()
    object ToggleDescriptionExpanded : PolicyDetailsAction()
    object ToggleTimelineExpanded : PolicyDetailsAction()

    data object TogglePollsExpanded : PolicyDetailsAction()
    data object ToggleCommentsExpanded : PolicyDetailsAction()
    data class OnCommentTextChanged(val text: String) : PolicyDetailsAction()
    data class OnAnonymousToggled(val isAnonymous: Boolean) : PolicyDetailsAction()
    object SubmitComment : PolicyDetailsAction()
}

sealed class PolicyDetailsEvent {
    object SetupCommentsListener : PolicyDetailsEvent()
    object NavigateBack : PolicyDetailsEvent()
    data class NavigateToPollDetails(val pollId: String) : PolicyDetailsEvent()
}