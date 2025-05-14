package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.officialdetail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.components.ImagePicker
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.LoadingDialog
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.permissions

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
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(uiState) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.uiState.value.copy(error = null)
        }
    }


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
                OfficialDetailEvent.OfficialActivated -> {
                    Toast.makeText(context, "Official activated", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }

                else -> {

                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Official Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> FullScreenLoading()
                uiState.official == null -> ErrorState(
                    error = "Official not found",
                    onRetry = { viewModel.onEvent(OfficialDetailEvent.LoadOfficial(officialId)) }
                )
                else -> OfficialDetailsContent(
                    official = uiState.official!!,
                    onEditClick = { showBottomSheet = true },
                    onToggleStatus = { isActive ->
                        viewModel.onEvent(
                            if (isActive) OfficialDetailEvent.DeactivateOfficial
                            else OfficialDetailEvent.ActivateOfficial
                        )
                    }
                )
            }
        }
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


@Composable
private fun OfficialDetailsContent(
    official: Official,
    onEditClick: () -> Unit,
    onToggleStatus: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Section
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = official.profileImageUrl,
                contentDescription = "Official profile",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (official.active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Status Chip
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FilterChip(
                selected = true,
                onClick = {},
                label = {
                    Text(if (official.active) "Active" else "Inactive")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (official.active) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer,
                    selectedLabelColor = if (official.active) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Information
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LabeledText(label = "Full Name", text = "${official.firstName} ${official.lastName}")
                LabeledText(label = "Email", text = official.email)
                LabeledText(label = "Phone", text = official.phoneNumber)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Permissions
        Text(
            text = "Permissions",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (official.permissions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    official.permissions.forEach { permission ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = permission.replace("_", " ").replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No permissions assigned",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilledTonalButton(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Details")
            }

            Button(
                onClick = { onToggleStatus(official.active) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (official.active) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = if (official.active) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Text(if (official.active) "Deactivate Official" else "Activate Official")
            }
        }
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


@Composable
private fun LabeledText(label: String, text: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}