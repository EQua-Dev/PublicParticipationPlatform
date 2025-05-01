package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.CitizenHomeEvent
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.SuperAdminBottomBar
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.SuperAdminBottomNavigationGraph

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminHomeScreen(
    baseNavHostController: NavHostController,
    onNavigationRequested: (String, Boolean) -> Unit,
    viewModel: SuperAdminHomeViewModel = hiltViewModel()
//    studentHomeViewModel: StudentHomeViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()


//    val studentData by remember { studentHomeViewModel.studentInfo }.collectAsState()

    val errorMessage = remember { mutableStateOf("") }
//    val showLoading by remember { mutableStateOf(studentHomeViewModel.showLoading) }
    val openDialog = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = state) {
        if (state.logout) {
            baseNavHostController.navigate(Screen.InitRoleTypeScreen.route)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Hello, Admin")
                },
                actions = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            openDialog.value = true

                        })

                }
            )
        },
        bottomBar = {
            SuperAdminBottomBar(navController = navController)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            SuperAdminBottomNavigationGraph(navController = navController)
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
                            viewModel.onEvent(SuperAdminHomeEvent.Logout)
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
    /*

        if (showLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))

            }
        }
    */

}