package ngui_maryanne.dissertation.publicparticipationplatform.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilePicker(
    onFileSelected: (Uri) -> Unit
) {
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for file picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                fileUri = it
                onFileSelected(it)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { launcher.launch("*/*") }, // Allow all file types
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Section Material")
        }

        if (fileUri != null) {
            Text(
                text = "Selected File: ${fileUri!!.lastPathSegment}",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}