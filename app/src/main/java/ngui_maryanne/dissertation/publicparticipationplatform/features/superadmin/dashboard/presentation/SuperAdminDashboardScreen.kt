package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.dashboard.presentation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.NationalCitizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.SuperAdminBottomBarScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboardScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SuperAdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onEvent(SuperAdminDashboardEvent.LoadDashboardData)
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(SuperAdminDashboardEvent.ErrorShown)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard Overview") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> FullScreenLoading()
                else -> DashboardGrid(state, navController)
            }
        }
    }
}

@Composable
private fun DashboardGrid(
    state: SuperAdminDashboardState,
    navController: NavHostController
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DashboardCard(
                title = "Citizens",
                count = state.citizensCount,
                icon = R.drawable.ic_citizens,
                color = MaterialTheme.colorScheme.primary,
                onClick = {
                    navController.navigateToPeopleTab(0)
                }
            )
        }
        item {
            DashboardCard(
                title = "Officials",
                count = state.officialsCount,
                icon = R.drawable.ic_profile,
                color = MaterialTheme.colorScheme.secondary,
                onClick = {
                    navController.navigateToPeopleTab(1)
                }
            )
        }
        item {
            DashboardCard(
                title = "Policies",
                count = state.policiesCount,
                icon = R.drawable.ic_policies,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    navController.navigate(Screen.CitizenPolicies.route)
                }
            )
        }
        item {
            DashboardCard(
                title = "Polls",
                count = state.pollsCount,
                icon = R.drawable.ic_polls,
                color = MaterialTheme.colorScheme.primary,
                onClick = {
                    navController.navigate(Screen.CitizenPolls.route)
                }
            )
        }
        item {
            DashboardCard(
                title = "Budgets",
                count = state.budgetsCount,
                icon = R.drawable.ic_budget,
                color = MaterialTheme.colorScheme.secondary,
                onClick = {
                    navController.navigate(Screen.CitizenParticipatoryBudget.route)
                }
            )
        }
        item {
            DashboardCard(
                title = "Petitions",
                count = state.petitionsCount,
                icon = R.drawable.ic_petitions,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    navController.navigate(Screen.CitizenPetitions.route)
                }
            )
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    count: Int,
    icon: Int,
    color: Color,
    onClick: () -> Unit
) {
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(durationMillis = 800),
        label = "countAnimation"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    painterResource(id = icon),
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = animatedCount.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

private fun NavHostController.navigateToPeopleTab(selectedIndex: Int) {
    navigate(
        SuperAdminBottomBarScreen.People.route.replace(
            "{selectedIndex}",
            selectedIndex.toString()
        )
    ) {
        launchSingleTop = true
        restoreState = true
        popUpTo(SuperAdminBottomBarScreen.Dashboard.route) {
            saveState = true
        }
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

