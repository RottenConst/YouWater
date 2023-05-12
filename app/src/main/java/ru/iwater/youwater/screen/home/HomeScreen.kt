package ru.iwater.youwater.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.data.*
import ru.iwater.youwater.network.ImageUrl
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.CatalogListViewModel

@Composable
fun HomeScreen(
    catalogListViewModel: CatalogListViewModel = viewModel(),
    navController: NavController
) {
    val lastOrder by catalogListViewModel.lastOrder.observeAsState()
    val promoBanner by catalogListViewModel.promoBanners.observeAsState()
    val promoListState = rememberLazyListState()
    val productsList = catalogListViewModel.productList
    var itemBanner = 0
    LaunchedEffect(Unit) {
        catalogListViewModel.viewModelScope.launch {
            while (itemBanner <= (promoBanner?.size ?: 0)) {
                delay(5000)
                if (itemBanner != promoBanner?.size) {
                    itemBanner++
                    promoListState.scrollToItem(itemBanner)
                } else {
                    itemBanner = 0
                    promoListState.scrollToItem(itemBanner)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (lastOrder != null) {
                FloatingActionButton(onClick = {
                    navController.navigate(
                        HomeFragmentDirections.actionHomeFragmentToCreateOrderFragment(
                            false,
                            lastOrder!!
                        )
                    )
                }, modifier = Modifier.padding(bottom = 60.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_basket_icon),
                        contentDescription = "Повторить последний заказ"
                    )
                }
            }
        })
    {paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PromoAction(promo = promoBanner, promoListState) {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToBannerInfoBottomSheetFragment(
                        it!!.name,
                        it.description
                    )
                )
            }
            if (productsList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ProductContent(
                    catalogList = catalogListViewModel.catalogList,
                    productsList = productsList,
                    getAboutProduct = {
                        navController.navigate(
                            HomeFragmentDirections.actionShowAboutProductFragment(
                                it
                            )
                        )
                    },
                    addProductInBasket = { catalogListViewModel.addProductToBasket(it) },
                    onCheckedFavorite = { product, onFavorite ->
                        catalogListViewModel.onChangeFavorite(
                            productId = product.id,
                            onFavorite = onFavorite
                        )
                    }
                )
            }
        }
    }


}

@Composable
fun CatalogName(name: String, modifier: Modifier) {
    Text(
        modifier = modifier,
        text = name,
        style = YouWaterTypography.h6,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ProductContent(
    catalogList: List<TypeProduct>,
    productsList: List<Product>,
    addProductInBasket: (Product) -> Unit,
    getAboutProduct: (Int) -> Unit,
    onCheckedFavorite: (Product, Boolean) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(bottom = 60.dp)) {
        items(catalogList.size) { catalogIndex ->
            ProductsByCategoryRow(
                categoryName = catalogList[catalogIndex].category,
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
    productsList: List<Product>?,
    getAboutProduct: (Int) -> Unit,
    addProductInBasket: (Product) -> Unit,
    onCheckedFavorite: (Product, Boolean) -> Unit
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
                    key = { product -> productsList[product].id }) { productIndex ->
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

@Composable
fun PromoImage(
    banners: List<PromoBanner>?,
    listState: LazyListState,
    getInfoBanner: (PromoBanner?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        state = listState
    ) {
        items(banners?.size ?: 0) {
            Box(
                modifier = Modifier
                    .fillParentMaxWidth(),
            ) {
                GlideImage(
                    imageModel = { "$ImageUrl/${banners?.get(it)?.picture}" },
                    imageOptions = ImageOptions(
                        alignment = Alignment.Center,
                        contentDescription = stringResource(id = R.string.description_image_logo),
                        contentScale = ContentScale.FillWidth,
                    ),
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.matchParentSize()
                        )
                    },
                    failure = {
                        Text(text = "Не удалось загрузить картинку")
                    },
                    previewPlaceholder = R.drawable.ic_your_water_logo,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { getInfoBanner(banners?.get(it)) }
                )
            }
        }

    }
}

@Composable
fun PromoAction(
    promo: List<PromoBanner>?,
    listState: LazyListState,
    getInfoBanner: (PromoBanner?) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(182.dp),
        color = Blue500,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            CatalogName(name = "Акции", Modifier.padding(8.dp))
            PromoImage(banners = promo, listState, getInfoBanner)
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    getAboutProduct: (Int) -> Unit,
    addProductInBasket: (Product) -> Unit,
    onCheckedFavorite: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(152.dp)
            .height(200.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { getAboutProduct(product.id) }
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                ProductInfo(Modifier.weight(1f), product)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProductCost(product.id, product.getMinPriceProduct())
                    ProductPlusButton { addProductInBasket(product) }
                }
            }
            StatefulFavoriteButton(
                isFavorite = product.onFavoriteClick,
                onCheckedFavorite = { onFavorite -> onCheckedFavorite(onFavorite) })
        }
    }
}

@Composable
private fun IconLike(idIconLike: Int) {
    Icon(
        modifier = Modifier.padding(1.dp),
        painter = painterResource(id = idIconLike),
        contentDescription = stringResource(id = R.string.description_add_product),
        tint = Blue500
    )
}

@Composable
private fun StatefulFavoriteButton(isFavorite: Boolean, onCheckedFavorite: (Boolean) -> Unit) {
    var onFavorite by remember {
        mutableStateOf(isFavorite)
    }
    StatelessFavoriteButton(
        onFavorite = onFavorite,
        onCheckedFavorite = { favorite ->
            onFavorite = !favorite
            onCheckedFavorite(favorite)
        }
    )
}

@Composable
private fun StatelessFavoriteButton(onFavorite: Boolean, onCheckedFavorite: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 8.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = {
                onCheckedFavorite(onFavorite)
            }
        ) {
            if (onFavorite) {
                IconLike(idIconLike = R.drawable.ic_like_true)
            } else
                IconLike(idIconLike = R.drawable.ic_like)
        }
    }
}

@Composable
fun ProductInfo(modifier: Modifier, product: Product) {
    GlideImage(
        imageModel = { "$ImageUrl/${product.gallery}" },
        loading = {
            Box(modifier = modifier.matchParentSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        failure = {
            Image(
                modifier = modifier.matchParentSize(),
                painter = painterResource(id = R.drawable.ic_your_water_logo),
                contentDescription = stringResource(id = R.string.description_image_product),
                alignment = Alignment.TopCenter
            )
        },
        previewPlaceholder = R.drawable.ic_your_water_logo,
        imageOptions = ImageOptions(
            alignment = Alignment.TopCenter,
            contentDescription = stringResource(id = R.string.description_image_product),
            contentScale = ContentScale.Inside
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(98.dp)
    )
    Text(
        modifier = modifier
            .fillMaxWidth(),
        text = product.app_name ?: product.name,
        style = YouWaterTypography.caption,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        maxLines = 3
    )
}

@Composable
fun ProductCost(id: Int, minPrice: Int) {
    Box(
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            if (id == 81 || id == 84) {
                Text(
                    text = "${minPrice}₽",
                    color = Color.Gray,
                    fontWeight = FontWeight.Light,
                    textDecoration = TextDecoration.LineThrough,
                    style = YouWaterTypography.overline
                )
            }
            Text(
                text = if (id == 81 || id == 82) "от ${minPrice - 15}₽" else "от ${minPrice}₽",
                color = Blue500,
                style = YouWaterTypography.caption,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
fun ProductPlusButton(addProductInBasket: () -> Unit) {
    IconButton(
        onClick = { addProductInBasket() },
        modifier = Modifier
            .padding(0.dp)
            .size(24.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_btn_plus),
            contentDescription = stringResource(id = R.string.description_add_product),
            tint = Blue500,
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    YourWaterTheme {
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
        val catalogNames: List<String> = List(100) { "$it" }
        val promoTest: List<PromoBanner> = List(10) {
            PromoBanner(
                id = it,
                name = "name $it",
                promocode = "",
                picture = "iwater_logistic.PNG",
                discount = 10.0,
                discount_type = false,
                description = "ПРОЦЕНТ",
                is_active = true,
                end_date = "",
                start_date = "",
                products = List(1) { product ->
                    ProductXX(product, name = "name $product")
                },
                display_in_app = true
            )
        }
        Column {
            PromoAction(promoTest, rememberLazyListState()) {}
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