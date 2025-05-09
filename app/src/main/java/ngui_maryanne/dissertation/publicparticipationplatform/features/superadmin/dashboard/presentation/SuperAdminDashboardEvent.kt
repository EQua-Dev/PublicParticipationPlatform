package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.dashboard.presentation

sealed class SuperAdminDashboardEvent {
    object LoadDashboardData : SuperAdminDashboardEvent()
    object ErrorShown : SuperAdminDashboardEvent()
    data class CardClicked(val cardType: DashboardCardType) : SuperAdminDashboardEvent()
}


enum class DashboardCardType {
    CITIZENS, OFFICIALS, POLICIES, POLLS, BUDGETS, PETITIONS
}