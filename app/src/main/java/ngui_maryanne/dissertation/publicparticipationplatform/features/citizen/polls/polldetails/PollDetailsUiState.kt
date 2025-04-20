package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll

data class PollDetailsUiState(
    val poll: Poll? = null,
    val policy: Policy? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
