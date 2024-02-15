package ru.iwater.youwater.screen.component.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography

@Composable
fun AddressAndTimeOrder(
    addressList: List<NewAddress>,
    checkAddressDialog: Boolean,
    selectedAddress: Int,
    dateOrder: String,
    onShowDialog: () -> Unit,
    setAddressOrder: (NewAddress) -> Unit,
    showDatePickerDialog: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onShowDialog()
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    imageVector = Icons.Outlined.LocationOn,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(id = R.string.info_product)
                )
                Text(
                    text = if (addressList.isEmpty()) {
                        "Добавить адрес"
                    } else {
                        when (selectedAddress) {
                            -1 -> "Выбрать адрес"
                            else -> {
                                addressList[selectedAddress].address
                            }
                        }
                    },
//                    style = YouWaterTypography.body1,
                    textAlign = TextAlign.Center,
//                    color = Blue500,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline,
                )
            }
            if (selectedAddress != -1) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            showDatePickerDialog()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time_24),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = dateOrder.ifEmpty { "Укажите дату" },
//                        style = YouWaterTypography.body1,
                        textAlign = TextAlign.Start,
//                        color = Blue500
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }

    if (checkAddressDialog) {
        SetAddressDialog(
            addressList = addressList,
            onShowDialog = { onShowDialog() },
            setAddressOrder = setAddressOrder
        )
    }
}

@Composable
private fun SetAddressDialog(
    addressList: List<NewAddress>,
    onShowDialog: () -> Unit,
    setAddressOrder: (NewAddress) -> Unit
) {
    var addressSelected by remember {
        mutableStateOf(addressList[0])
    }
    AlertDialog(
        modifier = Modifier.padding(top = 32.dp, bottom = 32.dp),
//        containerColor = Color.White,
        onDismissRequest = { onShowDialog() },
        icon = {Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )},
        title = {
            Text(
                text = "Bыберете адрес",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                addressList.forEach { address ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (address == addressSelected),
                                onClick = { addressSelected = address }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = (address == addressSelected),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(text = "${address.city} ${address.street} д.${address.house} ${address.block} кв.${address.flat}")
                    }
                }
            }
        },
        shape = RoundedCornerShape(8.dp),
        dismissButton = {
            TextButton(onClick = { onShowDialog() }) {
                Text(text = "Отмена", color = MaterialTheme.colorScheme.primary)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onShowDialog()
                setAddressOrder(addressSelected)
            }
            ) {
                Text(text = "Выбрать", color = MaterialTheme.colorScheme.primary)
            }
        },
    )
}