package ru.iwater.youwater.screen.navigation

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun BottomNavBar() {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Catalog,
        BottomNavItem.Basket,
        BottomNavItem.Profile,
        BottomNavItem.More
    )
    BottomNavigation(backgroundColor = Color.White){
        items.forEach {item ->
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.title)},
                label = { Text(text = item.title)},
                selectedContentColor = Blue500,
                unselectedContentColor = Color.Gray,
                alwaysShowLabel = false,
                selected = false,
                onClick = { /*TODO*/ })
        }
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "YouWater")}
    )
}

@Preview
@Composable
fun BottomNavBarPreview() {
    YourWaterTheme {
        BottomNavBar()
    }
}

@Preview
@Composable
fun TopAppBarPreview() {
    YourWaterTheme {
        TopBar()
    }
}