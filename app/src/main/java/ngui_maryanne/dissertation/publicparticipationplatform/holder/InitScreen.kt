package ngui_maryanne.dissertation.publicparticipationplatform.holder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole

@Composable
fun InitScreen(
    holderViewModel: HolderViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onRoleSelected: (String) -> Unit // Callback to handle role selection
) {

    var logoVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        logoVisible = true // Trigger the animation when the screen appears
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo with animation
        AnimatedVisibility(
            visible = logoVisible,
            enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(1000)
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo), // <-- replace with your drawable name
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 32.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
            )
        }

        // Teacher Button
        CustomButton(
            text = stringResource(id = R.string.super_admin_text),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                holderViewModel.saveRole(UserRole.SUPERADMIN)
                onRoleSelected(UserRole.SUPERADMIN.name)
            })
        /*    Button(
                onClick = {
                    holderViewModel.saveRole(UserRole.SUPERADMIN)
                    onRoleSelected(UserRole.SUPERADMIN.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Super Admin")
            }*/

        Spacer(modifier = Modifier.height(16.dp))

        // Student Button
        CustomButton(
            text = stringResource(id = R.string.official_text),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                holderViewModel.saveRole(UserRole.OFFICIAL)
                onRoleSelected(UserRole.OFFICIAL.name)
            })
    /*    Button(
            onClick = {
                holderViewModel.saveRole(UserRole.OFFICIAL)
                onRoleSelected(UserRole.OFFICIAL.name)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Official")
        }

     */
        Spacer(modifier = Modifier.height(16.dp))

        // Student Button
    CustomButton(
        text = stringResource(id = R.string.citizen_text),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            holderViewModel.saveRole(UserRole.CITIZEN)
            onRoleSelected(UserRole.CITIZEN.name)
        })
     /*   Button(
            onClick = {
                holderViewModel.saveRole(UserRole.CITIZEN)
                onRoleSelected(UserRole.CITIZEN.name)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Citizen")
        }*/
    }
}
