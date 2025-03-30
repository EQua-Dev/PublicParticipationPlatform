package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.polls

sealed class PollEvent {
    object LoadData : PollEvent()
    object NavigateToCreatePoll : PollEvent()
}