package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.dashboard.presentation

sealed class SuperAdminDashboardEvent {
    object LoadDashboardData : SuperAdminDashboardEvent()
    data class CardClicked(val cardType: DashboardCardType) : SuperAdminDashboardEvent()
}

enum class DashboardCardType {
    Citizens, Policies, Polls, Budgets, Petitions
}
