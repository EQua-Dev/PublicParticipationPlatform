package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CitizenProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CitizenProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onEvent(CitizenProfileEvent.ProfileImageSelected(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(CitizenProfileEvent.ToggleEditMode) }) {
                        Icon(
                            imageVector = if (state.isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (state.isEditing) "Cancel" else "Edit"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.isEditing) {
                FloatingActionButton(
                    onClick = {
//                        if (state.phoneNumber.isNotBlank())
                            viewModel.onEvent(
                            CitizenProfileEvent.SaveProfile
                        )
                    },
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
            else {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.AuditLogScreen.route)
                    },
                ) {
                    Text(text = "Audit Logs")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                state.citizen?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable(enabled = state.isEditing) {
                                    imagePicker.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                state.profileImageUri != null -> {
                                    Image(
                                        painter = rememberImagePainter(state.profileImageUri),
                                        contentDescription = "Selected profile image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                state.citizen.profileImage.isNotEmpty() -> {
                                    AsyncImage(
                                        model = state.citizen.profileImage,
                                        contentDescription = "Profile image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile placeholder",
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                            }

                            if (state.isEditing) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change photo",
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .size(32.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = CircleShape
                                        )
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Profile Details
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProfileDetailItem(
                                label = "Name",
                                value = "${state.citizen.firstName} ${state.citizen.lastName}"
                            )


                                ProfileDetailItem(
                                    label = "Email",
                                    value = it.email
                                )


                            if (state.isEditing) {
                                OutlinedTextField(
                                    value = state.phoneNumber,
                                    onValueChange = {
                                        viewModel.onEvent(CitizenProfileEvent.PhoneNumberChanged(it))
                                    },
                                    label = { Text("Phone Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                                )
                            } else {
                                ProfileDetailItem(
                                    label = "Phone",
                                    value = state.citizen.phoneNumber
                                )
                            }

                            if (state.isEditing) {
                                OutlinedTextField(
                                    value = state.occupation,
                                    onValueChange = {
                                        viewModel.onEvent(CitizenProfileEvent.OccupationChanged(it))
                                    },
                                    label = { Text("Occupation") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = state.countyOfResidence,
                                    onValueChange = {
                                        viewModel.onEvent(CitizenProfileEvent.CountyOfResidenceChanged(it))
                                    },
                                    label = { Text("County of Residence") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                ProfileDetailItem(
                                    label = "Occupation",
                                    value = state.citizen.occupation
                                )

                                ProfileDetailItem(
                                    label = "County of Residence",
                                    value = state.citizen.countyOfResidence
                                )
                            }
                        }

                        LanguageSettingSection(
                            isEditing = state.isEditing,
                            selectedLanguage = state.selectedLanguage,
                            onLanguageSelected = { viewModel.onEvent(CitizenProfileEvent.LanguageChanged(it)) }
                        )

                    }
                }


            }

            if (state.updateSuccess) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    action = {
                        TextButton(
                            onClick = { viewModel.onEvent(CitizenProfileEvent.DismissSuccess) }
                        ) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text("Profile updated successfully")
                }
            }

            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    action = {
                        TextButton(
                            onClick = { viewModel.onEvent(CitizenProfileEvent.DismissSuccess) }
                        ) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}
