package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LoadingDialog
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen

@Composable
fun CitizenHomeScreen(
    navController: NavHostController,
    viewModel: CitizenHomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()


    LaunchedEffect(key1 = state) {
        if (state.logout) {
            navController.navigate(Screen.InitRoleTypeScreen.route)
        }
    }

    Scaffold(
        topBar = {
            CitizenHomeTopBar(
                citizen = state.citizen,
                onProfileClick = { navController.navigate(Screen.CitizenProfileScreen.route) },
                onLogout = { viewModel.onEvent(CitizenHomeEvent.Logout) }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingDialog()
            !state.isApproved -> AwaitingApprovalScreen()
            else -> ApprovedCitizenHome(
                paddingValues = paddingValues,
                navController = navController
            )
        }
    }
}

@Composable
fun AwaitingApprovalScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Awaiting Approval", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = { /* Navigate to app guide */ },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Learn About the App")
        }
    }
}

@Composable
fun ApprovedCitizenHome(
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    Column(modifier = Modifier.padding(paddingValues)) {
        // Carousel (Auto-sliding announcements)
//        AnnouncementCarousel()

        // 2x2 Grid of Actions
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(8.dp)
        ) {
            items(4) { index ->
                ActionCard(
                    icon = when (index) {
                        0 -> R.drawable.ic_policies
                        1 -> R.drawable.ic_polls
                        2 -> R.drawable.ic_petitions
                        else -> R.drawable.ic_budget
                    },
                    label = when (index) {
                        0 -> "Policies"
                        1 -> "Polls"
                        2 -> "Petitions"
                        else -> "Participatory Budget"
                    },
                    onClick = {
                        when (index) {
                            0 -> navController.navigate(Screen.CitizenPolicies.route)
                            1 -> navController.navigate(Screen.CitizenPolls.route)
                            2 -> navController.navigate(Screen.CitizenPetitions.route)
                            else -> navController.navigate(Screen.CitizenParticipatoryBudget.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ActionCard(icon: Int, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(label, textAlign = TextAlign.Center)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenHomeTopBar(
    citizen: Citizen?,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Text("Hello, ${citizen?.firstName ?: "Citizen"}")
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                if (citizen?.profileImage?.isNotEmpty() == true) {
                    AsyncImage(
                        model = citizen.profileImage,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                }
            }
            Text("Logout", modifier = Modifier.clickable {
                onLogout()
            })
            /* DropdownMenu(
                 expanded = false*//* State for menu visibility *//*,
                onDismissRequest = { *//* Close menu *//* }
            ) {
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = { *//* Navigate to settings *//* }
                )
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = onLogout
                )
            }*/
        }
    )
}