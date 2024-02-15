package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.iwater.youwater.R
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun AddressesScreen(
    addressList1: List<NewAddress>,
    deleteAddress: (Int) -> Unit,
    navController: NavHostController
) {

    val modifier = Modifier
    Column(modifier = modifier.fillMaxSize()) {
        MenuButton(
            modifier = modifier.background(color = MaterialTheme.colorScheme.onPrimary),
            painter = painterResource(id = R.drawable.ic_black_place),
            tint = MaterialTheme.colorScheme.primary,
            nameButton = stringResource(id = R.string.fragment_add_address_label),
            description = ""
        ) {
            navController.navigate(
                MainNavRoute.AddAddressScreen.withArgs(false.toString())
            )
        }
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(count = addressList1.size) {addressIndex ->
                AddressCard(
                    modifier = modifier,
                    address = addressList1[addressIndex].address,//address,//addressList[addressIndex].address,
                    notice = addressList1[addressIndex].notice,
                    deleteAddress = {
                        deleteAddress(addressList1[addressIndex].id)
                    }
                )
            }

        }
    }
}

@Composable
fun AddressCard(modifier: Modifier, address: String, notice: String?, deleteAddress: () -> Unit) {
    var isVisibleDialog by remember {
        mutableStateOf(false)
    }
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        DeleteAddressDialog(isVisible = isVisibleDialog, setVisible = {isVisibleDialog = !isVisibleDialog}) {
            deleteAddress()
        }
        Column(modifier = modifier.fillMaxWidth()) {
           Row(modifier = modifier
               .padding(8.dp)
               .fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
           ) {
               Icon(
                   imageVector = Icons.Sharp.LocationOn,
                   contentDescription = "",
                   tint = MaterialTheme.colorScheme.primary,
                   modifier = modifier.padding(4.dp)
               )
               Text(
                   text = address,
                   textAlign = TextAlign.Start,
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.primary,
                   modifier = modifier
                       .weight(1f)
                       .padding(4.dp)
               )
               IconButton(onClick = { isVisibleDialog = true }) {
                   Icon(
                       imageVector = Icons.Sharp.Close,
                       contentDescription = "",
                       tint = MaterialTheme.colorScheme.outline,
                       modifier = modifier.padding(4.dp)
                   )
               }
           }
           if (!notice.isNullOrEmpty()) {
               HorizontalDivider(modifier = modifier, color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
               Text(
                   text = notice,
                   textAlign = TextAlign.Start,
                   style = MaterialTheme.typography.labelLarge,
                   color = MaterialTheme.colorScheme.primary,
                   fontStyle = FontStyle.Italic,
                   modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
               )
           }
       }
    }
}

@Composable
fun DeleteAddressDialog(isVisible: Boolean, setVisible: (Boolean) -> Unit, deleteDialog: () -> Unit) {
    if (isVisible) {
        AlertDialog(
            icon = {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(id = R.string.delete_address_text), tint = MaterialTheme.colorScheme.primary )
            },
            title = {
                Text(text = stringResource(id = R.string.delete_address_quest_text))
            },
            onDismissRequest = { setVisible(false) },
            dismissButton = {
                TextButton(onClick = { setVisible(false) }) {
                    Text(text = stringResource(id = R.string.general_no), color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    setVisible(false)
                    deleteDialog()
                }) {
                    Text(text = stringResource(id = R.string.general_yes), color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Preview
@Composable
fun AddressesScreenPreview() {
    val modifier = Modifier
    YourWaterTheme {
        Column(modifier = modifier.fillMaxSize()) {
            MenuButton(
                modifier = modifier,
                painter = painterResource(id = R.drawable.ic_black_place),
                tint = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                nameButton = stringResource(id = R.string.fragment_add_address_label), description = ""
            ) {
                
            }
            AddressCard(modifier = modifier, "Питер , Некрасова , д. 1, корп. 2, подъезд 3, этаж 4, кв. 5", notice = "Примичание"){}
        }
    }
}