package ru.iwater.youwater.screen.basket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import okhttp3.internal.notifyAll
import ru.iwater.youwater.R
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Price
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.screen.component.product.CardProductInBasket
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.BasketViewModel
import ru.iwater.youwater.vm.BasketViewModelFactory

@Composable
fun BasketScreen(
    productsList: List<NewProduct> = emptyList(),
    deleteProduct: (Int) -> Unit,
    updateProduct: (NewProduct, Int) -> Unit,
    watterViewModel: BasketViewModel = viewModel(factory = BasketViewModelFactory(productList = productsList)),
    navController: NavHostController
) {

    val products = watterViewModel.products
    val priceProduct by watterViewModel.priceProduct.observeAsState()
    val generalPrice by watterViewModel.generalPrice.observeAsState()
//    var generalPrice by rememberSaveable {
//        mutableIntStateOf(watterViewModel.getCostProduct())
//    }

    Column {
        if (products.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products.size) { productIndex ->
                    CardProductInBasket(
                        product = products[productIndex],
                        getAboutProduct = {
                           navController.navigate(MainNavRoute.AboutProductScreen.withArgs(products[productIndex].id.toString()))
                        },
                        deleteProduct = {
                            watterViewModel.deleteProduct(productIndex = productIndex, delete = deleteProduct)
                        },
                        plusCount = {
                            if (products[productIndex].category != 20) {
                                watterViewModel.plusCountProduct(productIndex, updateProduct = updateProduct)
                            }
                        },
                        minusCount = {
                            if (products[productIndex].count > 1) {
                                watterViewModel.minusCountProduct(productIndex, updateProduct = updateProduct)
                            } else {
                                watterViewModel.deleteProduct(productIndex = productIndex, delete = deleteProduct)
                            }
                        })
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Ни одного товара не добавлено в корзину",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        GeneralInfo (
            modifier = Modifier,
            titleButton = stringResource(id = R.string.fragment_basket_checkout_order),
            priceNoDiscount = priceProduct ?: 0,
            generalCost = generalPrice ?: 0,
            isEnable = { productsList.isNotEmpty() }
        ){
            navController.navigate(
//                BasketFragmentDirections.actionBasketFragmentToCreateOrderFragment(false, 0)
                MainNavRoute.CreateOrderScreen.withArgs(false.toString(), "0")
            )
        }
    }

}

@Composable
fun PriceOrderNoDiscount(priceOrderNoDiscount: Int){
    Box(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.fragment_basket_total_sum_order))
            Text(text = "$priceOrderNoDiscount₽", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GeneralInfoOrder(generalPrice: Int, titleButton: String, isEnable: () -> Boolean, toCreateOrder: () -> Unit) {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box {
                Row(modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxHeight(),
                    verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = stringResource(id = R.string.fragment_basket_total_sum),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$generalPrice₽",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            Button(
                onClick = { toCreateOrder() },
                enabled = isEnable(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = titleButton)
            }
        }
    }
}

@Composable
fun GeneralInfo(modifier: Modifier = Modifier, priceNoDiscount: Int, generalCost: Int, isEnable: () -> Boolean, titleButton: String, toCreateOrder: () -> Unit) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp),
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(width = 1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                PriceOrderNoDiscount(priceNoDiscount)
                GeneralInfoOrder(generalCost, titleButton, isEnable = { isEnable() } ) {toCreateOrder()}
            }
        }

}

@Preview
@Composable
fun GeneralInfoPreview() {
    val productsList1: List<Product> = List(100) {
        Product(
            id = it,
            name = "Plesca Натуральная 19л в оборотной таре",
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
            company_id = "0007"
        )
    }
    YourWaterTheme {

        Column(modifier = Modifier.wrapContentHeight()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productsList1.size) { productIndex ->
                    CardProductInBasket(
                        product = NewProduct(appName = productsList1[productIndex].app_name ?: "", productsList1[productIndex].category, productsList1[productIndex].id, productsList1[productIndex].gallery, productsList1[productIndex].name, listOf(
                            Price(1, 355)
                        ), false, 5),
                        {productsList1[productIndex].getPriceNoDiscount(1)},
                        {productsList1[productIndex].getPriceOnCount(1)},
                        {}, {})
                }
            }
            GeneralInfo(
                priceNoDiscount = 1,
                titleButton = stringResource(id = R.string.fragment_basket_checkout_order),
                generalCost = 1,
                isEnable = {productsList1.size> 1},
                toCreateOrder = {}
            )
        }
    }
}