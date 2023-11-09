package ru.iwater.youwater.screen.basket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.network.ImageUrl
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun BasketScreen(
    watterViewModel: WatterViewModel = viewModel(),
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        watterViewModel.getBasket()
    }
    val productsListInOrder = watterViewModel.productsInBasket
    val priceNoDiscount by watterViewModel.priceNoDiscount.observeAsState()
    val generalCost by watterViewModel.generalCost.observeAsState()

    Column {
        if (productsListInOrder.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productsListInOrder.size) { productIndex ->
                    val product = productsListInOrder[productIndex]
                    var count by remember {
                        mutableIntStateOf(product.count)
                    }
                    CardProductInBasket(
                        id = product.id,
                        name = product.name,
                        urlImage = product.gallery,
                        priceNoDiscount = {product.getPriceNoDiscount(count)},
                        costProducts = { product.getPriceOnCount(count) },
                        count = count,
                        getAboutProduct = {
                           navController.navigate(MainNavRoute.AboutProductScreen.withArgs(product.id.toString()))
                        },
                        deleteProduct = {
                            watterViewModel.deleteProductFromBasket(product.id)
                        },
                        plusCount = {
                            if (product.category != 20) {
                                count = it + 1
                                watterViewModel.plusCountProduct(product.id)
                            }
                        },
                        minusCount = {
                            if (it > 1) {
                                count = it - 1
                                watterViewModel.minusCountProduct(product.id)
                            } else
                                watterViewModel.deleteProductFromBasket(product.id)
                        }
                    )

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
            priceNoDiscount = priceNoDiscount ?: 0,
            generalCost = generalCost ?: 0,
            isEnable = { productsListInOrder.isNotEmpty() }
        ){
            navController.navigate(
//                BasketFragmentDirections.actionBasketFragmentToCreateOrderFragment(false, 0)
                MainNavRoute.CreateOrderScreen.withArgs(false.toString(), "0")
            )
        }
    }

}

@Composable
fun CardProductInBasket(
    id: Int,
    name: String,
    urlImage: String,
    priceNoDiscount: (Int) -> Int,
    costProducts: (Int) -> Int,
    count: Int,
    getAboutProduct: (Int) -> Unit,
    deleteProduct: () -> Unit,
    plusCount: (Int) -> Unit,
    minusCount: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(102.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier.padding(4.dp).clickable { getAboutProduct(id) }
        ) {
            Row {
                ImageProduct(image = urlImage)
                Column(modifier = Modifier.wrapContentWidth()) {
                    TitleProduct(name) { deleteProduct() }
                    CostProducts(
                        id = id,
                        priceNoDiscount = {priceNoDiscount(count)},
                        costProducts = { costProducts(count) },
                        count = count,
                        plusCount = {plusCount(count)},
                        minusCount = {minusCount(count)}
                    )
                    }
                }
            }
        }
}

@Composable
fun TitleProduct(productName: String, deleteProduct: () -> Unit) {
    var isVisible by remember {
        mutableStateOf(false)
    }

    DeleteProductDialog(
        productName = productName,
        isVisible = isVisible,
        setVisible = {isVisible = !isVisible},
        deleteProduct = {
            deleteProduct()
        }
    )
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = productName,
            style = YouWaterTypography.subtitle2,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            maxLines = 2,
            modifier = Modifier.width(200.dp)
        )
        Icon(
            modifier = Modifier.clickable { isVisible = true },
            painter = painterResource(id = R.drawable.ic_cancel),
            contentDescription = stringResource(id = R.string.description_ic_delete),
            tint = Color.LightGray
        )
    }
}

@Composable
fun CostProducts(id: Int, priceNoDiscount: (Int) -> Int, costProducts: (Int) -> Int, count: Int, plusCount: (Int) -> Unit, minusCount: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        if (id == 81 || id == 84) {
            Column {
                Text(
                    text = "${priceNoDiscount(count)}₽",
                    style = YouWaterTypography.subtitle2,
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
                Text(
                    text = "${costProducts(count)}₽",
                    style = YouWaterTypography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = Blue500
                )
            }
        } else {
            Text(
                text = "${costProducts(count)}₽",
                style = YouWaterTypography.subtitle1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                color = Blue500
            )
        }
        Box(
            contentAlignment = Alignment.BottomEnd) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween) {
                MinusProductButton {
                    minusCount(count)
                }
                Text(
                    text = "$count",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    style = YouWaterTypography.body1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                PlusProductButton {
                    plusCount(count)
                }
            }
        }

    }
}

@Composable
fun DeleteProductDialog(productName: String, isVisible: Boolean, setVisible: (Boolean) -> Unit, deleteProduct: () -> Unit) {
    if (isVisible) {
        AlertDialog(
            icon = {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(id = R.string.delete_product_text), tint = Blue500)
            },
            title = {
                Text(text = "Удалить $productName из корзины?", textAlign = TextAlign.Center)
            },
            onDismissRequest = { setVisible(false) },
            dismissButton = {
                TextButton(onClick = { setVisible(false) }) {
                    Text(text = stringResource(id = R.string.general_no), color = Blue500)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    setVisible(false)
                    deleteProduct()
                }) {
                    Text(text = stringResource(id = R.string.general_yes), color = Blue500)
                }
            }
        )
    }
}

@Composable
fun PlusProductButton(plusClick: () -> Unit) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Icon(
            modifier = Modifier.clickable { plusClick() },
            painter = painterResource(id = R.drawable.ic_btn_plus),
            contentDescription = stringResource(id = R.string.description_ic_delete),
            tint = Blue500
        )
    }
}

@Composable
fun MinusProductButton(minusClick: () -> Unit) {
    Box(contentAlignment = Alignment.BottomStart) {
        Icon(
            modifier = Modifier.clickable { minusClick() },
            painter = painterResource(id = R.drawable.ic_btn_minus),
            contentDescription = stringResource(id = R.string.description_ic_delete),
            tint = Color.LightGray
        )
    }
}

@Composable
fun ImageProduct(image: String) {
    GlideImage(
        imageModel = { "$ImageUrl/$image" },
        loading = {
            Box(
                modifier = Modifier.wrapContentWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        failure = {
            Image(
                painter = painterResource(id = R.drawable.ic_your_water_logo),
                contentDescription = stringResource(id = R.string.description_image_product),
                alignment = Alignment.Center
            )
        },
        previewPlaceholder = R.drawable.ic_your_water_logo,
        imageOptions = ImageOptions(
            alignment = Alignment.Center,
            contentDescription = stringResource(id = R.string.description_image_product),
            contentScale = ContentScale.Inside
        ),
        modifier = Modifier
            .padding(8.dp)
    )
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
                        style = YouWaterTypography.h6,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$generalPrice₽",
                        style = YouWaterTypography.h6,
                        fontWeight = FontWeight.Bold,
                        color = Blue500,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            Button(
                onClick = { toCreateOrder() },
                enabled = isEnable(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500)
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
            border = BorderStroke(width = 1.dp, Color.LightGray)
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
                        productsList1[productIndex].id,
                        productsList1[productIndex].name,
                        productsList1[productIndex].gallery,
                        {productsList1[productIndex].getPriceNoDiscount(1)},
                        {productsList1[productIndex].getPriceOnCount(1)},
                        productsList1[productIndex].count,
                        {}, {}, {}, {})
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