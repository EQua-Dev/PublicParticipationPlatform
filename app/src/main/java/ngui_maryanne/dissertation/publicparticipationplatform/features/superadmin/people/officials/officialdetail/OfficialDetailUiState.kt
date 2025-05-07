package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.officialdetail

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official

data class OfficialDetailUiState(
    val isLoading: Boolean = false,
    val official: Official? = null,
    val permissions: List<String> = listOf(),
    val error: String? = null
)