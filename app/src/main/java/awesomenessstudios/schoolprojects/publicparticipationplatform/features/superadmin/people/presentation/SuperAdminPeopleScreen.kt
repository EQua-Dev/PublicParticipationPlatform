package awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.presentation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.components.ExpandableFloatingActionButton
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.components.FabMenu
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.citizens.presentation.CitizenListScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.officials.presentation.OfficialListScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminPeopleScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SuperAdminPeopleViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Super Admin Profile") })
        },
        floatingActionButton = {
            /*   FabMenu(
                   isExpanded = uiState.isFabMenuExpanded,
                   onToggle = { expanded ->

                       viewModel.onEvent(
                           SuperAdminUiEvent.ToggleFabMenu(
                               expanded
                           )
                       )

                       Log.d("SAPS", "SuperAdminPeopleScreen: ${uiState.isFabMenuExpanded}")
                   },
                   onAddCitizen = { navController.navigate("add_citizen") },
                   onAddOfficial = { navController.navigate("add_official") }
               )*/
            ExpandableFloatingActionButton(
                isExpanded = uiState.isFabMenuExpanded,
                onFabClick = { viewModel.onEvent(SuperAdminUiEvent.ToggleFabMenu(!uiState.isFabMenuExpanded)) },
                onAddCitizen = { navController.navigate(Screen.CreateCitizenScreen.route) },
                onAddOfficial = { navController.navigate(Screen.CreateOfficialScreen.route) }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = uiState.selectedTab) {
                listOf("Citizens", "Officials").forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.onEvent(SuperAdminUiEvent.SelectTab(index)) },
                        text = { Text(title) }
                    )
                }
            }

            when (uiState.selectedTab) {
                0 -> CitizenListScreen()
                1 -> OfficialListScreen()
            }
        }
    }
}