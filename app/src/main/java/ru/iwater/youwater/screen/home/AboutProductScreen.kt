package ru.iwater.youwater.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import ru.iwater.youwater.R
import ru.iwater.youwater.data.InfoProduct
import ru.iwater.youwater.data.Measure
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Price
import ru.iwater.youwater.network.ImageUrl
import ru.iwater.youwater.screen.component.product.PriceListScreen
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.AboutProductViewModel
import ru.iwater.youwater.vm.AboutProductViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutProductScreen(
    productId: Int,
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
    aboutProductViewModel: AboutProductViewModel = viewModel(factory = AboutProductViewModelFactory(productId)),
    navController: NavHostController
) {
    val product by aboutProductViewModel.product.observeAsState()
    val measures by aboutProductViewModel.measureList.observeAsState()

    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheet = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val showModalSheet = rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val measure = if (measures?.isNotEmpty() == true) measures?.find { measure -> product?.measure == measure.id } else Measure(product?.measure ?: 0, "штука", "шт.")
    if (skipPartiallyExpanded) {
        ModalBottomSheet(
            sheetState = bottomSheet,
            onDismissRequest = {
                skipPartiallyExpanded = false
            },
            windowInsets = BottomSheetDefaults.windowInsets,
            content = {
                PriceListScreen(prices = product?.price ?: emptyList(), measure = measure ?: Measure(product?.measure ?: 0, "штука", "шт.") )
            }
        )
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(snackbarData = snackbarData)
            }
        }
    ) { paddingValues ->
        if (product != null) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)) {
                InfoProduct(product = product!!)
                val price = if (product!!.price.isEmpty()) {
                    -1
                } else {
                    product!!.price.first().price
                }
                ProductPriceInfo(price = price) {
                    showModalSheet.value = !showModalSheet.value
                    scope.launch {
                        skipPartiallyExpanded = !skipPartiallyExpanded
                    }
                }
                AboutProduct(product!!) {
                    addProductInBasket(NewProduct(
                        appName = it.appName ?: it.name,
                        category = it.category,
                        id = it.id,
                        image = it.image ?: "",
                        name = it.name,
                        price = it.price,
                        onFavoriteClick = it.isFavorite,
                        count = it.count
                    ), it.count, false)
//                    aboutProductViewModel.addProductCountToBasket(
//                        NewProduct(
//                            appName = it.appName ?: it.name,
//                            category = it.category,
//                            id = it.id,
//                            image = it.image ?: "",
//                            name = it.name,
//                            price = it.price,
//                            onFavoriteClick = it.isFavorite,
//                            count = it.count
//                        )
//                    )
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "${it.appName} добавлен в корзину",
                            actionLabel = "Корзина"
                        )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                navController.navigate(MainNavRoute.BasketScreen.path)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoProduct(product: InfoProduct) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        GlideImage(
//            imageModel = { "$ImageUrl/${product.image}" },
//            modifier = Modifier
//                .padding(16.dp)
//                .height(248.dp)
//                .fillMaxWidth(),
//            previewPlaceholder = R.drawable.ic_your_water_logo,
//            loading = {
//                Box(modifier = Modifier.matchParentSize()) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//            },
//            failure = {
//                Image(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .height(248.dp)
//                        .fillMaxWidth(),
//                    painter = painterResource(id = R.drawable.ic_your_water_logo),
//                    contentDescription = stringResource(id = R.string.description_image_product))
//            },
//            imageOptions = ImageOptions(
//                alignment = Alignment.Center,
//                contentScale = ContentScale.Fit,
//                contentDescription = stringResource(id = R.string.description_image_product),
//            )
//
//        )
        val painter = rememberAsyncImagePainter(
            model = "$ImageUrl/${product.image}/",
            imageLoader = ImageLoader
                .Builder(LocalContext.current)
                .okHttpClient {
                    OkHttpClient.Builder()
                        .addInterceptor { chain -> val request = chain.request().newBuilder()
                            .addHeader("Content-Type", "image/png")
                            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiY29tcGFueV9pZCI6NywiZXhwIjoxNzAxNTI2NzI1MTQyLCJwZXJtaXNzaW9ucyI6eyJpbWFnZSI6NX19.JxLq6E9XGXltDK1iJMFS4K5j4eUzhs7XsWQ0krdYhjw")
                            .build()
                            return@addInterceptor chain.proceed(request)
                        }.build()
                }
                .error(R.drawable.ic_your_water_logo)
                .build()
        )
        val painterState = painter.state
        if (painterState is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator()
        }
        Image(
            painter = painter,
            contentDescription = stringResource(id = R.string.description_image_product),
            modifier = Modifier
                .padding(16.dp)
                .height(248.dp)
                .fillMaxWidth(),
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = product.appName ?: product.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )


    }
}

@Composable
fun ProductPriceInfo(price: Int, getPrice: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if (price == -1) {
            Text(
                text = "Товара нет",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = "от $price₽",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
            )
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { getPrice() },
                painter = painterResource(id = R.drawable.ic_help_24),
                contentDescription = stringResource(id = R.string.description_image_product),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AddInBasketButton(product: InfoProduct, addProductInBasket: (InfoProduct) -> Unit) {
    Button(
        modifier = Modifier
            .padding(start = 8.dp, bottom = 4.dp, end = 4.dp, top = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = {
            addProductInBasket(product)
        },
        enabled = product.price.isNotEmpty()
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(4.dp),
                painter = painterResource(id = R.drawable.ic_basket_icon),
                contentDescription = stringResource(id = R.string.description_add_product),
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = "Добавить за ${product.getPriceOnCount(product.count)}₽",
                textAlign = TextAlign.Center
            )
        }

    }
}

@Composable
fun AboutProduct(product: InfoProduct, addProductInBasket: (InfoProduct) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()
    ){
        val scrollState = rememberScrollState()
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 78.dp, top = 8.dp)
                .verticalScroll(enabled = true, state = scrollState),
            text = product.description,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyLarge
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 24.dp,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var count by rememberSaveable { mutableIntStateOf(product.count) }
                IconButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        if (count > 1)
                            count -= 1
                        else
                            count = 1
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(id = R.drawable.ic_btn_minus),
                        contentDescription = stringResource(id = R.string.description_add_product),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }

                Text(
                    text = "$count",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        if (product.category != 20) {
                            count += 1
                        }
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(id = R.drawable.ic_btn_plus),
                        contentDescription = stringResource(id = R.string.description_add_product_count),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                AddInBasketButton(
                    product = product.copy(count = count),
                    addProductInBasket = addProductInBasket
                )
            }
        }
    }
}

@Preview
@Composable
fun AboutProductScreenPreview() {
    YourWaterTheme {
        val product = InfoProduct(
            id = 85,
            companyId = 7,
            dateCreated = "2023-08-31T11:17:24.273128+03:00",
            createdBy = 0,
            updatedBy = 0,
            name = "НЕ Другая вода",
            shname = "НЕ Другая вода",
            appName = "НЕ Другая вода",
            price = listOf(
                Price(border = 1, price = 10),
                Price(border = 2, price = 22),
                Price(border = 3, price = 33)
            ),
            category = 1,
            description = "text123",
            image = "RS6hB7BkzDA-eEhbRfQ8.jpg",
            siteDisplay = false,
            appDisplay = true,
            measure = 1,
            minPrice = 10,
            isFavorite = false,
            isActive = true
        )
        Column(modifier = Modifier.fillMaxSize()) {
            InfoProduct(product)
            ProductPriceInfo(product.minPrice){}
            AboutProduct(product){}
        }


    }
}