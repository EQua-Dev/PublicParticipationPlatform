package ngui_maryanne.dissertation.publicparticipationplatform.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun OfficialBottomBar(navController: NavHostController) {
    val screens = listOf(
        OfficialBottomBarScreen.Policies,
        OfficialBottomBarScreen.Polls,
        OfficialBottomBarScreen.Petitions,
        OfficialBottomBarScreen.Budget,
        OfficialBottomBarScreen.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            OfficialAddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )

        }
    }

}


@Composable
fun RowScope.OfficialAddItem(
    screen: OfficialBottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        label = {
            Text(text = screen.title)
        },
        onClick = { navController.navigate(screen.route) },
        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) })
}


@Composable
fun SuperAdminBottomBar(navController: NavHostController) {
    val screens = listOf(
        SuperAdminBottomBarScreen.Dashboard,
        SuperAdminBottomBarScreen.PublicParticipation,
        SuperAdminBottomBarScreen.People,
        SuperAdminBottomBarScreen.Audit,
        SuperAdminBottomBarScreen.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            SuperAdminAddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )

        }
    }

}


@Composable
fun RowScope.SuperAdminAddItem(
    screen: SuperAdminBottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        label = {
            Text(text = screen.title)
        },
        onClick = { navController.navigate(screen.route) },
        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) })
}