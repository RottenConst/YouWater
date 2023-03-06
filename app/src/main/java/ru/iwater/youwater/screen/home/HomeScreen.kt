package ru.iwater.youwater.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.ProductXX
import ru.iwater.youwater.data.PromoBanner
import ru.iwater.youwater.network.ImageUrl
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.CatalogListViewModel

@Composable
fun HomeScreen(catalogListViewModel: CatalogListViewModel) {
    val promoBanner = catalogListViewModel.promoBanners.observeAsState()
    val catalogList = catalogListViewModel.catalogList.observeAsState()
    val catalogProductMap = catalogListViewModel.catalogProductMap.observeAsState()
    val listState = rememberLazyListState()
    Column {
        PromoAction(promo = promoBanner.value, listState)
        LazyColumn {
                items(catalogList.value?.size ?: 0) { catalog ->
                    val products = catalogProductMap.value?.get(catalogList.value?.get(catalog) ?: 0)
                    CategoryProduct(
                        categoryName = catalogList.value?.get(catalog)?.category,
                        productsList = products
                    )
                }
            }
    }
}

@Composable
fun CatalogName(name: String) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = name,
        style = YouWaterTypography.h6,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PromoImage(banners: List<PromoBanner>?, listState: LazyListState) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
//        contentPadding = PaddingValues(8.dp),
        state = listState
    ) {
        items(banners?.size ?: 0) {
            Box(
                modifier = Modifier
                    .fillParentMaxWidth(),
            ) {
                GlideImage(
                    imageModel = { "$ImageUrl/${banners?.get(it)?.picture}" },
                    imageOptions = ImageOptions (
                        alignment = Alignment.Center,
                        contentDescription = stringResource(id = R.string.description_image_logo),
                        contentScale = ContentScale.FillWidth,
                    ),
                    requestOptions = {
                        RequestOptions()
                            .placeholder(R.drawable.ic_your_water_logo)
                            .error(R.drawable.ic_your_water_logo)
                    },
                    modifier = Modifier
//                        .fillMaxSize()
//                        .fillMaxHeight()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
//                        .aspectRatio(16f / 4f)
                )
            }
        }

    }
}

@Composable
fun PromoAction(promo: List<PromoBanner>?, listState: LazyListState) {
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
            CatalogName(name = "Акции")
            PromoImage(banners = promo, listState)
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Surface(
        modifier = Modifier
            .width(152.dp)
            .height(200.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            FavoriteIconButton()
            Column(modifier = Modifier.padding(8.dp)) {
                ProductInfo(product)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProductCost()
                    ProductPlusButton()
                }
            }
        }
    }
}

@Composable
fun CategoryProduct(categoryName: String?, productsList: List<Product>?) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        if (categoryName != null) {
            CatalogName(name = categoryName)
        }
        LazyRow {
            if (productsList != null) {
                items(productsList.size) { product ->
                    ProductCard(product = productsList[product])
                }
            }
        }
    }
}

@Composable
private fun FavoriteIconButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_like),
            contentDescription = stringResource(id = R.string.description_add_product),
            tint = Blue500
        )
    }
}

@Composable
fun ProductInfo(product: Product) {
    GlideImage(
        imageModel = {"$ImageUrl/${product.gallery}"},
        requestOptions = {
            RequestOptions()
                .placeholder(R.drawable.ic_your_water_logo)
                .error(R.drawable.ic_your_water_logo)
        },
        imageOptions = ImageOptions(
            alignment = Alignment.TopCenter,
            contentDescription = stringResource(id = R.string.description_image_product),
            contentScale = ContentScale.Inside
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp)
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
        text = product.app_name ?: product.name,
        style = YouWaterTypography.caption,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        maxLines = 3
    )
}

@Composable
fun ProductCost() {
    Box(
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = "от 100Р",
            color = Blue500,
            style = YouWaterTypography.caption,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProductPlusButton() {
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_btn_plus),
            contentDescription = stringResource(id = R.string.description_add_product),
            tint = Blue500,
            modifier = Modifier.clickable { }
        )
    }
}

//@Preview
//@Composable
//fun PromoActionPreview() {
//    YourWaterTheme {
//        PromoAction()
//    }
//}

@Preview
@Composable
fun HomeScreenPreview() {
    YourWaterTheme {
        val productsList1: List<Product> = List(100) { Product(
            id = it,
            name = "Plesca Натуральная 19л в оборотной таре",
            shname = "PLN",
            app_name = "Plesca Натуральная в оборотной таре",
            price = "1:355;2:325;4:300;8:280;10:250;20:240;",
            discount = 0,
            category = 1,
            about = "",
            gallery = "008.png",
            date_created = 1676121935,
            date = "11/02/2023",
            site = 1,
            app = 1,
            company_id = "0007"
        ) }
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
            products = List(1) {product ->
                ProductXX(product, name = "name $product")
            },
            display_in_app = true)
        }
        Column {
            PromoAction(promoTest, rememberLazyListState())
            LazyColumn {
                items(catalogNames.size) { catalog ->
                    CategoryProduct(
                        categoryName = "Catalog #$catalog",
                        productsList = productsList1
                    )
                }
            }
        }
    }
}