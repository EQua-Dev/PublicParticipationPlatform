package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.officialdetail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.components.ImagePicker
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.permissions
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OfficialDetailScreen(
    officialId: String,
    navController: NavController,
    viewModel: OfficialDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()



    LaunchedEffect(true) {
        // Load official details on screen load
        viewModel.onEvent(OfficialDetailEvent.LoadOfficial(officialId))

        // Collect UI events (update, deactivate, error)
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is OfficialDetailEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }

                OfficialDetailEvent.OfficialUpdated -> {
                    Toast.makeText(context, "Official updated", Toast.LENGTH_SHORT).show()
                    showBottomSheet = false
                }

                OfficialDetailEvent.OfficialDeactivated -> {
                    Toast.makeText(context, "Official deactivated", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }

                else -> {

                }
            }
        }
    }

    // Show loading indicator while data is being fetched
    if (uiState.isLoading) {
        CircularProgressIndicator()
    } else {
        Column(Modifier.padding(16.dp)) {
            // Display Profile Image
            Image(
                painter = rememberImagePainter(uiState.official?.profileImageUrl),
                contentDescription = "Official Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Official Details
            Text(text = "Name: ${uiState.official?.firstName} ${uiState.official?.lastName}")
            Text(text = "Phone: ${uiState.official?.phoneNumber}")

            // Display Permissions List
            uiState.official?.permissions?.let { permissions ->
                if (permissions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Permissions:")
                    permissions.forEach { permission ->
                        Text(text = "• $permission", modifier = Modifier.padding(start = 16.dp))
                    }
                } else {
                    Text(text = "This official has no permissions")
                }
            }


            // Edit Button to trigger the Bottom Sheet
            CustomButton(
                onClick = {
                    showBottomSheet = true
                    coroutineScope.launch {
                        sheetState.show()
                    }
                },
                modifier = Modifier.padding(top = 16.dp),
                text = "Edit Details"
            )

            // Deactivate Button
            CustomButton(
                onClick = { viewModel.onEvent(OfficialDetailEvent.DeactivateOfficial) },
                modifier = Modifier.padding(top = 8.dp),
                text = "Deactivate Official"
            )
        }
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }


    // Bottom Sheet for Editing Official Details
    /*   ModalBottomSheetLayout(
           sheetState = sheetState,
           sheetContent = {

           }
       ) {
           // Content outside the bottom sheet goes here (empty since we only need the bottom sheet)
       }*/
    if (showBottomSheet) {
        Log.d("TAG", "OfficialDetailScreen: this is true for bottom sheet")
        EditOfficialDetails(
            uiState = uiState,
            official = uiState.official,
            allPermissions = permissions, // your full list of permission strings

            onSave = { updatedOfficial ->
                viewModel.onEvent(OfficialDetailEvent.UpdateOfficial(updatedOfficial))
            },
            onCancel = {
                showBottomSheet = false
                coroutineScope.launch {
                    sheetState.hide()  // Hide the modal bottom sheet when cancel
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOfficialDetails(
    official: Official?,
    allPermissions: List<String>,
    onSave: (Official) -> Unit,
    onCancel: () -> Unit,
    uiState: OfficialDetailUiState
) {

    var selectedPermissions by remember {
        mutableStateOf(official?.permissions?.toMutableList() ?: mutableListOf())
    }


    LaunchedEffect(key1 = Unit) {

        Log.d("TAG", "EditOfficialDetails: $official")
    }


    var firstName by remember { mutableStateOf(official?.firstName ?: "") }
    var lastName by remember { mutableStateOf(official?.lastName ?: "") }
    var phoneNumber by remember { mutableStateOf(official?.phoneNumber ?: "") }
    var profileImageUri by remember { mutableStateOf<String?>(official?.profileImageUrl) }

    var showImagePicker by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()


    ModalBottomSheet(
        onDismissRequest = { onCancel() },
        content = {
            Box {

                if (uiState.isLoading) {
                    LoadingDialog()
                }
                Column {
                    if (showImagePicker) {
                        // Image Picker Logic
                        ImagePicker(
                            onImageSelected = { uri ->
                                profileImageUri = uri.toString()
                                showImagePicker = false
                            },
                            emptyStateText = "Select Image",
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .offset(x = 24.dp)
                                .clip(
                                    CircleShape
                                )
                                .height(120.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        // Profile Image Section
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberImagePainter(profileImageUri),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { showImagePicker = true }) {
                                Text("Change Profile Image")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Text Fields for Official Details
                        CustomTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = "First Name"
                        )
                        CustomTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = "Last Name"
                        )
                        CustomTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = "Phone Number"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Permissions", style = MaterialTheme.typography.titleSmall)

                        allPermissions.forEach { permission ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedPermissions.contains(permission),
                                    onCheckedChange = {
                                        val updatedPermissions = selectedPermissions.toMutableSet()
                                        if (updatedPermissions.contains(permission)) {
                                            updatedPermissions.remove(permission)
                                        } else {
                                            updatedPermissions.add(permission)
                                        }
                                        selectedPermissions =
                                            updatedPermissions.toList()
                                                .toMutableList() // triggers recomposition
                                    }
                                )


                                Text(permission)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val updatedOfficial = official?.copy(
                                        firstName = firstName,
                                        lastName = lastName,
                                        phoneNumber = phoneNumber,
                                        profileImageUrl = profileImageUri,
                                        permissions = selectedPermissions
                                    )
                                    updatedOfficial?.let { onSave(it) }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isLoading
                            ) {
                                Text("Save Changes")
                            }

                            Button(
                                onClick = onCancel,
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isLoading
                            ) {
                                Text("Cancel")
                            }
                        }


                    }

                }

            }

        }
    )
}
