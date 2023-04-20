package ru.iwater.youwater.screen.basket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.sharp.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.iwater.youwater.R
import ru.iwater.youwater.data.MyOrder
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.ProductListViewModel

@Composable
fun CompleteOrderScreen(productListViewModel: ProductListViewModel = viewModel(), orderId: Int, isPaid: Boolean, navController: NavController) {
    val modifier = Modifier
    productListViewModel.getOrderCrm(orderId)
    val completeOrder by productListViewModel.completedOrder.observeAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = modifier.padding(bottom = 80.dp)) {
            item {
                InfoStatusCreate(modifier = modifier, isPaid = isPaid)
            }
                items(count = 1) {
                    completeOrder?.let { it1 -> CardOrderInfo(modifier = modifier, it1) }
            }
        }
        HomeButton(modifier = modifier.align(Alignment.BottomCenter)) {
            navController.navigate(
                CompleteOrderFragmentDirections.actionCompleteOrderFragmentToHomeFragment()
            )
        }
    }
}

@Composable
fun InfoStatusCreate(modifier: Modifier, isPaid: Boolean) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPaid) {
            Icon(
                modifier = modifier.size(42.dp, 42.dp),
                imageVector = Icons.Sharp.CheckCircle,
                contentDescription = "",
                tint = Blue500
            )
            Text(
                text = stringResource(id = R.string.fragment_complete_order_complete_text),
                style = YouWaterTypography.h6,
                fontWeight = FontWeight.Bold,
                color = Blue500
            )
            Text(
                text = stringResource(id = R.string.fragment_complete_order_connect_order_delivery),
                textAlign = TextAlign.Center
            )
        } else {
            Icon(
                modifier = modifier.size(42.dp, 42.dp),
                imageVector = Icons.Rounded.Close,
                contentDescription = "",
                tint = Color.Red
            )
            Text(
                text = "Ошибка, не удалось оплатить заказ",
                style = YouWaterTypography.h6,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Blue500
            )
        }
    }
}

@Composable
fun NumberOrderText(numberOrder: Int) {
    Row {
        Text(
            text = "Заказ №",
            style = YouWaterTypography.body1,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "$numberOrder",
            style = YouWaterTypography.body1,
            color = Blue500,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InfoAddressOrder(modifier: Modifier, address: String) {
    Row(modifier = modifier
        .padding(8.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = "",
            tint = Blue500
        )
        Text(
            modifier = modifier.padding(8.dp),
            text = address,
            style = YouWaterTypography.body2,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun InfoTimeOrder(modifier: Modifier, time: String) {
    Row(modifier = modifier
        .padding(8.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_time_24),
            contentDescription = "",
            tint = Blue500
        )
        Text(
            modifier = modifier.padding(8.dp),
            text = time,
            style = YouWaterTypography.body2,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun PriceOrder(modifier: Modifier, priceOder: String) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier.padding(8.dp),
            text = "Итого:",
            style = YouWaterTypography.subtitle2,
            fontWeight = FontWeight.Black
        )
        Text(
            modifier = modifier.padding(8.dp),
            text = "${priceOder}P",
            style = YouWaterTypography.subtitle2,
            color = Blue500,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TypePayOrder(modifier: Modifier, typePay: String) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier.padding(8.dp),
            text = "Оформление заявки:",
            style = YouWaterTypography.subtitle2,
            fontWeight = FontWeight.Black
        )
        Text(
            modifier = modifier.padding(8.dp),
            text = when (typePay) {
                "0" -> {
                    "Оплата наличными"
                }

                "2" -> {
                    "Оплата онлайн"
                }

                "4" -> {
                    "Оплата по карте курьеру"
                }

                else -> {typePay}
            },
            style = YouWaterTypography.subtitle2,
        )
    }
}

@Composable
fun CardOrderInfo(modifier: Modifier, order: MyOrder) {
    Surface(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
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
                    style = YouWaterTypography.body1,
                    color = Blue500
                )
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
            InfoAddressOrder(modifier = modifier, address = order.address)
            Divider(color = Color.LightGray, thickness = 1.dp)
            InfoTimeOrder(modifier = modifier, time = order.date)
            Divider(color = Color.LightGray, thickness = 1.dp)
            ProductsOrderList(modifier = modifier, products = order.products)
            PriceOrder(modifier = modifier, priceOder = order.cash)
            Divider(color = Color.LightGray, thickness = 1.dp)
            TypePayOrder(modifier = modifier, typePay = order.typeCash ?: "")
            Button(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = { /*TODO*/ }) {
                Text(text = "Повторить заказ")
            }
        }
    }
}

@Composable
fun ProductsOrderList(modifier: Modifier, products: List<Product>) {
    Box(modifier = modifier.height(((products.size + 1)*66).dp).fillMaxWidth()) {
        LazyColumn {
            item {
                Text(
                    modifier = modifier.padding(8.dp),
                    text = "Детали заказа:",
                    style = YouWaterTypography.body2,
                    fontWeight = FontWeight.Bold
                )
            }
            items(products.size) { productIndex ->
                ItemProductOrder(modifier = modifier, products[productIndex])
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun ItemProductOrder(modifier: Modifier, product: Product) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .height(64.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ImageProduct(image = product.gallery)
        Text(
            text = product.name,
            style = YouWaterTypography.caption,
            textAlign = TextAlign.Start,
            modifier = Modifier.width(156.dp)
        )
        Column(
            modifier = modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (product.getPriceNoDiscount(product.count) != product.getPriceOnCount(product.count)) {
                Text(
                    text = "${product.getPriceNoDiscount(product.count)}P",
                    style = YouWaterTypography.caption,
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
            }
            Text(
                text = "${product.getPriceOnCount(product.count)}",
                style = YouWaterTypography.caption,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Blue500
            )
        }
        Text(
            text = "${product.count} шт.",
            style = YouWaterTypography.caption,
        )
    }
}

@Composable
fun HomeButton(modifier: Modifier, toHomeScreen: () -> Unit){
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = { toHomeScreen() }) {
        Text(text = "Перейти на главную")
    }
}

@Preview
@Composable
fun CompleteOrderPreview() {
    YourWaterTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(bottom = 80.dp)) {
                item { InfoStatusCreate(modifier = Modifier.fillMaxWidth(), false) }
                items(1) {
                }
        }
            HomeButton(modifier = Modifier.align(Alignment.BottomCenter)){}

        }

    }
}