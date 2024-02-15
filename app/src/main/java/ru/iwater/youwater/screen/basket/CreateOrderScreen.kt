package ru.iwater.youwater.screen.basket

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.iwater.youwater.R
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import timber.log.Timber
import java.util.Calendar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Price
import ru.iwater.youwater.screen.component.order.AddressAndTimeOrder
import ru.iwater.youwater.screen.component.order.ClientInfoCard
import ru.iwater.youwater.screen.component.order.DetailsOrder
import ru.iwater.youwater.screen.component.order.SetTimeOrderCard
import ru.iwater.youwater.screen.component.order.ShowDatePickerDialog
import ru.iwater.youwater.vm.OrderViewModel
import ru.iwater.youwater.vm.OrderViewModelFactory

@Composable
fun CreateOrderScreen(
    clientId: Int,
    clientName: String,
    clientPhone: String,
    addressList: List<NewAddress>,
    repeatOrder: Int,
    isShowMessage: Boolean,
    navController: NavHostController,
    watterViewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(clientId = clientId, repeatOrder = repeatOrder, addressList = addressList)),
) {
    val productsList = watterViewModel.productsInBasket
    val priceNoDiscount by watterViewModel.priceNoDiscount.observeAsState()
    val generalCost by watterViewModel.generalCost.observeAsState()
    var highSize by remember {
        mutableIntStateOf(0)
    }

    var checkAddressDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var dateOrder by rememberSaveable {
        mutableStateOf("")
    }
    var expandedTime by remember {
        mutableStateOf(false)
    }
    var expandedPay by remember {
        mutableStateOf(false)
    }
    val timesOrder = watterViewModel.timesListOrder
    val typesPayOrder = listOf(
        "Оплата по карте курьеру",
        "Оплата наличными",
        "Оплата онлайн",
    )
    var selectedTime by rememberSaveable {
        mutableStateOf("**:**-**:**")
    }
    var selectedPay by rememberSaveable {
        mutableStateOf("Выберите способ оплаты")
    }
    var commentOrder by rememberSaveable {
        mutableStateOf("")
    }
    var titleButtonCreate by rememberSaveable {
        mutableStateOf("Оформить заявку")
    }
    var isCreateOrder by rememberSaveable {
        mutableStateOf(false)
    }

    val order by watterViewModel.order.observeAsState()

    var selectedAddress by rememberSaveable {
        mutableIntStateOf(-1)
    }

    if (isShowMessage) {
        Toast.makeText(navController.context, "Извините оплатить заказ не удалось, попробуйте выбрать другой тип оплаты или отредактировать заказ", Toast.LENGTH_LONG).show()
    }

    if (repeatOrder != 0) {
        Timber.d("Repeat order $repeatOrder")

        val address = addressList.find { it.id == order?.addressId }
        if (address != null) {
            selectedAddress = addressList.indexOf(address)
        }
        when (order?.paymentType) {
            "4" -> selectedPay = "Оплата по карте курьеру"
            "0" -> selectedPay = "Оплата наличными"
            "2" -> selectedPay = "Оплата онлайн"
        }
        commentOrder = order?.notice ?: ""
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val scrollState = rememberScrollState()
        var showDatePicker by remember {
            mutableStateOf(false)
        }
        val calendar = Calendar.getInstance()
        Column(
            modifier = Modifier
                .padding(bottom = 120.dp)
                .verticalScroll(state = scrollState, enabled = true)
        ) {
//            if (client != null) {
                ClientInfoCard(name = clientName, telNumber = clientPhone)
                Timber.d("Order = $order")
//            }
            AddressAndTimeOrder(
                addressList = addressList,
                selectedAddress = selectedAddress,
                checkAddressDialog = checkAddressDialog,
                dateOrder = dateOrder,
                onShowDialog = {
                    if (addressList.isEmpty()) {
                        navController.navigate(
                            MainNavRoute.AddAddressScreen.withArgs(true.toString())
                        )
                    } else checkAddressDialog = !checkAddressDialog },
                setAddressOrder = {
                    selectedTime = "**:**-**:**"
                    dateOrder = ""
//                    getDeliveryOnAddress(it)
                    watterViewModel.getDeliveryOnAddress(it)
                    selectedAddress = addressList.indexOf(it) },
                showDatePickerDialog = {
                    showDatePicker = true
                }
            )
            ShowDatePickerDialog(showDatePicker = showDatePicker, watterViewModel = watterViewModel, setShowDatePicker = {showDatePicker = it}, calendar = calendar, setDateOrder = {dateOrder = it})
            if (dateOrder.isNotEmpty()) {
                Timber.d("Date Order = $order")
                SetTimeOrderCard(
                    timeListOrder = timesOrder,
                    selectedTime = selectedTime,
                    expandedTime = expandedTime,
                    setTimeOrder = {
                            timesOrder -> watterViewModel.setTimeOrder(timesOrder)
                            selectedTime = timesOrder
                            expandedTime = !expandedTime
                    }
                )
            }
            highSize = productsList.size*72
            DetailsOrder(highSize = highSize, products = productsList, minusCount = {watterViewModel.minusCountProduct(it.id)}, addCount = {watterViewModel.plusCountProduct(it.id)})
            TypePayCard(
                typesPayOrder = typesPayOrder,
                selectedPay = selectedPay,
                expandedPay = expandedPay,
                setPaymentType = {typePay ->
                    watterViewModel.setTypePeyOrder(typePay)
                    selectedPay = typePay
                    titleButtonCreate = if (typePay == "Оплата онлайн") "Перейти к оплате" else "Оформить заявку"
                    expandedPay = !expandedPay
            })
            GetCommentCard(commentOrder) {comment ->
                commentOrder = comment
                watterViewModel.setNoticeOrder(comment)
            }
        }
        GeneralInfo(
            modifier = Modifier.align(Alignment.BottomCenter),
            titleButton = titleButtonCreate,
            priceNoDiscount = priceNoDiscount ?: 0,
            generalCost = generalCost ?: 0,
            isEnable = { watterViewModel.isTrueOrder(order) && !isCreateOrder  }
        ) {
            isCreateOrder = !isCreateOrder
            watterViewModel.sendAndSaveOrder(order, generalCost ?: 0, clientPhone = clientPhone, navController)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypePayCard(typesPayOrder: List<String>, selectedPay: String, expandedPay: Boolean, setPaymentType: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expandedPay,
            onExpandedChange = {
                setPaymentType("Выберите способ оплаты")

            }
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selectedPay,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "вид оплаты")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPay)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expandedPay,
                onDismissRequest = { setPaymentType("Выберите способ оплаты") }
            ) {
                typesPayOrder.forEach {typePey ->
                    DropdownMenuItem(
                        onClick = {
                            setPaymentType(typePey)
                        },
                        text = {
                            Text(
                                text = typePey,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        })
                }


            }
        }
    }
}

@Composable
fun GetCommentCard(commentOrder: String, setComment: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = commentOrder,
            onValueChange = {setComment(it)},
            label = { Text(text = stringResource(id = R.string.add_notice_dialog_comment_for_driver))},
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview
@Composable
fun CreateOrderScreenPreview() {
    val productsList: List<NewProduct> = List(10) {
        NewProduct(
            id = it,
            name = "Plesca Натуральная 19л в оборотной таре $it",
            appName = "Plesca Натуральная в оборотной таре",
            price = listOf(
                Price(1, 355),
                Price(2, 325),
                Price(4, 400),
                Price(8, 280),
                Price(10, 250),
                Price(20, 240)
            ),
            category = 1,
            image = "cat-1.png",
            count = it + 1,
        )
    }.toMutableStateList()
    var generalPrice by remember {
        mutableIntStateOf(0)
    }

    val highSize by rememberSaveable {
        mutableIntStateOf(72*productsList.size)
    }
    productsList.forEach {product ->
        generalPrice += product.getPriceOnCount(product.count)
    }
    YourWaterTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier
                .padding(bottom = 120.dp)
                .verticalScroll(state = scrollState, enabled = true)) {
                ClientInfoCard(name = "Екатерина Иванова", telNumber = "+7 (999) 210-48-94")
                DetailsOrder(
                    highSize = highSize,
                    products = productsList,
                    minusCount = {
                        generalPrice = 0
                        productsList.find { product -> it.id == product.id }?.count = it.count--
                    },
                    addCount = {
                        generalPrice = 0
                        productsList.find { product -> it.id == product.id }?.count = it.count++
                    }
                )
            }
            GeneralInfo(
                modifier = Modifier.align(Alignment.BottomCenter),
                titleButton = "qweqwe",
                priceNoDiscount = generalPrice,
                generalCost = generalPrice,
                isEnable = { generalPrice > 0 }
            ) {

            }
        }


    }
}