package ru.iwater.youwater.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import ru.iwater.youwater.R
import ru.iwater.youwater.data.*
import ru.iwater.youwater.network.ImageUrl
import ru.iwater.youwater.screen.component.product.ProductCard
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    favorite: Favorite,
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit,
    watterViewModel: HomeViewModel = viewModel( factory = HomeViewModel.Factory),
    navController: NavHostController
) {
    watterViewModel.getProductsList(favorite)
    val lastOrder by watterViewModel.lastOrder.observeAsState()
    val promoBanner by watterViewModel.promoBanners.observeAsState()
    var bannerName by remember {
        mutableStateOf("")
    }
    var bannerDescription by remember {
        mutableStateOf("")
    }
    val promoPagerState = rememberPagerState {
        promoBanner?.size ?: 0
    }
    val productsListState = rememberLazyListState()
    val startPocket by watterViewModel.startPocket.observeAsState()
    val productsList by watterViewModel.productList.observeAsState()
    var itemBanner = 0

    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheet = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    val showModalSheet = rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val expandedFab by remember {
        derivedStateOf {
            productsListState.firstVisibleItemIndex == 0
        }
    }
    val scope = rememberCoroutineScope()

    if (skipPartiallyExpanded) {
        ModalBottomSheet(
            sheetState = bottomSheet,
            onDismissRequest = {
                skipPartiallyExpanded = false
            },
            windowInsets = BottomSheetDefaults.windowInsets,
            content = {
                BannerInfoScreen(namePromo = bannerName, promoDescription = bannerDescription)
            }
        )
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(snackbarData = snackbarData)
            } },
        floatingActionButton = {
            if (lastOrder != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate(
                            MainNavRoute.CreateOrderScreen.withArgs(false.toString(), lastOrder.toString())
                        ) },
                    expanded = expandedFab,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = stringResource(id = R.string.repeat_last_order)
                        ) },
                    text = { Text(text = stringResource(id = R.string.repeat_last_order)) },
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.primary
                    )
                }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (productsList?.isEmpty() == true) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ProductContent(
                    promoBanner = promoBanner,
                    promoPagerState = promoPagerState,
                    getInfoBanner = {
                        bannerName = it?.name ?: ""
                        bannerDescription = it?.description ?: ""
                        showModalSheet.value = !showModalSheet.value
                        scope.launch {
                            skipPartiallyExpanded = !skipPartiallyExpanded
                        } },
                    catalogList = watterViewModel.catalogList,
                    productsList = productsList ?: emptyList(),
                    productsListState = productsListState,
                    getAboutProduct = {
                        navController.navigate(MainNavRoute.AboutProductScreen.withArgs(it.toString()))
                                      },
                    addProductInBasket = { product ->
                        if (product.price.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${product.name} не доступен для заказа",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            addProductInBasket(product, 1, startPocket?: false)
//                            watterViewModel.addProductToBasket(product)
                            scope.launch {
                                val messageAddProduct = snackbarHostState.showSnackbar(
                                    message = "${product.name} добавлен в корзину",
                                    actionLabel = "Корзина",
                                    duration = SnackbarDuration.Short
                                )
                                when (messageAddProduct) {
                                    SnackbarResult.ActionPerformed -> {
                                        navController.navigate(MainNavRoute.BasketScreen.path)
                                    }
                                    else -> {}
                                }
                            }
                        } },
                    onCheckedFavorite = { product, onFavorite ->
                        onCheckedFavorite(product, onFavorite)
                    }
                )
            }
        }
    }

    LaunchedEffect(itemBanner) {
        watterViewModel.viewModelScope.launch {
            while (itemBanner <= (promoBanner?.size ?: 0)) {
                delay(5000)
                if (itemBanner != promoBanner?.size) {
                    itemBanner++
                    promoPagerState.scrollToPage(itemBanner)
                } else {
                    itemBanner = 0
                    promoPagerState.scrollToPage(itemBanner)
                }
            }
        }
    }
}

@Composable
fun CatalogName(name: String, modifier: Modifier) {
    Text(
        modifier = modifier,
        text = name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductContent(
    promoBanner: List<Banner>?,
    promoPagerState: PagerState,
    getInfoBanner: (Banner?) -> Unit,
    catalogList: List<Category>,
    productsList: List<NewProduct>,
    productsListState: LazyListState,
    addProductInBasket: (NewProduct) -> Unit,
    getAboutProduct: (Int) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit
) {
    LazyColumn (state = productsListState) {
        if (promoPagerState.pageCount != 0) {
            item {
                PromoAction(
                    promo = promoBanner,
                    pagerState = promoPagerState,
                    getInfoBanner = getInfoBanner
                )
            }
        }

        items(catalogList.size) { catalogIndex ->
            ProductsByCategoryRow(
                categoryName = catalogList[catalogIndex].name,
                productsList = productsList.filter { it.category == catalogList[catalogIndex].id },
                getAboutProduct = getAboutProduct,
                addProductInBasket = addProductInBasket,
                onCheckedFavorite = onCheckedFavorite
            )
        }
    }
}

@Composable
fun ProductsByCategoryRow(
    categoryName: String?,
    productsList: List<NewProduct>?,
    getAboutProduct: (Int) -> Unit,
    addProductInBasket: (NewProduct) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        if (categoryName != null) {
            CatalogName(name = categoryName, Modifier.padding(8.dp))
        }
        LazyRow {
            if (productsList != null) {
                items(
                    count = productsList.size,
                    ) { productIndex ->
                    ProductCard(
                        product = productsList[productIndex],
                        getAboutProduct = getAboutProduct,
                        addProductInBasket = addProductInBasket,
                        onCheckedFavorite = { isFavorite ->
                            onCheckedFavorite(
                                productsList[productIndex],
                                isFavorite
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoImage(
    banners: List<Banner>,
    pagerState: PagerState,
    getInfoBanner: (Banner?) -> Unit
) {
    HorizontalPager(state = pagerState) { page ->
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val painter = rememberAsyncImagePainter(
                model = "$ImageUrl/${banners[page].picture}/",
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
                    .padding(8.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { getInfoBanner(banners[page]) }
            )
        }
    }
    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoAction(
    promo: List<Banner>?,
    pagerState: PagerState,
    getInfoBanner: (Banner?) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(182.dp),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.stocks),
//                style = YouWaterTypography.h6,
                fontWeight = FontWeight.Bold,
//                color = Color.White
            )
            PromoImage(banners = promo ?: emptyList(), pagerState, getInfoBanner)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun HomeScreenPreview() {
    YourWaterTheme {
        val productsList1: List<NewProduct> = List(100) {
            NewProduct(
                id = it,
                name = "Plesca Классическая 19л в оборотной таре",
                appName = "Plesca Классическая оборотной таре",
                price = listOf(Price(1, 310)),
                category = 1,
                image = "cat-1.png"
            )
        }
        val catalogNames: List<String> = List(100) { "$it" }
        val promoTest: List<Banner> = List(10) {
            Banner(
                id = it,
                name = "name $it",
                promoCode = "",
                picture = "iwater_logistic.PNG",
                discountValue = 10.0,
                discountType = 1,
                description = "ПРОЦЕНТ",
                isActive = true,
                endDate = "",
                startDate = "",
                productList = List(1) { product ->
                    ProductX(product, name = "name $product")
                }
            )
        }
        Column {
            PromoAction(promoTest, rememberPagerState {
                promoTest.size
            }) {}
            LazyColumn {
                items(catalogNames.size) { catalog ->
                    ProductsByCategoryRow(
                        categoryName = "Catalog #$catalog",
                        productsList = productsList1,
                        {},
                        {},
                        { _, _ -> run {} }
                    )
                }
            }
        }
    }
}