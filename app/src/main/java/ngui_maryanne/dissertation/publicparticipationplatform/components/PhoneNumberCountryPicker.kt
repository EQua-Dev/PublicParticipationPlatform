package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Country

@Composable
fun CountryPickerDialog(
    onDismiss: () -> Unit,
    onCountrySelected: (Country) -> Unit
) {
    val countries = listOf(
        Country("Kenya", "+254", "KE", "\uD83C\uDDF0\uD83C\uDDEA"),
        Country("Ireland", "+353", "IE", "\uD83C\uDDEE\uD83C\uDDEA"),
        Country("Nigeria", "+234", "NG", "\uD83C\uDDF3\uD83C\uDDEC"),
        Country("United States", "+1", "US", "\uD83C\uDDFA\uD83C\uDDF8"),
        Country("United Kingdom", "+44", "GB", "\uD83C\uDDEC\uD83C\uDDE7"),
        Country("India", "+91", "IN", "\uD83C\uDDEE\uD83C\uDDF3"),


        // Add more as needed
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(countries) { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCountrySelected(country) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = country.flag, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = country.name)
                            Text(text = country.dialCode, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
