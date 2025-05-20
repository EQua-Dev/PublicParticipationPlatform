package ngui_maryanne.dissertation.publicparticipationplatform.features.officials

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile.OfficialProfileViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.OfficialBottomBarScreen
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.OfficialBottomNavigationGraph
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.AppBackground
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.BackgroundAnimations
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OfficialHomeScreen(
    baseNavHostController: NavHostController,
    onNavigationRequested: (String, Boolean) -> Unit,
    viewModel: OfficialProfileViewModel = hiltViewModel(),
    officialViewModel: OfficialsHomeViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val state = viewModel.state.value
    val officialState by officialViewModel.state.collectAsState()

//    val studentData by remember { studentHomeViewModel.studentInfo }.collectAsState()

    val errorMessage = remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
//    val showLoading by remember { mutableStateOf(studentHomeViewModel.showLoading) }
//    val openDialog by remember { mutableStateOf(studentHomeViewModel.openDialog) }

    val screens = listOf(
        OfficialBottomBarScreen.Policies,
        OfficialBottomBarScreen.Polls,
        OfficialBottomBarScreen.Petitions,
        OfficialBottomBarScreen.Budget,
        OfficialBottomBarScreen.Profile,
        OfficialBottomBarScreen.Citizens,
    )




    LaunchedEffect(key1 = state) {
        if (officialState.logout) {
            baseNavHostController.navigate(Screen.InitRoleTypeScreen.route)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                // Drawer header
                DrawerHeader(state.official)

                // Drawer items
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(text = screen.title) },
                        selected = navController.currentBackStackEntryAsState().value?.destination?.route == screen.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(screen.route) {
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting an item
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(painter = painterResource(id = screen.icon), contentDescription = screen.title)
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.surface, // same as FAB background
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                // Spacer to push logout to bottom
                Spacer(modifier = Modifier.weight(1f))

                // Logout item
                NavigationDrawerItem(
                    label = { Text("Logout", color = Color.Red) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Add your logout logic here
                        // For example: authViewModel.logout()
                        officialViewModel.onEvent(OfficialsHomeEvent.Logout)
                        // Then navigate to login screen
                        baseNavHostController.navigate(Screen.InitRoleTypeScreen.route) {
                            popUpTo(0) { inclusive = true } // Clear back stack
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.Red
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        Scaffold(
            /* bottomBar = {
                 OfficialBottomBar(navController = navController)
             },*/
            topBar = {
                AppBar(
                    onMenuClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    profileImageUrl = state.official.profileImageUrl,
                    officialName = state.official.firstName
                )
            }
        ) { innerPadding ->
            val reducedTopPadding = (innerPadding.calculateTopPadding() - 8.dp).coerceAtLeast(0.dp)
            val reducedBottomPadding = (innerPadding.calculateBottomPadding() - 8.dp).coerceAtLeast(0.dp)



            Box(
                modifier = Modifier.padding(
                    top = reducedTopPadding,
                    bottom = reducedBottomPadding
                )
            ) {
                BackgroundAnimations()
                AppBackground {
                    OfficialBottomNavigationGraph(navController = navController)
                }
            }
        }
    }
    /*

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onDismissRequest.
                    openDialog.value = false
                },
                title = {
                    Text(text = "Logout", style = Typography.titleLarge)
                },
                text = {
                    Text(text = "Do you want to logout?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            Common.mAuth.signOut()
                            baseNavHostController.navigate(Screen.Login.route)
                            studentHomeViewModel.updateDialogStatus()
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            studentHomeViewModel.updateDialogStatus()
                        }
                    ) {
                        Text("No")
                    }
                },

                )
        }

        if (showLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))

            }
        }
    */

}

@Composable
fun DrawerHeader(
    official: Official?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        // Profile Image or Placeholder
        if (official?.profileImageUrl != null) {
            AsyncImage(
                model = official.profileImageUrl,
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "User",
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Name
        Text(
            text = official?.let { "Welcome, ${it.firstName}" } ?: "Welcome, Official",
            style = MaterialTheme.typography.titleMedium
        )

        // Email
        Text(
            text = official?.email ?: "official@example.com",
            style = MaterialTheme.typography.bodySmall
        )

        // Optional: Display role/permissions summary
        if (!official?.permissions.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Permissions: ${official?.permissions?.size ?: 0}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onMenuClick: () -> Unit,
    profileImageUrl: String? = null,
    officialName: String,
    modifier: Modifier = Modifier
) {
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when (currentHour) {
        in 6..11 -> stringResource(R.string.good_morning)
        in 12..17 -> stringResource(R.string.good_afternoon)
        in 18..21 -> stringResource(R.string.good_evening)
        else -> stringResource(R.string.good_night)
    }

    TopAppBar(
        title = {
            Column {
                ColoredAppName()
                Text(
                    text = "$greeting, $officialName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clickable(onClick = onMenuClick)
            ) {
                if (!profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.shadow(4.dp) // Add visual shadow/elevation

    )
}

@Composable
fun ColoredAppName() {
    val context = LocalContext.current
    val appName = stringResource(id = R.string.app_name)

    val kenyanColors = listOf(
        Color(0xFF000000), // Black
        Color(0xFFB00B1C), // Red
        Color(0xFF006600), // Green
//        Color(0xFFFFFFFF)  // White
    )

    Text(
        text = buildAnnotatedString {
            appName.forEachIndexed { index, char ->
                val color = kenyanColors[index % kenyanColors.size]
                withStyle(style = SpanStyle(color = color)) {
                    append(char)
                }
            }
        },
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}
