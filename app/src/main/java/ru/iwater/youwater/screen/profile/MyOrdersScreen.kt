package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.iwater.youwater.R
import ru.iwater.youwater.data.MyOrder
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Price
import ru.iwater.youwater.screen.basket.InfoAddressOrder
import ru.iwater.youwater.screen.basket.InfoTimeOrder
import ru.iwater.youwater.screen.basket.NumberOrderText
import ru.iwater.youwater.screen.basket.PriceOrder
import ru.iwater.youwater.screen.basket.ProductsOrderList
import ru.iwater.youwater.screen.basket.TypePayOrder
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.utils.StatusData
import ru.iwater.youwater.vm.OrderViewModel
import ru.iwater.youwater.vm.OrderViewModelFactory

@Composable
fun MyOrdersScreen(
    watterViewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory()),
    navController: NavController
) {
    val modifier = Modifier
    val statusData by watterViewModel.statusData.observeAsState()
    val myOrdersList = watterViewModel.ordersList
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
                        CardOrderInfoWithButton(modifier = modifier, order = myOrdersList[it]) {navController.navigate(
                            MainNavRoute.CreateOrderScreen.withArgs(false.toString(), myOrdersList[it].id.toString())
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


@Composable
private fun CardOrderInfoWithButton(modifier: Modifier, order: MyOrder, repeatOrder: () -> Unit) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NumberOrderText(order.id)
                Text(
                    text = when(order.status) {
                        0 -> {
                            "Заказ принят"
                        }
                        1 -> {
                            "Передан в доставку"
                        }
                        2 -> {
                            "Заказ отменён"
                        }
                        3 -> {
                            "Заказ доставлен"
                        }
                        4 -> {
                            "Заказ перенесён"
                        }
                        else -> {""}
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            InfoAddressOrder(modifier = modifier, address = order.address)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            InfoTimeOrder(modifier = modifier, time = order.date)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            ProductsOrderList(modifier = modifier, products = order.products)
            PriceOrder(modifier = modifier, priceOder = order.cash)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            TypePayOrder(modifier = modifier, typePay = order.typeCash ?: "")
            Button(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = { repeatOrder() }) {
                Text(text = stringResource(id = R.string.item_order_replay_order))
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
                products = List(5) { orderId ->
                    NewProduct(
                        appName = "Стартовый набор Plesca Классическая",
                        category = 0,
                        image = "",
                        id = orderId,
                        name = "Стартовый набор Plesca Классическая",
                        price = listOf(Price(1, 100), Price(2, 200)),
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
                CardOrderInfoWithButton(modifier = modifier, order = myOrders[it]) {}
            }
        }
    }
}