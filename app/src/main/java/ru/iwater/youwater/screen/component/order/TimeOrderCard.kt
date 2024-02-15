package ru.iwater.youwater.screen.component.order

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetTimeOrderCard(timeListOrder: List<String>, selectedTime: String, expandedTime: Boolean, setTimeOrder: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expandedTime,
            onExpandedChange = {
                setTimeOrder("**:**-**:**")
            }
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selectedTime,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(id = R.string.fragment_create_order_time_order)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTime)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expandedTime,
                onDismissRequest = { setTimeOrder("**:**-**:**") }
            ) {
                timeListOrder.forEach {timeOrder ->
                    DropdownMenuItem(
                        onClick = {
                            setTimeOrder(timeOrder)
                        },
                        text = {
                            Text(
                                text = timeOrder,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                    )
                }


            }
        }
    }
}