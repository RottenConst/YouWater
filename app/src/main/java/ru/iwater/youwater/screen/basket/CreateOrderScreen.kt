package ru.iwater.youwater.screen.basket

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.RawAddress
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.ProductListViewModel
import java.util.Calendar

@Composable
fun CreateorderScreen(productListViewModel: ProductListViewModel = viewModel(), fragmentManager: FragmentManager) {

    productListViewModel.getClient()
    productListViewModel.getAddressList()
    val client by productListViewModel.client.observeAsState()
    val productsList = productListViewModel.productsList
    val priceNoDiscount by productListViewModel.priceNoDiscount.observeAsState()
    val generalCost by productListViewModel.generalCost.observeAsState()
//    Timber.d("Product = ${productsList.size}")
    var highSize by remember {
        mutableStateOf(0)
    }
    val addressList by productListViewModel.addressList.observeAsState()
    var selectedAddress by rememberSaveable {
        mutableStateOf(-1)
    }
    var checkAddressDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var dateOrder by rememberSaveable {
        mutableStateOf("")
    }
    var timesOrder = productListViewModel.timesListOrder


    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(bottom = 120.dp)
                .verticalScroll(state = scrollState, enabled = true)
        ) {
            if (client != null) {
                ClientInfoCard(name = client?.name ?: "", telNumber = client?.contact ?: "")
            }
            AddressAndTimeOrder(
                addressList = addressList ?: emptyList(),
                selectedAddress = selectedAddress,
                checkAddressDialog = checkAddressDialog,
                dateOrder = dateOrder,
                onShowDialog = {checkAddressDialog = !checkAddressDialog},
                setAddressOrder = {
                    productListViewModel.getDeliveryOnAddress(it)
                    selectedAddress = addressList?.indexOf(it) ?: -1 },
                showDatePickerDialog = { productListViewModel.getCalendar(
                    calendar = Calendar.getInstance(),
                    setDateOrder = {dateOrder = it}
                ).show(fragmentManager, "SetDateDialog") }
            )
            if (dateOrder.isNotEmpty()) {
                SetTimeOrderCard(timeListOrder = timesOrder)
            }
            highSize = productsList.size*72
            DetailsOrder(highSize = highSize, products = productsList, minusCount = {}, addCount = {})
            TypePayCard()
            GetComentCard()
        }
        GeneralInfo(
            modifier = Modifier.align(Alignment.BottomCenter),
            priceNoDiscount = priceNoDiscount ?: 0,
            generalCost = generalCost ?: 0,
            isEnable = { productsList.isNotEmpty() }
        ) {

        }
    }

}

@Composable
fun ClientInfoCard(name: String, telNumber: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = name,
                style = YouWaterTypography.h5,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = telNumber,
                style = YouWaterTypography.subtitle1
            )
        }
    }
}

@Composable
fun AddressAndTimeOrder(
    addressList: List<RawAddress>,
    checkAddressDialog: Boolean,
    selectedAddress: Int,
    dateOrder: String,
    onShowDialog: () -> Unit,
    setAddressOrder: (RawAddress) -> Unit,
    showDatePickerDialog: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onShowDialog()
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    imageVector = Icons.Outlined.LocationOn,
                    tint = Blue500,
                    contentDescription = stringResource(id = R.string.info_product)
                )
                Text(
                    text = if (addressList.isEmpty()) {
                        "Добавить адрес"
                    } else {
                        when (selectedAddress) {
                            -1 -> "Выбрать адрес"
                            else -> addressList[selectedAddress].factAddress
                        }
                    },
                    style = YouWaterTypography.body1,
                    textAlign = TextAlign.Center,
                    color = Blue500,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "",
                    tint = Color.LightGray,
                )
            }
            if (selectedAddress != -1) {
                if (!addressList[selectedAddress].notice.isNullOrEmpty()) {
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = 8.dp),
                            imageVector = Icons.Outlined.Edit,
                            tint = Blue500,
                            contentDescription = stringResource(id = R.string.info_product)
                        )
                        Text(
                            text = "Комментарий к адресу: ${addressList[selectedAddress].notice}",
                            style = YouWaterTypography.body1,
                            textAlign = TextAlign.Center,
                            color = Blue500,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Divider(color = Color.LightGray, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            showDatePickerDialog()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time_24),
                        contentDescription = "",
                        tint = Blue500
                    )
                    Text(
                        text = dateOrder.ifEmpty { "Укажите дату" },
                        style = YouWaterTypography.body1,
                        textAlign = TextAlign.Start,
                        color = Blue500
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowRight,
                        contentDescription = "",
                        tint = Color.LightGray
                    )
                }
            }
        }
    }

    if (checkAddressDialog) {
        SetAddressDialog(
            addressList = addressList,
            onShowDialog = { onShowDialog() },
            setAddressOrder = setAddressOrder)
    }

}

@Composable
private fun SetAddressDialog(
    addressList: List<RawAddress>,
    onShowDialog: () -> Unit,
    setAddressOrder: (RawAddress) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onShowDialog() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "",
                    tint = Blue500
                )
                Text(
                    text = "Bыберете адрес",
                    color = Blue500,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        shape = RoundedCornerShape(8.dp),
        buttons = {
            var addressSelected by remember {
                mutableStateOf(addressList[0])
            }
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .fillMaxWidth(),
            ) {
                addressList.forEach { address ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (address == addressSelected),
                                onClick = { addressSelected = address }
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = (address == addressSelected),
                            onClick = null
                        )
                        Text(text = address.factAddress)
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onShowDialog() }) {
                        Text(text = "Отмена")
                    }
                    TextButton(onClick = {
                        onShowDialog()
                        setAddressOrder(addressSelected)
                    }
                    ) {
                        Text(text = "Выбрать")
                    }
                }

            }

        })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetTimeOrderCard(timeListOrder: MutableList<String>) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedItem by remember {
        mutableStateOf(timeListOrder.last())
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
                timeListOrder.remove("**:**-**:**")
            }
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = selectedItem,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(id = R.string.fragment_create_order_time_order))},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
            )
            ExposedDropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                timeListOrder.forEach {typePey ->
                    DropdownMenuItem(
                        onClick = {
                            selectedItem = typePey
                            expanded = !expanded
                        }
                    ) {
                        Text(text = typePey)
                    }
                }


            }
        }
    }
}

@Composable
fun DetailsOrder(highSize: Int, products: List<Product>, minusCount: (Product) -> Unit, addCount: (Product) -> Unit) {
    Text(
        text = stringResource(id = R.string.fragment_create_order_info_order),
        style = YouWaterTypography.h6,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(highSize.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        LazyColumn {
            items(products.size) {index ->
                val product = products[index]
                var count by remember {
                    mutableStateOf(product.count)
                }
                ItemProductInOrder(
                    productName = product.name,
                    productGallery = product.gallery,
                    productCount = count,
                    productsPrise = {product.getPriceOnCount(count)},
                    minusCount = {
                        if (count > 1) count--
                        minusCount(product)
                                 },
                    addCount = {
                        count++
                        addCount(product)
                    }
                )
            }
        }
    }
}

@Composable
fun ItemProductInOrder(
    productName: String,
    productGallery: String,
    productCount: Int,
    productsPrise: (Int) -> Int,
    minusCount: () -> Unit,
    addCount: () -> Unit
) {
    Column {
            Row(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ImageProduct(image = productGallery)
                Text(
                    text = productName,
                    style = YouWaterTypography.caption,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.width(156.dp)
                )
                IconButton(onClick = { minusCount() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_btn_minus),
                        contentDescription = "",
                        tint = Color.LightGray
                    )
                }
                PriceAndCount(productCount = productCount, productsPrise = productsPrise)
                IconButton(onClick = { addCount() }) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "",
                        tint = Blue500
                    )
                }
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Composable
fun PriceAndCount(productCount: Int, productsPrise: (Int) -> Int) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${productsPrise(productCount)}P",
            style = YouWaterTypography.caption,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Blue500
        )
        Text(
            text = "$productCount шт.",
            style = YouWaterTypography.caption,
            textAlign = TextAlign.Center
        )

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TypePayCard() {
    var expanded by remember {
        mutableStateOf(false)
    }
    val listItems = mutableListOf(
        "Оплата по карте курьеру",
        "Оплата наличными",
        "Оплата онлайн",
        "Выберите способ оплаты"
    )
    var selectedItem by remember {
        mutableStateOf(listItems.last())
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
                listItems.remove("Выберите способ оплаты")
            }
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = selectedItem,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "вид оплаты")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
            )
            ExposedDropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listItems.forEach {typePey ->
                    DropdownMenuItem(
                        onClick = {
                            selectedItem = typePey
                            expanded = !expanded
                        }
                    ) {
                        Text(text = typePey)
                    }
                }

                
            }
        }
    }
}

@Composable
fun GetComentCard() {
    var coment by remember {
        mutableStateOf("")
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = coment,
            onValueChange = {coment = it},
            label = { Text(text = "Коментарий для курьера")},

        )
    }
}

@Preview
@Composable
fun CreateOrderScreenPreview() {
    val productsList: List<Product> = List(10) {
        Product(
            id = it,
            name = "Plesca Натуральная 19л в оборотной таре $it",
            shname = "PLN",
            app_name = "Plesca Натуральная в оборотной таре",
            price = "1:355;2:325;4:300;8:280;10:250;20:240;",
            discount = 0,
            category = 1,
            about = "",
            gallery = "cat-1.png",
            date_created = 1676121935,
            date = "11/02/2023",
            site = 1,
            app = 1,
            count = it + 1,
            company_id = "0007"
        )
    }.toMutableStateList()
    var generalPrice by remember {
        mutableStateOf(0)
    }

    val highSize by rememberSaveable {
        mutableStateOf(72*productsList.size)
    }
    var onShowDialog by rememberSaveable {
        mutableStateOf(false)
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
//                AddressAndTimeOrder(context = LocalContext.current)
                DetailsOrder(
                    highSize = highSize,
                    products = productsList,
                    minusCount = {
//                        it.count--
                        generalPrice = 0
                        productsList.find { product -> it.id == product.id }?.count = it.count--
                    },
                    addCount = {
                        generalPrice = 0
                        productsList.find { product -> it.id == product.id }?.count = it.count++
                    }
                )
                TypePayCard()
                GetComentCard()
            }
            GeneralInfo(
                modifier = Modifier.align(Alignment.BottomCenter),
                priceNoDiscount = generalPrice,
                generalCost = generalPrice,
                isEnable = { generalPrice > 0 }
            ) {

            }
        }


    }
}