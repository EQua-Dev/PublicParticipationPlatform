package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun FabMenu(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit,
    onAddCitizen: () -> Unit,
    onAddOfficial: () -> Unit
) {
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        FloatingActionButton(
            onClick = {
                onToggle(!isExpanded)
                Log.d("TAG", "FabMenu: $isExpanded")
            },
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onToggle(false) }
        ) {
            DropdownMenuItem(onClick = {
                onToggle(false)
                onAddCitizen()
            }, leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null) }, text = {
                Text(
                    "Add Citizen"
                )
            })

            DropdownMenuItem(onClick = {
                onToggle(false)
                onAddOfficial()
            }, leadingIcon = {
                Icon(Icons.Default.AccountBox, contentDescription = null)
            }, text = { Text("Add Official") })
        }
    }
}

@Composable
fun ExpandableFloatingActionButton(
    isExpanded: Boolean,
    onFabClick: () -> Unit,
    onAddCitizen: () -> Unit,
    onAddOfficial: () -> Unit
) {
    var expanded by remember { mutableStateOf(isExpanded) }
    val fabSize = 64.dp
    val expandedWidth by animateDpAsState(
        targetValue = if (expanded) 200.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )
    val expandedHeight by animateDpAsState(
        targetValue = if (expanded) 58.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )

    Column {

        Box(
            modifier = Modifier
                .offset(y = 25.dp)
                .size(
                    width = expandedWidth,
                    height = animateDpAsState(
                        if (expanded) 160.dp else 0.dp,
                        animationSpec = spring(dampingRatio = 4f)
                    ).value
                )
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Column {
                AnimatedVisibility(visible = expanded) {
                    Column {
                        FloatingActionButton(
                            onClick = {
                                expanded = false
                                onAddCitizen()
                            },
                            modifier = Modifier.padding(4.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                        ) {
                            Row {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Citizen")
                            Text(text = "Add Citizen")
                            }
                        }
                        FloatingActionButton(
                            onClick = {
                                expanded = false
                                onAddOfficial()
                            },
                            modifier = Modifier.padding(4.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                        ) {
                            Row {
                            Icon(Icons.Default.AccountBox, contentDescription = "Add Official")
                            Text(text = "Add Official")
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                expanded = !expanded
                onFabClick()
            },
            modifier = Modifier
                .width(expandedWidth)
                .height(expandedHeight),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .offset(
                        x = animateDpAsState(
                            if (expanded) -70.dp else 0.dp,
                            animationSpec = spring(dampingRatio = 3f)
                        ).value
                    )
            )

            Text(
                text = "Add",
                softWrap = false,
                modifier = Modifier
                    .offset(
                        x = animateDpAsState(
                            if (expanded) 10.dp else 50.dp,
                            animationSpec = spring(dampingRatio = 3f)
                        ).value
                    )
                    .alpha(
                        animateFloatAsState(
                            targetValue = if (expanded) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = if (expanded) 350 else 100,
                                delayMillis = if (expanded) 100 else 0,
                                easing = EaseIn
                            )
                        ).value
                    )
            )
        }
    }
}
