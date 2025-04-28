package ngui_maryanne.dissertation.publicparticipationplatform.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
        icon = { Icon(painterResource(id = screen.icon), contentDescription = screen.title) })
}

@Composable
fun SuperAdminBottomBar(navController: NavHostController) {
    val screens = listOf(
        SuperAdminBottomBarScreen.Dashboard,
        SuperAdminBottomBarScreen.People,
        SuperAdminBottomBarScreen.Audit,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, // Background color
        tonalElevation = 8.dp, // slight shadow
    ) {
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
    val selected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true

    val animatedIconTint by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300)
    )

    val animatedIndicatorColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    NavigationBarItem(
        selected = selected,
        onClick = {
            if (!selected) {
                navController.navigate(screen.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = animatedIndicatorColor,
                        shape = CircleShape
                    )
                    .padding(8.dp)
            ) {
                Icon(painterResource(id = screen.icon), contentDescription = screen.title)
            }
        },
        label = {
            Text(
                text = screen.title,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent // We're handling background manually
        )
    )
}
