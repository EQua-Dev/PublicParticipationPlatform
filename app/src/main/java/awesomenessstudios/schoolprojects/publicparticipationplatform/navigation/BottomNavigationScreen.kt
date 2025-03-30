/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package awesomenessstudios.schoolprojects.publicparticipationplatform.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationScreen(
    val title: String = "",
    val selectedItem: Unit,
    val unSelectedItem: Unit,
    val notificationCount: Int? = null,
    val route: String = ""
)

sealed class OfficialBottomBarScreen(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Policies :
        OfficialBottomBarScreen("Policies", Icons.Default.VideoCall, "official_policies")

    object Polls : OfficialBottomBarScreen("Polls", Icons.Default.Payments, "official_polls")
    object Petitions :
        OfficialBottomBarScreen("Petitions", Icons.Default.VolunteerActivism, "official_petitions")

    object Budget : OfficialBottomBarScreen("Budget", Icons.Default.Gavel, "official_budget")
    object Profile : OfficialBottomBarScreen("Profile", Icons.Default.Gavel, "official_profile")
    object Citizens : OfficialBottomBarScreen("Citizens", Icons.Default.Gavel, "official_citizens")


}

sealed class SuperAdminBottomBarScreen(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Dashboard :
        SuperAdminBottomBarScreen("Dashboard", Icons.Default.VideoCall, "super_admin_dashboard")

    object PublicParticipation : SuperAdminBottomBarScreen(
        "Participation",
        Icons.Default.MenuBook,
        "super_admin_public_participation",
    )

    object People :
        SuperAdminBottomBarScreen("People", Icons.Default.Payments, "super_admin_people")

    object Audit : SuperAdminBottomBarScreen("Audit", Icons.Default.Person, "super_admin_audit")
    object Profile :
        SuperAdminBottomBarScreen("Profile", Icons.Default.Person, "super_admin_profile")

}
