package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.BackgroundAnimations
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.LoadingDialog
import ngui_maryanne.dissertation.publicparticipationplatform.ui.theme.KenyaGreen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe

@Composable
fun CitizenHomeScreen(
    navController: NavHostController,
    viewModel: CitizenHomeViewModel = hiltViewModel()
) {
    // Define Kenyan theme colors
    val kenyaGreen = Color(0xFF006600)
    val kenyaRed = Color(0xFFBF0000)
    val kenyaBlack = Color(0xFF000000)
    val kenyaWhite = Color(0xFFF5F5F5)
    val kenyaGold = Color(0xFFFFD700)

    val state by viewModel.state.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val announcements = state.announcements

    LaunchedEffect(key1 = state) {
        if (state.logout) {
            navController.navigate(Screen.InitRoleTypeScreen.route)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CitizenHomeTopBar(
                citizen = state.citizen,
                notifications = state.notifications,
                onProfileClick = { navController.navigate(Screen.CitizenProfileScreen.route) },
                onNotificationsClick = { navController.navigate(Screen.NotificationScreen.route) },
                onLogout = { openDialog.value = true }
            )
        }
    ) { paddingValues ->
        // Main background with animated elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            kenyaBlack,
                            kenyaBlack.copy(alpha = 0.85f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Background animations
            BackgroundAnimations()

            // Content based on state
            when {
                state.isLoading -> LoadingDialog(loadingText = "Loading your dashboard...")
                !state.isApproved -> AwaitingApprovalScreen()
                else -> ApprovedCitizenHome(
                    paddingValues = paddingValues,
                    navController = navController,
                    announcements = announcements
                )
            }
        }
    }

    if (openDialog.value) {
        KenyanAlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = "Logout",
            message = "Do you want to logout?",
            onConfirm = { viewModel.onEvent(CitizenHomeEvent.Logout) },
            onDismiss = { openDialog.value = false }
        )
    }
}

@Composable
fun KenyanAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val kenyaGreen = Color(0xFF006600)
    val kenyaRed = Color(0xFFBF0000)
    val kenyaBlack = Color(0xFF000000)
    val kenyaWhite = Color(0xFFF5F5F5)
    val kenyaGold = Color(0xFFFFD700)

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = kenyaBlack.copy(alpha = 0.9f),
            border = BorderStroke(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(kenyaRed, kenyaGreen)
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = kenyaWhite,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    color = kenyaWhite.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = kenyaRed
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("No", color = kenyaWhite)
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = kenyaGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Yes", color = kenyaWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun AwaitingApprovalScreen() {
    val kenyaGreen = Color(0xFF006600)
    val kenyaRed = Color(0xFFBF0000)
    val kenyaWhite = Color(0xFFF5F5F5)
    val kenyaGold = Color(0xFFFFD700)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shield container with animation
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            kenyaGold.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.waiting),
                contentDescription = "Awaiting Approval Illustration",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Awaiting Approval",
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = kenyaWhite,
                letterSpacing = 0.5.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Your account is pending approval. You'll gain access to all features once approved.",
            style = TextStyle(
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = kenyaWhite.copy(alpha = 0.8f)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = { /* Navigate to app guide */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = kenyaGreen
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = kenyaWhite
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Learn About the App",
                    color = kenyaWhite,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ApprovedCitizenHome(
    paddingValues: PaddingValues,
    navController: NavHostController,
    announcements: MutableList<Announcement>
) {
    val contentColor = Color(0xFFF5F5F5) // Light color for content on dark background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Carousel section
        if (announcements.isNotEmpty()) {
            Text(
                text = "ANNOUNCEMENTS",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            AnnouncementsCarousel(
                announcements = announcements,
                onAnnouncementClick = { announcement ->
                    when (announcement.type) {
                        NotificationTypes.POLICY -> navController.navigate(Screen.CitizenPolicies.route)
                        NotificationTypes.POLL -> navController.navigate(Screen.CitizenPolls.route)
                        NotificationTypes.PETITION -> navController.navigate(Screen.CitizenPetitions.route)
                        else -> navController.navigate(Screen.CitizenParticipatoryBudget.route)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Services section heading
        Text(
            text = "SERVICES",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Service cards grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Add bottom padding for better UX
        ) {
            val services = listOf(
                ServiceItem(R.drawable.ic_policies, "Policies", KenyaGreen) {
                    navController.navigate(Screen.CitizenPolicies.route)
                },
                ServiceItem(R.drawable.ic_polls, "Polls", Color(0xFFBF0000)) {
                    navController.navigate(Screen.CitizenPolls.route)
                },
                ServiceItem(R.drawable.ic_petitions, "Petitions", Color(0xFFFFD700)) {
                    navController.navigate(Screen.CitizenPetitions.route)
                },
                ServiceItem(R.drawable.ic_budget, "Budget", Color(0xFF006600)) {
                    navController.navigate(Screen.CitizenParticipatoryBudget.route)
                }
            )

            items(services.size) { index ->
                ActionCard(
                    icon = services[index].icon,
                    label = services[index].label,
                    accentColor = services[index].color,
                    onClick = services[index].onClick
                )
            }
        }
    }
}

data class ServiceItem(
    val icon: Int,
    val label: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun ActionCard(icon: Int, label: String, accentColor: Color, onClick: () -> Unit) {
    val kenyaBlack = Color(0xFF000000)
    val kenyaWhite = Color(0xFFF5F5F5)

    val infiniteTransition = rememberInfiniteTransition(label = "card_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .clickable { onClick() }
            .graphicsLayer {
                scaleX = if (isHovering) scale else 1f
                scaleY = if (isHovering) scale else 1f
            },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = kenyaBlack.copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.7f),
                    accentColor
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = Offset.Infinite,
                        radius = 300f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(56.dp),
                    tint = accentColor
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = kenyaWhite
                    )
                )
            }
        }
    }
}

// Hover state simulation for demo
val isHovering = false

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenHomeTopBar(
    citizen: Citizen?,
    notifications: MutableList<AppNotification>,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val kenyaGreen = Color(0xFF006600)
    val kenyaRed = Color(0xFFBF0000)
    val kenyaBlack = Color(0xFF000000)
    val kenyaWhite = Color(0xFFF5F5F5)
    val kenyaGold = Color(0xFFFFD700)

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Small Kenya flag icon
                Image(
                    painter = painterResource(id = R.drawable.ic_people), // Add Kenya flag icon
                    contentDescription = "Kenya Flag",
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        "Hello,",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = kenyaWhite.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = citizen?.firstName ?: "Citizen",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = kenyaWhite
                        )
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = kenyaBlack.copy(alpha = 0.7f)
        ),
        actions = {
            // Notifications Icon with Badge
            IconButton(onClick = onNotificationsClick) {
                BadgedBox(
                    badge = {
                        if (notifications.isNotEmpty()) {
                            Badge(
                                containerColor = kenyaRed
                            ) {
                                Text(
                                    notifications.size.toString(),
                                    color = kenyaWhite
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = kenyaGold
                    )
                }
            }

            // Profile button
            IconButton(onClick = onProfileClick) {
                if (citizen?.profileImage?.isNotEmpty() == true) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(
                                width = 2.dp,
                                brush = Brush.sweepGradient(
                                    listOf(kenyaGreen, kenyaRed, kenyaGold)
                                ),
                                shape = CircleShape
                            )
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = citizen.profileImage,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(
                                width = 2.dp,
                                brush = Brush.sweepGradient(
                                    listOf(kenyaGreen, kenyaRed, kenyaGold)
                                ),
                                shape = CircleShape
                            )
                            .padding(2.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            tint = kenyaWhite
                        )
                    }
                }
            }

            // Logout button
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = kenyaRed
                )
            }
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
    val kenyaWhite = Color(0xFFF5F5F5)

    LaunchedEffect(Unit) {
        // Auto-slide logic
        while (true) {
            delay(4000)
            pagerState.animateScrollToPage(
                (pagerState.currentPage + 1) % announcements.size,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Carousel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp
        ) { pageIndex ->
            val announcement = announcements[pageIndex]
            AnnouncementCard(
                announcement = announcement,
                onClick = { onAnnouncementClick(announcement) }
            )
        }

        // Page indicator dots
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(announcements.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) {
                    when (announcements[iteration].type) {
                        NotificationTypes.POLICY -> Color(0xFF006600)
                        NotificationTypes.POLL -> Color(0xFFBF0000)
                        NotificationTypes.PETITION -> Color(0xFFFFD700)
                        else -> Color(0xFF006600)
                    }
                } else {
                    kenyaWhite.copy(alpha = 0.3f)
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = if (pagerState.currentPage == iteration) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                        .animateContentSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val kenyaGreen = Color(0xFF006600)
    val kenyaRed = Color(0xFFBF0000)
    val kenyaBlack = Color(0xFF000000)
    val kenyaWhite = Color(0xFFF5F5F5)
    val kenyaGold = Color(0xFFFFD700)

    // Determine color based on announcement type
    val accentColor = when (announcement.type) {
        NotificationTypes.POLICY -> kenyaGreen
        NotificationTypes.POLL -> kenyaRed
        NotificationTypes.PETITION -> kenyaGold
        else -> kenyaGreen
    }

    val iconRes = when (announcement.type) {
        NotificationTypes.POLICY -> R.drawable.ic_policies
        NotificationTypes.POLL -> R.drawable.ic_polls
        NotificationTypes.PETITION -> R.drawable.ic_petitions
        else -> R.drawable.ic_budget
    }

    Card(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { /* Optional: Handle long click */ }
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = kenyaBlack.copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.7f),
                    accentColor
                )
            )
        )
    ) {
        Column {
            // Header with icon and background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Large icon
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Announcement icon",
                    modifier = Modifier.size(70.dp),
                    tint = accentColor
                )

                // Category badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(
                            color = accentColor,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (announcement.type) {
                            NotificationTypes.POLICY -> "POLICY"
                            NotificationTypes.POLL -> "POLL"
                            NotificationTypes.PETITION -> "PETITION"
                            else -> "BUDGET"
                        },
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (announcement.type == NotificationTypes.PETITION) kenyaBlack else kenyaWhite,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            // Content area
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title
                Text(
                    text = announcement.title,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = kenyaWhite
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Description
                Text(
                    text = announcement.description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = kenyaWhite.copy(alpha = 0.8f)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Footer with date
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = accentColor
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = HelpMe.getDate(announcement.createdAt.toLong(), "EEE dd MMM yyyy"),
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = kenyaWhite.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}