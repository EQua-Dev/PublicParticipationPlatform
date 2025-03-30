package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy

data class PolicyState(
    val policies: List<Policy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canCreatePolicy: Boolean = false
)