package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.components.ExpandableFloatingActionButton
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.citizens.presentation.CitizenListScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.presentation.OfficialListScreen
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminPeopleScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    selectedTab: Int = 0,
    viewModel: SuperAdminPeopleViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value

    LaunchedEffect(selectedTab) {
        viewModel.onEvent(SuperAdminUiEvent.SelectTab(selectedTab))
    }
    Scaffold(
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