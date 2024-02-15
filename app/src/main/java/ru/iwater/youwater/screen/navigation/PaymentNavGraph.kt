package ru.iwater.youwater.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.iwater.youwater.screen.basket.CompleteOrderScreen
import ru.iwater.youwater.screen.basket.LoadUrl
import ru.iwater.youwater.vm.PaymentViewModel
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun PaymentNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    orderId: Int,
    viewModel: PaymentViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = PaymentNavRoute.CompleteOrderScreen.path
    ) {
        addCompleteOrderScreen(viewModel = viewModel, orderId = orderId, navController = navController, navGraphBuilder = this)
        addCheckPayScreen(viewModel = viewModel, navController = navController, navGraphBuilder = this)
    }
}

private fun addCompleteOrderScreen(
    orderId: Int,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: PaymentViewModel
) {
    navGraphBuilder.composable(route = PaymentNavRoute.CompleteOrderScreen.path)
    {
        CompleteOrderScreen(
            watterViewModel = viewModel,
            orderId = orderId,
            navController = navController,
            isPayment = true
        )
    }
}

private fun addCheckPayScreen(
    viewModel: PaymentViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = PaymentNavRoute.CheckPaymentScreen.path) {
        LoadUrl(watterViewModel = viewModel, navController = navController)
    }
}