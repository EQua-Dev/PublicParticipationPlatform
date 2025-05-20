package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetScreen(
    viewModel: OfficialBudgetViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(state.creationSuccess) {
        if (state.creationSuccess) {
            navController.popBackStack()
            viewModel.onEvent(OfficialBudgetEvent.OnResetCreateState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_budget)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CustomTextField(
                value = state.amount,
                onValueChange = { viewModel.onEvent(OfficialBudgetEvent.OnAmountChanged(it)) },
                label = stringResource(R.string.total_budget_amount),
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = state.budgetNote,
                onValueChange = { viewModel.onEvent(OfficialBudgetEvent.OnNoteChanged(it)) },
                label = stringResource(R.string.budget_note),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = state.impact,
                onValueChange = { viewModel.onEvent(OfficialBudgetEvent.OnImpactChanged(it)) },
                label = stringResource(R.string.impact),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.budget_options), style = MaterialTheme.typography.titleMedium)

                TextButton(
                    onClick = { viewModel.onEvent(OfficialBudgetEvent.OnAddBudgetOption) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_option)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.add_option))
                }
            }

            state.budgetOptions.forEachIndexed { index, option ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.option, index + 1), style = MaterialTheme.typography.labelLarge)
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        viewModel.onEvent(
                            OfficialBudgetEvent.OnBudgetOptionImageSelected(
                                index,
                                uri
                            )
                        )
                    }
                }
                CustomTextField(
                    value = option.projectName,
                    onValueChange = {
                        viewModel.onEvent(
                            OfficialBudgetEvent.OnBudgetOptionChanged(
                                index, option.copy(projectName = it)
                            )
                        )
                    },
                    label = stringResource(R.string.project_name),
                )

                CustomTextField(
                    value = option.description,
                    onValueChange = {
                        viewModel.onEvent(
                            OfficialBudgetEvent.OnBudgetOptionChanged(
                                index, option.copy(description = it)
                            )
                        )
                    },
                    label = stringResource(R.string.description),
                )

                CustomTextField(
                    value = option.amount,
                    onValueChange = {
                        viewModel.onEvent(
                            OfficialBudgetEvent.OnBudgetOptionChanged(
                                index, option.copy(amount = it)
                            )
                        )
                    },
                    label = stringResource(R.string.amount),
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(4.dp))
                CustomButton(text = stringResource(R.string.upload_image), onClick = { launcher.launch("image/*") })

                option.imageUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { viewModel.submitBudget() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                text = stringResource(R.string.create_budget)
            )

            if (state.isLoading) {
                FullScreenLoading(

                )
//                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
