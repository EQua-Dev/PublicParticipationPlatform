package awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.officials.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Official
import coil.compose.AsyncImage

@Composable
fun OfficialListScreen(
    viewModel: OfficialListViewModel = hiltViewModel()
) {
    val officials by viewModel.uiState
    val errorMessage by viewModel.errorMessage

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Officials List",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
        }

        if (officials.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No officials found")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(officials) { official ->
                    OfficialItem(official)
                }
            }
        }
    }
}

@Composable
fun OfficialItem(official: Official) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = official.profileImageUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "${official.firstName} ${official.lastName}",
                    fontWeight = FontWeight.Bold
                )
                Text(text = official.email, fontSize = 14.sp, color = Color.Gray)
                Text(text = "Phone: ${official.phoneNumber}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
