package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Country

@Composable
fun PhoneNumberInput(
    selectedCountry: Country,
    onCountryChange: (Country) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = "${selectedCountry.dialCode} $phoneNumber",
            onValueChange = {
                // Only update the phone number part
                val input = it.removePrefix("${selectedCountry.dialCode} ").trim()
                onPhoneNumberChange(input)
            },
            label = { Text("Phone Number") },
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { showDialog = true }
                        .padding(start = 8.dp)
                ) {
                    Text(text = selectedCountry.flag, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = selectedCountry.dialCode, fontSize = 14.sp)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select country"
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardType.Phone
        )

        if (showDialog) {
            CountryPickerDialog(
                onDismiss = { showDialog = false },
                onCountrySelected = {
                    onCountryChange(it)
                    showDialog = false
                }
            )
        }
    }
}
