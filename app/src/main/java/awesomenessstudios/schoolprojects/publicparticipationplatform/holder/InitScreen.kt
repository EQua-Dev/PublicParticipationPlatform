package awesomenessstudios.schoolprojects.publicparticipationplatform.holder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.UserRole

@Composable
fun InitScreen(
    holderViewModel: HolderViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onRoleSelected: (String) -> Unit // Callback to handle role selection
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Teacher Button
        Button(
            onClick = {
                holderViewModel.saveRole(UserRole.SUPERADMIN)
                onRoleSelected(UserRole.SUPERADMIN.name)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Super Admin")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Button
        Button(
            onClick = {
                holderViewModel.saveRole(UserRole.OFFICIAL)
                onRoleSelected(UserRole.OFFICIAL.name) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Official")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Student Button
        Button(
            onClick = {
                holderViewModel.saveRole(UserRole.CITIZEN)
                onRoleSelected(UserRole.CITIZEN.name) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Citizen")
        }
    }
}