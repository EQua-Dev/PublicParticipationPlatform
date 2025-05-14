package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.components.StatusSelectionSection
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePolicyScreen(
    navController: NavHostController,
    viewModel: CreatePolicyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onEvent(CreatePolicyEvent.CoverImageSelected(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Policy") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .clickable(
                        enabled = state.policyName.isNotBlank() &&
                                state.policyTitle.isNotBlank() &&
                                state.policySector.isNotBlank(),
                        onClick = { viewModel.onEvent(CreatePolicyEvent.Submit) }
                    )
            ) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
                    text = { Text("Create Policy") },
                    onClick = { viewModel.onEvent(CreatePolicyEvent.Submit) }, // Empty if using Box's clickable
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cover Image Section remains the same
            PolicyCoverImageSection(
                imageUri = state.coverImageUri,
                onImageClick = { imagePicker.launch("image/*") }
            )

            // Policy Name Field
            CustomTextField(
                value = state.policyName,
                onValueChange = { viewModel.onEvent(CreatePolicyEvent.PolicyNameChanged(it)) },
                label = "Policy Name *",
                isError = state.error?.contains("Policy Name") == true,
                errorMessage = state.error?.takeIf { it.contains("Policy Name") },
                leadingIcon = Icons.Default.Description,
                keyboardType = KeyboardType.Text,
                isSingleLine = true
            )

            // Policy Title Field
            CustomTextField(
                value = state.policyTitle,
                onValueChange = { viewModel.onEvent(CreatePolicyEvent.PolicyTitleChanged(it)) },
                label = "Policy Title *",
                isError = state.error?.contains("Policy Title") == true,
                errorMessage = state.error?.takeIf { it.contains("Policy Title") },
                leadingIcon = Icons.Default.Title,
                keyboardType = KeyboardType.Text,
                isSingleLine = true
            )

            // Policy Sector Field (using DropdownMenu with CustomTextField)
            var expanded by remember { mutableStateOf(false) }
            Box {
                CustomTextField(
                    value = state.policySector,
                    onValueChange = {},
                    label = "Policy Sector *",
                    isError = state.error?.contains("Policy Sector") == true,
                    errorMessage = state.error?.takeIf { it.contains("Policy Sector") },
                    leadingIcon = Icons.Default.Category,
                    trailingIcon = Icons.Default.ArrowDropDown,
                    onTrailingIconClick = { expanded = true },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    state.sectors.forEach { sector ->
                        DropdownMenuItem(
                            text = { Text(sector) },
                            onClick = {
                                viewModel.onEvent(CreatePolicyEvent.PolicySectorChanged(sector))
                                expanded = false
                            }
                        )
                    }
                }
            }

            StatusSelectionSection(
                selectedStatus = state.selectedStatus,
                onStatusSelected = { viewModel.onEvent(CreatePolicyEvent.StatusChanged(it)) }
            )

            // Policy Description Field
            CustomTextField(
                value = state.policyDescription,
                onValueChange = { viewModel.onEvent(CreatePolicyEvent.PolicyDescriptionChanged(it)) },
                label = "Policy Description",
                leadingIcon = Icons.Default.Notes,
                keyboardType = KeyboardType.Text,
                isSingleLine = false,
                maxLines = 5,
                modifier = Modifier.heightIn(min = 100.dp)
            )

            // Status indicators
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Error/Success handling remains the same
        state.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(
                        onClick = { viewModel.onEvent(CreatePolicyEvent.DismissError) }
                    ) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(error)
            }
        }

        if (state.isSuccess) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(CreatePolicyEvent.DismissSuccess) },
                title = { Text("Success") },
                text = { Text("Policy created successfully!") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.onEvent(CreatePolicyEvent.DismissSuccess)
                            navController.popBackStack()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun PolicyCoverImageSection(
    imageUri: Uri?,
    onImageClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.large

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(shape)
            .background(colorScheme.surfaceVariant)
            .clickable(onClick = onImageClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberImagePainter(imageUri),
                contentDescription = "Policy cover image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Add cover image",
                    modifier = Modifier.size(48.dp),
                    tint = colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Add Cover Image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}