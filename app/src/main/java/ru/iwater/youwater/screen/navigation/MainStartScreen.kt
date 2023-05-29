package ru.iwater.youwater.screen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.AuthViewModel

@Composable
fun MainStartScreen(authViewModel: AuthViewModel, startActivity: StartActivity) {
    YourWaterTheme {
        val navController = rememberNavController()
        StartNavGraph(navController = navController, authViewModel = authViewModel, startActivity = startActivity)
    }
}