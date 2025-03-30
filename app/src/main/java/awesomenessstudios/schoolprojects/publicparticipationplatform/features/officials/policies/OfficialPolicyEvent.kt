package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies

sealed class PolicyEvent {
    object LoadPolicies : PolicyEvent()
    object NavigateToCreatePolicy : PolicyEvent()
}