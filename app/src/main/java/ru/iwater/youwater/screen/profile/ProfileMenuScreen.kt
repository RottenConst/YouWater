package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.iwater.youwater.R
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun ProfileScreen(clientProfileViewModel: ClientProfileViewModel = viewModel(),navController: NavController) {
    val modifier = Modifier
    val client by clientProfileViewModel.client.observeAsState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 60.dp)) {
        NameUser(modifier = modifier, nameUser = client?.name ?: "")
        MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_orders), tint = Blue500, nameButton = stringResource(
            id = R.string.fragment_profile_my_orders
        ), description = "") {
            navController.navigate(
                ProfileFragmentDirections.actionProfileFragmentToMyOrdersFragment()
            )
        }
        MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_info), tint = Blue500, nameButton = stringResource(
            id = R.string.fragment_profile_my_data
        ), description = "") {
            navController.navigate(
                ProfileFragmentDirections.actionProfileFragmentToUserDataFragment(false)
            )
        }
        MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_favorite), tint = Blue500, nameButton = stringResource(
            id = R.string.general_favorite
        ), description = "") {
            navController.navigate(
                ProfileFragmentDirections.actionProfileFragmentToFavoriteFragment()
            )
        }
        MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_address), tint = Blue500, nameButton = stringResource(
            id = R.string.fragment_profile_addresses
        ), description = "") {
            navController.navigate(
                ProfileFragmentDirections.actionProfileFragmentToAddresessFragment()
            )
        }
    }
}

@Composable
fun NameUser(modifier: Modifier,nameUser: String) {
    Box(modifier = modifier.padding(16.dp)) {
        Text(
            text = nameUser,
            fontWeight = FontWeight.Bold,
            style = YouWaterTypography.h5
        )
    }
}

@Composable
fun MenuButton(modifier: Modifier, painter: Painter, tint: Color, fontWeight: FontWeight? = null, nameButton: String, description: String, toScreen: () -> Unit) {
    Box(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable { toScreen() },
        contentAlignment = Alignment.CenterStart
    ){
        Row(
            modifier = modifier
                .fillMaxSize()
                .border(width = 1.dp, color = Color.LightGray)
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painter,
                contentDescription = description,
                tint = tint,
                modifier = modifier.padding(16.dp)
            )
            Text(
                text = nameButton,
                style = YouWaterTypography.h6,
                fontWeight = fontWeight
            )
        }
    }
}


@Preview
@Composable
fun ProfileScreenPreview() {
    YourWaterTheme {
        val modifier = Modifier
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)) {
            NameUser(modifier = modifier, nameUser = "Екатерина Иванова")
            MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_orders), tint = Blue500, nameButton = stringResource(
                id = R.string.fragment_profile_my_orders
            ), description = "") {}
            MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_info), tint = Blue500, nameButton = stringResource(
                id = R.string.fragment_profile_my_data
            ), description = "") {}
            MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_favorite), tint = Blue500, nameButton = stringResource(
                id = R.string.general_favorite
            ), description = "") {}
            MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_address), tint = Blue500, nameButton = stringResource(
                id = R.string.fragment_profile_addresses
            ), description = "") {}
            MenuButton(modifier = modifier, painter = painterResource(id = R.drawable.ic_notification), tint = Blue500, nameButton = stringResource(
                id = R.string.general_notice
            ), description = "") {}
        }
    }
}