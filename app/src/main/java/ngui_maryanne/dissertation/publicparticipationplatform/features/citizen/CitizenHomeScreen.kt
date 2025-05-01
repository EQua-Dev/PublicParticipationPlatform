package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LoadingDialog
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe

@Composable
fun CitizenHomeScreen(
    navController: NavHostController,
    viewModel: CitizenHomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val announcements = state.announcements

    LaunchedEffect(key1 = state) {
        if (state.logout) {
            navController.navigate(Screen.InitRoleTypeScreen.route)
        }
    }

    Scaffold(
        topBar = {
            CitizenHomeTopBar(
                citizen = state.citizen,
                notifications = state.notifications,
                onProfileClick = { navController.navigate(Screen.CitizenProfileScreen.route) },
                onNotificationsClick = {
                    navController.navigate(Screen.NotificationScreen.route)
                },
                onLogout = {
                    openDialog.value = true
                }
            )
        }
    ) { paddingValues ->

        when {

            state.isLoading -> LoadingDialog()
            !state.isApproved -> AwaitingApprovalScreen()
            else -> ApprovedCitizenHome(
                paddingValues = paddingValues,
                navController = navController,
                announcements = announcements
            )
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            },
            title = {
                Text(text = "Logout", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text(text = "Do you want to logout?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(CitizenHomeEvent.Logout)
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("No")
                }
            },

            )
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
    navController: NavHostController,
    announcements: MutableList<Announcement>
) {
    Column(modifier = Modifier.padding(paddingValues)) {
        // Carousel (Auto-sliding announcements)
//        AnnouncementCarousel()
        if (announcements.size > 0){
            AnnouncementsCarousel(
                announcements = announcements,
                onAnnouncementClick = { announcement ->
                    // Navigate based on announcement type
                    when (announcement.type) {
                        NotificationTypes.POLICY -> {
                            navController.navigate(Screen.CitizenPolicies.route)
                        }
                        NotificationTypes.POLL -> {
                            navController.navigate(Screen.CitizenPolls.route)
                        }
                        NotificationTypes.PETITION -> {
                            navController.navigate(Screen.CitizenPetitions.route)
                        }
                        else -> {
                            navController.navigate(Screen.CitizenParticipatoryBudget.route)
                        }
                    }
                }
            )
        }

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
    notifications: MutableList<AppNotification>,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
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
//                            .size(32.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                }
            }
            // Notifications Icon with Badge
            IconButton(onClick = onNotificationsClick) {
                BadgedBox(
                    badge = {
                        if (notifications.isNotEmpty()) {
                            Badge {
                                Text(notifications.size.toString())
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.clickable {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnnouncementsCarousel(
    announcements: List<Announcement>,
    onAnnouncementClick: (Announcement) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { announcements.size })


    LaunchedEffect(Unit) {
        // Auto-slide logic: Change page every 3 seconds
        while (true) {
            delay(3000)  // Delay for 3 seconds before sliding to next item
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % announcements.size)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)  // You can adjust the height to fit your UI
    ) { pageIndex ->
        val announcement = announcements[pageIndex]
        AnnouncementCard(
            announcement = announcement,
            onClick = { onAnnouncementClick(announcement) })
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.medium

    val iconRes = when (announcement.type) {
        NotificationTypes.POLICY -> R.drawable.ic_policies
        NotificationTypes.POLL -> R.drawable.ic_polls
        NotificationTypes.PETITION -> R.drawable.ic_petitions
        else -> R.drawable.ic_budget
    }

    Card(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = { /* Optional: Handle long click */ }
            ),
        shape = shape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorScheme.surfaceVariant)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Announcement icon",
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    tint = colorScheme.onSurfaceVariant
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(colorScheme.primary.copy(alpha = 0.8f), shape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onPrimary
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = announcement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
//                        text = "By ${announcement.createdBy}",
                        text = "",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.primary
                    )

                    Text(
                        text = HelpMe.getDate(announcement.createdAt.toLong(), "EEE dd MMM yyyy"),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
