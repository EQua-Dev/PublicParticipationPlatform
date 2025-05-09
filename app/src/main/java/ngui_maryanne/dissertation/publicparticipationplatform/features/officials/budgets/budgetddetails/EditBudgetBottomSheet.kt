package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetOption
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.OfficialBudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetBottomSheet(
    viewModel: OfficialBudgetViewModel = hiltViewModel(),
    budget: Budget,
    onSave: (amount: String, note: String, impact: String, budgetOptions: MutableList<BudgetOption>) -> Unit,
    onClose: () -> Unit
) {

    val scrollState = rememberScrollState()

    // Top-level budget fields
    var amount by remember { mutableStateOf(budget.amount) }
    var note by remember { mutableStateOf(budget.budgetNote) }
    var impact by remember { mutableStateOf(budget.impact) }

    // Options as editable mutable list
    val budgetOptions = remember { mutableStateListOf<BudgetOption>().apply { addAll(budget.budgetOptions) } }


    val context = LocalContext.current

    /*   LaunchedEffect(state.editSuccess) {
           if (state.editSuccess) {
               onClose()
               viewModel.onEvent(OfficialBudgetEvent.OnResetEditState)
           }
       }
   */
    ModalBottomSheet(
        onDismissRequest = { onClose() },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),

                ) {
                Text("Edit Budget", style = MaterialTheme.typography.titleLarge)

                CustomTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Total Budget Amount",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Budget Note",
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = impact,
                    onValueChange = { impact = it },
                    label = "Impact",
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Budget Options", style = MaterialTheme.typography.titleMedium)


                budgetOptions.forEachIndexed { index, option ->
                    Spacer(modifier = Modifier.height(12.dp))
                    var projectName by remember { mutableStateOf(option.optionProjectName) }
                    var description by remember { mutableStateOf(option.optionDescription) }
                    var optionAmount by remember { mutableStateOf(option.optionAmount) }
                    val imageUri = remember { mutableStateOf(option.imageUrl?.let { Uri.parse(it) }) }

//                    val imageUri = remember { mutableStateOf<Uri?>(null) }
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri ->
                        uri?.let {
                            imageUri.value = it
                            budgetOptions[index] =
                                budgetOptions[index].copy(imageUrl = it.toString()) // Add imageUri to model
                        }
                    }

                    CustomTextField(
                        value = projectName,
                        onValueChange = {
                            projectName = it
                            budgetOptions[index] = option.copy(optionProjectName = it)
                        },
                        label = "Project Name"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            budgetOptions[index] = option.copy(optionDescription = it)
                        },
                        label = "Description"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = optionAmount,
                        onValueChange = {
                            optionAmount = it
                            budgetOptions[index] = option.copy(optionAmount = it)
                        },
                        label = "Amount",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    imageUri.value?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    TextButton(onClick = { launcher.launch("image/*") }) {
                        Text(if (imageUri.value == null) "Add Image" else "Change Image")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    text = "Save Changes",
                    onClick = {
                        onSave(amount, note, impact, budgetOptions)
//                viewModel.onEvent(OfficialBudgetEvent.OnSubmitBudgetEdit(budgetId))
                    }
                )

                OutlinedButton(onClick = { onClose() }) {
                    Text(text = "Cancel")
                }
            }
        }
    )

}
