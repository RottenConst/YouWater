package ru.iwater.youwater.screen.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun PaymentScreen(modifier: Modifier = Modifier, watterViewModel: WatterViewModel, orderId: Int) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    watterViewModel.startPay()
    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        PaymentNavGraph(navController = navController, watterViewModel = watterViewModel, modifier = modifier.padding(paddingValues = paddingValues), orderId = orderId)
    }
}