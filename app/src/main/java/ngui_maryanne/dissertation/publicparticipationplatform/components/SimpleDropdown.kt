

package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AssimOutlinedDropdown(
    modifier: Modifier = Modifier,
    label: String,
    hint: String,
    options: List<Pair<String, Any>>, // Label to Value mapping
    selectedValue: Any?,
    onValueSelected: (Any) -> Unit,
    error: String? = null,
    isSearchable: Boolean = false,
    showLabelAbove: Boolean = true, // Label above the text field
    isCompulsory: Boolean = false, // Show asterisk if required
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedLabel by remember {
        mutableStateOf(
            options.find { it.second == selectedValue }?.first ?: ""
        )
    }

    val filteredOptions = if (isSearchable) {
        options.filter { it.first.contains(searchQuery, ignoreCase = true) }
    } else {
        options
    }
    Column {
        // Label above the text field if enabled
        if (showLabelAbove) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isCompulsory) {
                    Text(
                        text = " *",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Box(
            modifier = modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                ,
                label = { Text(hint) },
                readOnly = true,
                isError = error != null,
                supportingText = {
                    if (error != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown Arrow",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSearchable) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        singleLine = true
                    )
                }

                filteredOptions.forEach { (label, value) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedLabel = label
                            onValueSelected(value)
                            expanded = false
                            searchQuery = "" // Reset search on selection
                        }
                    )
                }
            }
        }

    }


}
/*

@AssimParentPreview
@Composable
fun DropdownPreview(modifier: Modifier = Modifier) {
    AssimParentTheme {
        Surface {
            var selectedItem by remember { mutableStateOf<Any?>(null) }

            val options = listOf(
                "Option 1" to 1,
                "Option 2" to 2,
                "Option 3" to 3
            )

            Column(modifier = Modifier.padding(16.dp)) {
                AssimOutlinedDropdown(
                    label = "Option",
                    hint = "Select an option",
                    options = options,
                    selectedValue = selectedItem,
                    onValueSelected = { selectedItem = it },
                    isCompulsory = true,
                    isSearchable = true // Enable search functionality

                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Selected Value: $selectedItem")
            }
        }
    }
}
*/
