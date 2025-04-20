package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.participatorybudgets.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun CitizenParticipatoryBudgetScreen(modifier: Modifier = Modifier, navController: NavHostController,) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Participatory Budget")
    }
}