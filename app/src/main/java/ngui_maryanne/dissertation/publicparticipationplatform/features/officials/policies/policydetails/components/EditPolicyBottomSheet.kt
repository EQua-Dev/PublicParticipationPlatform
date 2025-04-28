package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy.CreatePolicyEvent
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy.PolicyCoverImageSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPolicyBottomSheet(
    policy: Policy?,
    onDismiss: () -> Unit,
    onSave: (String, String, Map<String, Any?>) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(policy?.policyTitle ?: "") }
    var imageUrl by remember { mutableStateOf(policy?.policyCoverImage ?: "") }
    var otherDetails by remember { mutableStateOf<Map<String, Any?>>(emptyMap()) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUrl = it.toString() }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                PolicyCoverImageSection(
                    imageUri = imageUrl.toUri(),
                    onImageClick = { imagePicker.launch("image/*") }
                )

                CustomTextField(value = name, onValueChange = { name = it }, label = "Policy Name")

                Spacer(modifier = Modifier.height(8.dp))
           /*     TextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )*/
                // TODO: Add more fields for "otherDetails" if needed

                Spacer(modifier = Modifier.height(16.dp))

                CustomButton(
                    onClick = { onSave(name, imageUrl, otherDetails) },
                    modifier = Modifier.fillMaxWidth(),
                    text = "Save Changes"
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onDelete() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Policy")
                }
            }
        }
    )
}
