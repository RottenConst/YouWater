package ru.iwater.youwater.screen.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar() },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Navigation(navController)
            }
        }
    )
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Home.screenRoute) {
        composable(BottomNavItem.Home.screenRoute) {

        }
        composable(BottomNavItem.Catalog.screenRoute) {

        }
        composable(BottomNavItem.Basket.screenRoute) {

        }
        composable(BottomNavItem.Profile.screenRoute) {

        }
        composable(BottomNavItem.More.screenRoute) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    YourWaterTheme {
        MainScreen()
    }
}