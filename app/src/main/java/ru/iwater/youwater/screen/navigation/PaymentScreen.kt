package ru.iwater.youwater.screen.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ru.iwater.youwater.vm.PaymentViewModel
import ru.iwater.youwater.vm.PaymentViewModelFactory
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    orderId: Int,
    watterViewModel: PaymentViewModel,
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        PaymentNavGraph(navController = navController, modifier = modifier.padding(paddingValues = paddingValues), orderId = orderId, viewModel = watterViewModel)
    }
}