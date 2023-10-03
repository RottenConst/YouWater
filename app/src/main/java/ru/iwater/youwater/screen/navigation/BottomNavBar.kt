package ru.iwater.youwater.screen.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun BottomNavBar(navController: NavController, onOpenDriverMenu: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Catalog,
        BottomNavItem.Basket,
        BottomNavItem.Profile,
        BottomNavItem.More
    )

    if (currentRoute == null ||
        currentRoute.contains(MainNavRoute.AboutProductScreen.path) ||
        currentRoute.contains(MainNavRoute.ProductsByCategoryScreen.path) ||
        currentRoute.contains(MainNavRoute.CreateOrderScreen.path) ||
        currentRoute.contains(MainNavRoute.CardPaymentScreen.path) ||
        currentRoute.contains(MainNavRoute.CompleteOrderScreen.path) ||
        currentRoute.contains(MainNavRoute.MyOrderScreen.path) ||
        currentRoute.contains(MainNavRoute.UserDataScreen.path) ||
        currentRoute.contains(MainNavRoute.EditUserDataScreen.path) ||
        currentRoute.contains(MainNavRoute.FavoriteProductScreen.path) ||
        currentRoute.contains(MainNavRoute.AddressesScreen.path) ||
        currentRoute.contains(MainNavRoute.AddAddressScreen.path) ||
        currentRoute == MainNavRoute.NotificationScreen.path ||
        currentRoute == MainNavRoute.FaqScreen.path ||
        currentRoute == MainNavRoute.AboutCompanyScreen.path ||
        currentRoute == MainNavRoute.ContactScreen.path ||
        currentRoute == MainNavRoute.DeliveryInfoScreen.path
    ) {
        return
    }


    BottomNavigation(backgroundColor = Color.White){
        items.forEach {item ->
            val selectedItem = currentRoute == item.screenRoute
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.title)},
                label = {
                    Text(
                        text = item.title,
                        textAlign = TextAlign.Center,
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                        },
                selectedContentColor = Blue500,
                unselectedContentColor = Color.Gray,
                alwaysShowLabel = false,
                selected = selectedItem,
                onClick = {
                    if (!selectedItem && item.screenRoute != "more_item") {
                        navController.navigate(item.screenRoute) {
                            popUpTo(MainNavRoute.HomeScreen.path) { inclusive = true}
                        }
                    } else  if (item.screenRoute == "more_item"){
                        onOpenDriverMenu()
                    }
                })
        }
    }
}

@Composable
fun TopBar(navController: NavController, onEditUserData: () -> Unit, onExitUser: () ->Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val title = when {
        currentRoute == null -> {"YouWatter"}
        currentRoute.contains(MainNavRoute.HomeScreen.path) -> {
            stringResource(id = R.string.home)
        }
        currentRoute.contains(MainNavRoute.AboutProductScreen.path) -> {
            stringResource(id = R.string.info_product)
        }
        currentRoute.contains(MainNavRoute.CatalogScreen.path) -> {
            stringResource(id = R.string.catalog)
        }
        currentRoute.contains(MainNavRoute.BasketScreen.path) -> {
            stringResource(id = R.string.basket)
        }
        currentRoute.contains(MainNavRoute.ProfileMenuScreen.path) -> {
            stringResource(id = R.string.profile)
        }
        currentRoute.contains(MainNavRoute.MyOrderScreen.path) -> {
            stringResource(id = R.string.fragment_profile_my_orders)
        }
        currentRoute.contains(MainNavRoute.UserDataScreen.path) -> {
            stringResource(id = R.string.fragment_profile_my_data)
        }
        currentRoute.contains(MainNavRoute.EditUserDataScreen.path) -> {
            stringResource(id = R.string.edit_user_data)
        }
        currentRoute.contains(MainNavRoute.FavoriteProductScreen.path) -> {
            stringResource(id = R.string.general_favorite)
        }
        currentRoute.contains(MainNavRoute.AddressesScreen.path) -> {
            stringResource(id = R.string.fragment_profile_addresses)
        }
        currentRoute.contains(MainNavRoute.AddAddressScreen.path) -> {
            stringResource(id = R.string.item_add_address_label)
        }
        currentRoute.contains(MainNavRoute.NotificationScreen.path) -> {
            stringResource(id = R.string.fragment_profile_notifications)
        }
        currentRoute.contains(MainNavRoute.CreateOrderScreen.path) -> {
            stringResource(id = R.string.item_order_description_complete_order)
        }
        currentRoute.contains(MainNavRoute.CardPaymentScreen.path) -> {
            stringResource(id = R.string.payment_on_card_logo)
        }
        currentRoute.contains(MainNavRoute.ContactScreen.path) -> {
            stringResource(id = R.string.send_text)
        }
        currentRoute.contains(MainNavRoute.AboutCompanyScreen.path) -> {
            stringResource(id = R.string.information_text)
        }
        currentRoute.contains(MainNavRoute.DeliveryInfoScreen.path) -> {
            stringResource(id = R.string.delivery_text)
        }
        currentRoute.contains(MainNavRoute.FaqScreen.path) -> {
            stringResource(id = R.string.faq_label)
        }
        else -> {
            "YouWatter"
        }
    }

    when {
        currentRoute == null ||
                currentRoute.contains(MainNavRoute.HomeScreen.path) ||
                currentRoute.contains(MainNavRoute.CatalogScreen.path) ||
                currentRoute.contains(MainNavRoute.BasketScreen.path) ||
                currentRoute.contains(MainNavRoute.ProfileMenuScreen.path) ||
                currentRoute.contains(MainNavRoute.CompleteOrderScreen.path) ||
                currentRoute.contains(MainNavRoute.CardPaymentScreen.path) -> {
            TopAppBar {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = title,
                    style = YouWaterTypography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f, true))
                IconButton(onClick = { onExitUser() }) {
                    Icon(painter = painterResource(id = R.drawable.ic_exit), contentDescription = stringResource(
                        id = R.string.Exit
                    ), tint = Color.White)
                }
            }
        }
        currentRoute.contains(MainNavRoute.UserDataScreen.path) -> {
            TopAppBar{
                IconButton(onClick = { navController.navigate(MainNavRoute.ProfileMenuScreen.path) }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "backIcon", tint = Color.White)
                }
                Text(
                    text = title,
                    style = YouWaterTypography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f, true))
                IconButton(onClick = { navController.navigate(MainNavRoute.EditUserDataScreen.path) }) {
                    Icon(painter = painterResource(id = R.drawable.ic_mode_edit_24), contentDescription = stringResource(
                        id = R.string.Exit
                    ), tint = Color.White)
                }
            }
        }
        currentRoute.contains(MainNavRoute.EditUserDataScreen.path) -> {
            TopAppBar{
                IconButton(onClick = { navController.navigate(MainNavRoute.UserDataScreen.withArgs(false.toString())){
                    popUpTo(MainNavRoute.UserDataScreen.path) {inclusive = true}
                } }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "backIcon", tint = Color.White)
                }
                Text(
                    text = title,
                    style = YouWaterTypography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f, true))
                IconButton(onClick = { onEditUserData() }) {
                    Icon(painter = painterResource(id = R.drawable.ic_check_24), contentDescription = stringResource(
                        id = R.string.Exit
                    ), tint = Color.White)
                }
            }
        }
        else -> {
            TopAppBar{
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "backIcon", tint = Color.White)
                }
                Text(
                    text = title,
                    style = YouWaterTypography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f, true))
                IconButton(onClick = { onExitUser() }) {
                    Icon(painter = painterResource(id = R.drawable.ic_exit), contentDescription = stringResource(
                        id = R.string.Exit
                    ), tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun DrawerHeader(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp)
        ,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(painter = painterResource(id = R.drawable.ic_your_water_logo), contentDescription = stringResource(
            id = R.string.description_image_logo
        ))
    }
}

@Composable
private fun DrawerMenuItem(
    iconDrawableId: Int,
    text: String,
    onItemClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Icon(
            painter = painterResource(iconDrawableId),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text )
    }
}

@Composable
fun DrawerBody(navController: NavHostController?, closeNavDrawer: () -> Unit) {
    Column {
        DrawerMenuItem(
            iconDrawableId = R.drawable.ic_delivery,
            text = stringResource(id = R.string.delivery_text),
            onItemClick = {
                navController?.navigate(MainNavRoute.DeliveryInfoScreen.path)
                closeNavDrawer()
            }
        )
        DrawerMenuItem(
            iconDrawableId = R.drawable.ic_information,
            text = stringResource(id = R.string.information_text),
            onItemClick = {
                navController?.navigate(MainNavRoute.AboutCompanyScreen.path)
                closeNavDrawer()
            }
        )
        DrawerMenuItem(
            iconDrawableId = R.drawable.ic_faq,
            text = stringResource(id = R.string.faq_label),
            onItemClick = {
                navController?.navigate(MainNavRoute.FaqScreen.path)
                closeNavDrawer()
            }
        )
        DrawerMenuItem(
            iconDrawableId = R.drawable.ic_send,
            text = stringResource(id = R.string.send_text),
            onItemClick = {
                navController?.navigate(MainNavRoute.ContactScreen.path)
                closeNavDrawer()
            }
        )
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    YourWaterTheme {
//        BottomNavBar()
    }
}

@Preview
@Composable
fun TopAppBarPreview() {
    YourWaterTheme {
//        TopBar("asd")
    }
}