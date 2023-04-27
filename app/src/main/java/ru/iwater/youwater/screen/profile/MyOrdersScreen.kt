package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.iwater.youwater.R
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.data.MyOrder
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.StatusData
import ru.iwater.youwater.screen.basket.CardOrderInfo
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun MyOrdersScreen(clientProfileViewModel: ClientProfileViewModel = viewModel(), navController: NavController) {
    val modifier = Modifier
    val statusData by clientProfileViewModel.statusData.observeAsState()
    val myOrdersList = clientProfileViewModel.ordersList
    when (statusData) {
        StatusData.LOAD -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        StatusData.DONE -> {
            if (myOrdersList.isNotEmpty()) {
                LazyColumn(modifier = modifier.fillMaxSize()) {

                    items(count = myOrdersList.size) {
                        CardOrderInfo(modifier = modifier, order = myOrdersList[it]) {navController.navigate(
                            MyOrdersFragmentDirections.actionMyOrdersFragmentToCreateOrderFragment(false, myOrdersList[it].id)
                        )}
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.fragment_my_order_nothing_order),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.fragment_my_order_nothing_order),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}


@Preview
@Composable
fun MyOrdersPreview() {
    YourWaterTheme {
        val myOrders = List(10) {
            MyOrder(
                address = "Санкт-Петербург, ул. Галерная, д. 75, кв. 16",
                cash = "3020 ₽",
                date = "23 октября, вторник, в течение дня",
                products = List(5) {
                    Product(
                        about = "123",
                        app = 1,
                        app_name = "Стартовый набор Plesca Классическая",
                        category = 0,
                        company_id = "",
                        date = "",
                        discount = 0,
                        date_created = 0,
                        gallery = "",
                        id = it,
                        name = "Стартовый набор Plesca Классическая",
                        price = "1:100;2:200;",
                        shname = "",
                        site = 1,
                        count = 2,
                        onFavoriteClick = false
                    )
                },
                typeCash = "0",
                status = 1,
                id = it
            )
        }
        val modifier = Modifier
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(myOrders.size) {
                CardOrderInfo(modifier = modifier, order = myOrders[it]) {}
            }
        }
    }
}