package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

sealed class PollDetailsEvent {
    data class LoadPollDetails(val pollId: String) : PollDetailsEvent()
    object Retry : PollDetailsEvent()
}