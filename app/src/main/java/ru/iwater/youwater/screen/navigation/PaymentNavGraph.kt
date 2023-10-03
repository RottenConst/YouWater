package ru.iwater.youwater.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.iwater.youwater.screen.basket.CompleteOrderScreen
import ru.iwater.youwater.screen.basket.LoadUrl
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun PaymentNavGraph(modifier: Modifier = Modifier, navController: NavHostController, watterViewModel: WatterViewModel, orderId: Int) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = PaymentNavRoute.CompleteOrderScreen.path
    ) {
        addCompleteOrderScreen(watterViewModel = watterViewModel, orderId = orderId, navController = navController, navGraphBuilder = this)
        addCheckPayScreen(watterViewModel = watterViewModel, navController = navController, navGraphBuilder = this)
    }
}

private fun addCompleteOrderScreen(
    watterViewModel: WatterViewModel,
    orderId: Int,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = PaymentNavRoute.CompleteOrderScreen.path)
    {
        CompleteOrderScreen(
            watterViewModel = watterViewModel,
            orderId = orderId,
            navController = navController,
            isPayment = true
        )
    }
}

private fun addCheckPayScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = PaymentNavRoute.CheckPaymentScreen.path) {
        LoadUrl(watterViewModel, navController)
    }
}