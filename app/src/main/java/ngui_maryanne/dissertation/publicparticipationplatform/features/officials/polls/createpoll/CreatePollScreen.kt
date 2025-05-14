package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls.createpoll

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollOption

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatePollScreen(
    navController: NavHostController,
    viewModel: CreatePollViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    if (state.createSuccess) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Poll") },
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
                        enabled = state.pollQuestion.isNotBlank() &&
                                state.options.size > 2 &&
                                state.selectedPolicy?.policyName!!.isNotBlank(),
                        onClick = { viewModel.onEvent(CreatePollEvent.Submit) }
                    )
            ) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onEvent(CreatePollEvent.Submit) },
                    icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
                    text = { Text("Create Poll") },
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
            // Policy Selection Dropdown
            PolicyDropdown(
                policies = state.policies,
                selectedPolicy = state.selectedPolicy,
                onPolicySelected = { viewModel.onEvent(CreatePollEvent.PolicySelected(it)) }
            )

            // Poll Question
            CustomTextField(
                value = state.pollQuestion,
                onValueChange = { viewModel.onEvent(CreatePollEvent.QuestionChanged(it)) },
                label = "Poll Question *",
                modifier = Modifier.fillMaxWidth(),
                isError = state.error?.contains("Poll Question") == true
            )

            // Poll Options
            Text("Poll Options:", style = MaterialTheme.typography.titleSmall)
            state.options.forEachIndexed { index, option ->
                PollOptionItem(
                    option = option,
                    onOptionChanged = {
                        viewModel.onEvent(
                            CreatePollEvent.OptionChanged(
                                index,
                                it
                            )
                        )
                    },
                    onRemove = { viewModel.onEvent(CreatePollEvent.RemoveOption(index)) }
                )
                Divider()
            }
            Button(
                onClick = { viewModel.onEvent(CreatePollEvent.AddOption) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Option")
            }

            // Expiry Days Selection
            Text("Poll Duration:", style = MaterialTheme.typography.titleSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = state.expiryDays.toFloat(),
                    onValueChange = { viewModel.onEvent(CreatePollEvent.ExpiryDaysChanged(it.toInt())) },
                    valueRange = 1f..30f,
                    steps = 29,
                    modifier = Modifier.weight(1f)
                )
                Text("${state.expiryDays} days", modifier = Modifier.padding(start = 8.dp))
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }

        state.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(
                        onClick = { viewModel.onEvent(CreatePollEvent.DismissError) }
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

@Composable
private fun PolicyDropdown(
    policies: List<Policy>,
    selectedPolicy: Policy?,
    onPolicySelected: (Policy) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        CustomTextField(
            value = selectedPolicy?.policyTitle ?: "Select Policy *",
            onValueChange = {},
            label = "Associated Policy",
            leadingIcon = Icons.Default.Category,
            trailingIcon = Icons.Default.ArrowDropDown,
            onTrailingIconClick = { expanded = true },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            policies.forEach { policy ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(policy.policyTitle)
                            Text(
                                "Status: ${policy.policyStatus.displayName}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    onClick = {
                        onPolicySelected(policy)
                        expanded = false
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
private fun PollOptionItem(
    option: PollOption,
    onOptionChanged: (PollOption) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextField(
            value = option.optionText,
            onValueChange = {
                onOptionChanged(option.copy(optionText = it))
            },
            label = "Option Text",
            modifier = Modifier.weight(1f)
        )

        CustomTextField(
            value = option.optionExplanation,
            onValueChange = { newExplanation -> onOptionChanged(option.copy(optionExplanation = newExplanation)) },
            label = "Poll Explanation",
            modifier = Modifier
                .fillMaxWidth()  // Make the text area fill the available width
                .heightIn(min = 100.dp), // Set a minimum height to give more space for input
            maxLines = 5, // Allow the user to enter multiple lines
//            minLines = 3, // Set a minimum line height to make it more spacious
            isSingleLine = false // Allow multi-line input
        )

        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}