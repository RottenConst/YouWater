package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.iwater.youwater.R
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun AddressesScreen(
    watterViewModel: WatterViewModel = viewModel(),
    navController: NavHostController
) {

    LaunchedEffect(Unit){
        watterViewModel.getAddressesList()
    }

    val modifier = Modifier
    val addressList = watterViewModel.addressesList
    Column(modifier = modifier.fillMaxSize()) {
        MenuButton(
            modifier = modifier,
            painter = painterResource(id = R.drawable.ic_black_place),
            tint = Color.Black,
            nameButton = stringResource(id = R.string.fragment_add_address_label),
            description = ""
        ) {
            navController.navigate(
                MainNavRoute.AddAddressScreen.withArgs(false.toString())
            )
        }
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(count = addressList.size) {addressIndex ->
                AddressCard(
                    modifier = modifier,
                    address = addressList[addressIndex].factAddress,
                    notice = addressList[addressIndex].notice,
                    deleteAddress = {
                        watterViewModel.inActiveAddress(addressList[addressIndex].id)
                    }
                )
            }

        }
    }
}

@Composable
fun AddressCard(modifier: Modifier, address: String, notice: String?, deleteAddress: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
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
                   tint = Blue500,
                   modifier = modifier.padding(4.dp)
               )
               Text(
                   text = address,
                   textAlign = TextAlign.Start,
                   style = YouWaterTypography.body2,
                   color = Blue500,
                   modifier = modifier
                       .weight(1f)
                       .padding(4.dp)
               )
               IconButton(onClick = { deleteAddress() }) {
                   Icon(
                       imageVector = Icons.Sharp.Close,
                       contentDescription = "",
                       tint = Color.LightGray,
                       modifier = modifier.padding(4.dp)
                   )
               }
           }
           if (!notice.isNullOrEmpty()) {
               Divider(modifier = modifier, color = Color.LightGray, 1.dp)
               Text(
                   text = notice,
                   textAlign = TextAlign.Start,
                   style = YouWaterTypography.subtitle2,
                   color = Blue500,
                   fontStyle = FontStyle.Italic,
                   modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
               )
           }
       }
    }
}

@Preview
@Composable
fun AddressesScreenPreview() {
    val modifier = Modifier
    YourWaterTheme {
        Column(modifier = modifier.fillMaxSize()) {
            MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_black_place), tint = Color.Black, fontWeight = FontWeight.Bold, nameButton = stringResource(id = R.string.fragment_add_address_label), description = "") {
                
            }
            AddressCard(modifier = modifier, "Питер , Некрасова , д. 1, корп. 2, подъезд 3, этаж 4, кв. 5", notice = "Примичание"){}
        }
    }
}