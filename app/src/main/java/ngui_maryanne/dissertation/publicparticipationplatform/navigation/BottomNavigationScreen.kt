/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package ngui_maryanne.dissertation.publicparticipationplatform.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector
import ngui_maryanne.dissertation.publicparticipationplatform.R

data class BottomNavigationScreen(
    val title: String = "",
    val selectedItem: Unit,
    val unSelectedItem: Unit,
    val notificationCount: Int? = null,
    val route: String = ""
)

sealed class OfficialBottomBarScreen(
    @StringRes val title: Int,
    val icon: Int,
    val route: String
) {
    object Policies :
        OfficialBottomBarScreen(R.string.policies, R.drawable.ic_policies, "official_policies")

    object Polls : OfficialBottomBarScreen(R.string.polls, R.drawable.ic_polls, "official_polls")
    object Petitions :
        OfficialBottomBarScreen(R.string.petitions, R.drawable.ic_petitions, "official_petitions")

    object Budget : OfficialBottomBarScreen(R.string.budget, R.drawable.ic_budget, "official_budget")
    object Citizens : OfficialBottomBarScreen(R.string.citizens, R.drawable.ic_citizens, "official_citizens")
    object Profile : OfficialBottomBarScreen(R.string.profile, R.drawable.ic_profile, "official_profile")


}

sealed class SuperAdminBottomBarScreen(
    val title: String,
    val icon: Int,
    val route: String,
    val defaultRoute: String

) {
    object Dashboard :
        SuperAdminBottomBarScreen("Dashboard", R.drawable.ic_dashboard, "super_admin_dashboard", "super_admin_dashboard")

    object People :
        SuperAdminBottomBarScreen("People", R.drawable.ic_people, "super_admin_people/{selectedIndex}", "super_admin_people/0")

    object Audit : SuperAdminBottomBarScreen("Audit", R.drawable.ic_audit, "super_admin_audit", "super_admin_audit")
    object Profile :
        SuperAdminBottomBarScreen("Profile", R.drawable.ic_profile, "super_admin_profile", "super_admin_profile")

}
