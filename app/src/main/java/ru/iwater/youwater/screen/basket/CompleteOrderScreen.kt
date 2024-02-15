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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.sharp.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.iwater.youwater.R
import ru.iwater.youwater.data.MyOrder
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.component.product.ImageProduct
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.screen.navigation.PaymentNavRoute
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.utils.StatusPayment
import ru.iwater.youwater.vm.PaymentViewModel
import ru.iwater.youwater.vm.PaymentViewModelFactory
import timber.log.Timber

@Composable
fun CompleteOrderScreen(
    modifier: Modifier = Modifier,
    orderId: Int,
    isPayment: Boolean,
    navController: NavHostController,
    watterViewModel: PaymentViewModel = viewModel(factory = PaymentViewModelFactory(orderId))
) {
    LaunchedEffect(Unit) {
        watterViewModel.setCompleteOrderId(orderId)
    }

    val completeOrder by watterViewModel.completedOrder.observeAsState()
    val paymentStatus by watterViewModel.paymentStatus.observeAsState(initial = StatusPayment.LOAD)


    var pay by remember {
        mutableIntStateOf(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = modifier.padding(bottom = 80.dp)) {
            item {
                InfoStatusCreate(modifier = modifier, isPayment = isPayment, isPay = paymentStatus, navController = navController, pay) { pay++ }
            }
                items(count = 1) {
                    if (completeOrder != null) {
                    CardOrderInfo(modifier = modifier, completeOrder!!)
                    }
            }
        }
        HomeButton(modifier = modifier.align(Alignment.BottomCenter)) {
            if (isPayment) {
                MainActivity.start(navController.context)
            } else {
                navController.navigate(
                    MainNavRoute.HomeScreen.path
                )
            }
        }
    }
}

@Composable
fun InfoStatusCreate(modifier: Modifier, isPayment: Boolean, isPay: StatusPayment?, navController: NavHostController, pay: Int, payPlus: () -> Unit) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPayment) {
            Timber.d("is payment ${isPay?.name}")
            when (isPay) {
                StatusPayment.DONE -> {
                    Timber.d("is Done")
                    Icon(
                        modifier = modifier.size(42.dp, 42.dp),
                        imageVector = Icons.Sharp.CheckCircle,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(id = R.string.fragment_complete_order_complete_text),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(id = R.string.fragment_complete_order_connect_order_delivery),
                        textAlign = TextAlign.Center
                    )
                }

                StatusPayment.ERROR -> {
                    Icon(
                        modifier = modifier.size(42.dp, 42.dp),
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = stringResource(id = R.string.error_pay_order_text),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                StatusPayment.LOAD -> {
                    Timber.d("Status LOAD")
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                StatusPayment.PANDING -> {
                    if (pay == 0) {
                        payPlus()
                        navController.navigate(
                            PaymentNavRoute.CheckPaymentScreen.path
                        ){
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                }

                else -> {
                }
            }
        } else {
            Timber.d("is payment $isPayment")
            Icon(
                modifier = modifier.size(42.dp, 42.dp),
                imageVector = Icons.Sharp.CheckCircle,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = R.string.fragment_complete_order_complete_text),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = R.string.fragment_complete_order_connect_order_delivery),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NumberOrderText(numberOrder: Int) {
    Row {
        Text(
            text = stringResource(id = R.string.order_number_text),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "$numberOrder",
            style = YouWaterTypography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
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
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = modifier.padding(8.dp),
            text = address,
            style = MaterialTheme.typography.bodyMedium,
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
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = modifier.padding(8.dp),
            text = time,
            style = MaterialTheme.typography.bodyMedium,
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
            text = stringResource(id = R.string.item_order_total_sum),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Black
        )
        Text(
            modifier = modifier.padding(8.dp),
            text = "${priceOder}₽",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
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
            text = stringResource(id = R.string.item_order_description_complete_order),
            style = MaterialTheme.typography.titleSmall,
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
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
fun CardOrderInfo(modifier: Modifier, order: MyOrder) {
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
        }
    }
}

@Composable
fun ProductsOrderList(modifier: Modifier, products: List<NewProduct>) {
    Box(modifier = modifier
        .height(((products.size + 1) * 66).dp)
        .fillMaxWidth()) {
        LazyColumn {
            item {
                Text(
                    modifier = modifier.padding(8.dp),
                    text = stringResource(id = R.string.item_order_info_order),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(products.size) { productIndex ->
                ItemProductOrder(modifier = modifier, products[productIndex])
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun ItemProductOrder(modifier: Modifier, product: NewProduct) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .height(64.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ImageProduct(image = product.image)
        Text(
            text = product.name,
            style = MaterialTheme.typography.labelMedium,
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
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                text = "${product.getPriceOnCount(product.count)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "${product.count} шт.",
            style = MaterialTheme.typography.labelMedium,
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
        Text(text = stringResource(id = R.string.to_home_text))
    }
}

@Preview
@Composable
fun CompleteOrderPreview() {
    YourWaterTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(bottom = 80.dp)) {
                items(1) {
                }
        }
            HomeButton(modifier = Modifier.align(Alignment.BottomCenter)){}

        }

    }
}