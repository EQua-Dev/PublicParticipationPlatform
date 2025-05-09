package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.dashboard.presentation

data class SuperAdminDashboardState(
    val citizensCount: Int = 0,
    val officialsCount: Int = 0,
    val policiesCount: Int = 0,
    val pollsCount: Int = 0,
    val budgetsCount: Int = 0,
    val petitionsCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)