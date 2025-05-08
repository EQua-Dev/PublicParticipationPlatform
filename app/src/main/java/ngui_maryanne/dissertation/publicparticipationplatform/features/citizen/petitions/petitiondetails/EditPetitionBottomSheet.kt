package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy.PolicyCoverImageSection
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetitionBottomSheet(
    state: PetitionDetailsState,
    petition: Petition?,
    onDismiss: () -> Unit,
    onSave: (String, String, Map<String, Any?>) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(petition?.title ?: "") }
    var description by remember { mutableStateOf(petition?.description ?: "") }
    var imageUrl by remember { mutableStateOf(petition?.coverImage ?: "") }
    var otherDetails by remember { mutableStateOf<Map<String, Any?>>(emptyMap()) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUrl = it.toString() }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        content = {
            Box( Modifier
                .fillMaxSize()
                .imePadding()) {
                if (state.isLoading){
                    LoadingDialog()
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    // Petition Cover Image Section
                    PolicyCoverImageSection(
                        imageUri = imageUrl.toUri(),
                        onImageClick = { imagePicker.launch("image/*") }
                    )

                    // Title Text Field
                    CustomTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Petition Title"
                    )

                    // Description Text Field
                    CustomTextField(value = description, onValueChange = { newDescription ->
                        description = newDescription
                        otherDetails = otherDetails.toMutableMap().apply {
                            put("description", newDescription)
                        }
                    }, label = "Description")

                    Spacer(modifier = Modifier.height(8.dp))

                    // Other details (if needed)
                    // Add more fields for "otherDetails" as required

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Changes Button
                    CustomButton(
                        onClick = { onSave(title, imageUrl, otherDetails) },
                        modifier = Modifier.fillMaxWidth(),
                        text = "Save Changes",
                        enabled = !state.isLoading
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Delete Button
                    OutlinedButton(
                        onClick = { onDelete() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text("Delete Petition")
                    }
                }
            }

        }
    )
}