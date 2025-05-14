package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.signup


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.AssimOutlinedDropdown
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login.KenyanBackgroundPattern
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login.KenyanShieldLogo
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.countiesMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenRegistrationStepTwo(
    state: CitizenRegistrationState,
    onEvent: (CitizenRegistrationEvent) -> Unit,
    onRegistrationComplete: () -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var logoVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        logoVisible = true // Trigger the animation when the screen appears
    }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                    initialScale = 0.5f,
                    animationSpec = tween(1000)
                )
            ) {
                /*Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                ) {*/
                Image(
                    painter = painterResource(id = R.drawable.app_logo), // replace with your drawable
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(16.dp) // inner padding inside the card
                        .clip(shape = RoundedCornerShape(12.dp))
                )
//                    }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.complete_your_profile),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = stringResource(R.string.finish_setting_up_your_citizen_account),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Registration form card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Picture Section
                    Text(
                        text = stringResource(R.string.profile_picture),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .size(150.dp)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = CircleShape
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                selectedImageUri?.let {
                                    onEvent(CitizenRegistrationEvent.ProfileImageChanged(it))
                                    AsyncImage(
                                        model = it,
                                        contentDescription = "Profile Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } ?: Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Add Profile Picture",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.tap_to_add_profile_photo),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Personal Details Section
                    Text(
                        text = stringResource(R.string.personal_details),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Occupation
                    OutlinedTextField(
                        value = state.occupation,
                        onValueChange = { onEvent(CitizenRegistrationEvent.OccupationChanged(it)) },
                        label = { Text(stringResource(R.string.occupation)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = null
                            )
                        },
                        colors = textFieldColors()
                    )

                    // County of Residence
                    AssimOutlinedDropdown(
                        label = stringResource(id = R.string.county_of_residence_label),
                        hint = stringResource(id = R.string.county_of_residence_hint),
                        options = countiesMap.sortedBy { it.first },
                        selectedValue = state.countyOfResidence,
                        onValueSelected = { onEvent(CitizenRegistrationEvent.CountyOfResidenceChanged(it.toString())) },
                        isCompulsory = true,
                        isSearchable = true,
                        /*leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null
                            )
                        }*/
                    )

                    // County of Birth
                    AssimOutlinedDropdown(
                        label = stringResource(id = R.string.county_of_birth_label),
                        hint = stringResource(id = R.string.county_of_birth_hint),
                        options = countiesMap.sortedBy { it.first },
                        selectedValue = state.countyOfBirth,
                        onValueSelected = { onEvent(CitizenRegistrationEvent.CountyOfBirthChanged(it.toString())) },
                        isCompulsory = true,
                        isSearchable = true,
                        /*leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null
                            )
                        }*/
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = { onEvent(CitizenRegistrationEvent.CompleteRegistration) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = state.occupation.isNotBlank() &&
                                state.countyOfResidence.isNotBlank() &&
                                state.countyOfBirth.isNotBlank() &&
                                !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.complete_registration),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.completing_registration),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Snackbar for errors
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                snackbarData = data
            )
        }
    }

    // Handle errors
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Navigate when registration is complete
    LaunchedEffect(state.isRegistrationComplete) {
        if (state.isRegistrationComplete) {
            onRegistrationComplete()
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    focusedLabelColor = MaterialTheme.colorScheme.primary
)