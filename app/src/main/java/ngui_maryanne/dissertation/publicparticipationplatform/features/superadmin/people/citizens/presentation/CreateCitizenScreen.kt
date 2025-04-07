package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.citizens.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun CreateCitizenScreen(viewModel: CreateCitizenViewModel = hiltViewModel()) {
    val uiState = viewModel.state.value
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Image Picker
        Card(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { imagePickerLauncher.launch("image/*") },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                selectedImageUri?.let {
                    viewModel.onEvent(AddCitizenEvent.SelectedProfileImage(it))
                    AsyncImage(
                        model = it,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Add Image",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onEvent(AddCitizenEvent.EnteredName(it)) },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.onEvent(AddCitizenEvent.EnteredPhone(it)) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.nationalId,
            onValueChange = { viewModel.onEvent(AddCitizenEvent.EnteredNationalId(it)) },
            label = { Text("National ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.onEvent(AddCitizenEvent.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedImageUri != null // Ensure an image is selected

        ) {
            Text("Add Citizen")
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        uiState.successMessage?.let {
            Text(it, color = Color.Green, modifier = Modifier.padding(8.dp))
        }

        uiState.errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(8.dp))
        }
    }
}
