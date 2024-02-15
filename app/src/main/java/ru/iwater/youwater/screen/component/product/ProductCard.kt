package ru.iwater.youwater.screen.component.product

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import okhttp3.OkHttpClient
import ru.iwater.youwater.R
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Price
import ru.iwater.youwater.network.ImageUrl
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun ProductCard(
    product: NewProduct,
    getAboutProduct: (Int) -> Unit,
    addProductInBasket: (NewProduct) -> Unit,
    onCheckedFavorite: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .width(152.dp)
            .height(200.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { getAboutProduct(product.id) }
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                ProductInfo(Modifier.weight(1f), product)
                val priceList = product.price.ifEmpty { listOf(Price(1, -1)) }
                ProductCost(
                    id = product.id,
                    minPrice = priceList.first().price,
                    nameProduct = product.name,
                    prices = priceList,
                    addProductInBasket = {addProductInBasket(product)}
                )
            }
            StatefulFavoriteButton(
                isFavorite = product.onFavoriteClick,
                onCheckedFavorite = { onFavorite -> onCheckedFavorite(onFavorite) })
        }
    }
}

@Composable
fun ProductInfo(modifier: Modifier, product: NewProduct) {
//    GlideImage(
//        imageModel = { "$ImageUrl/${product.image}/" },
//        loading = {
//            Box(modifier = modifier.matchParentSize()) {
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center),
//                    color = Blue500
//                )
//            }
//        },
//        failure = {
//            Image(
//                modifier = modifier.matchParentSize(),
//                painter = painterResource(id = R.drawable.ic_your_water_logo),
//                contentDescription = stringResource(id = R.string.description_image_product),
//                alignment = Alignment.TopCenter
//            )
//        },
//        previewPlaceholder = R.drawable.ic_your_water_logo,
//        imageOptions = ImageOptions(
//            alignment = Alignment.TopCenter,
//            contentDescription = stringResource(id = R.string.description_image_product),
//            contentScale = ContentScale.Inside
//        ),
//        modifier = modifier
//            .fillMaxWidth()
//            .height(98.dp)
//    )
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
        modifier = modifier
            .fillMaxWidth()
            .height(98.dp)
    )
    Text(
        modifier = modifier
            .fillMaxWidth(),
        text = product.appName,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        maxLines = 3
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
fun ProductCost(id: Int, minPrice: Int, nameProduct: String, prices: List<Price>, addProductInBasket: () -> Unit) {
    var openDialog by remember { mutableStateOf(false) }
    InfoPriceProduct(
        openInfoPrice = openDialog,
        setOpenInfo = {openDialog = !openDialog},
        nameProduct = nameProduct,
        prices = prices,
        minPrice = minPrice
    )
    Box(
        contentAlignment = Alignment.BottomStart
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            if (id == 81 || id == 84 && minPrice != -1) {
                Text(
                    text = "oт ${minPrice}₽",
                    color = Color.Gray,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start,
                    textDecoration = TextDecoration.LineThrough,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(
                modifier = Modifier.padding(0.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                if (minPrice == -1){
                    Text(
                        modifier = Modifier.weight(9F),
                        text = "Товара нет",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = when (id) {
                            81 -> "от ${minPrice - 30}₽"
                            84 -> "от ${minPrice - 30}₽"
                            else -> "от ${minPrice}₽"
                        },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = {
                        openDialog = true
                    },
                    modifier = Modifier
                        .weight(4f)
                        .size(16.dp)
                ) {
                    Icon(
                        modifier = Modifier.padding(start = 2.dp),
                        painter = painterResource(id = R.drawable.ic_help_24),
                        contentDescription = stringResource(id = R.string.description_image_product),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.weight(3f))
                ProductPlusButton(Modifier.weight(4f)) {
                    addProductInBasket()
                }
            }

        }
    }
}

@Composable
fun InfoPriceProduct(openInfoPrice: Boolean, setOpenInfo: (Boolean) -> Unit, nameProduct: String, minPrice: Int, prices: List<Price>) {
    if (openInfoPrice) {
        AlertDialog(
            onDismissRequest = { setOpenInfo(false) },
            title = {
                Text(text = nameProduct,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                if (minPrice == -1) {
                    Text(text = "Товара нет в наличии")
                } else
                    LazyColumn(modifier = Modifier.padding(8.dp)) {
                        items(prices.size) { itemIndex ->
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (itemIndex == 0) {
                                    Text(
                                        text = "От одной шт.",
//                                        style = YouWaterTypography.subtitle1
                                    )
                                } else {
                                    Text(
                                        text = "От ${prices[itemIndex].border} шт.",
//                                        style = YouWaterTypography.subtitle1
                                    )
                                }
                                Text(
                                    text = "${prices[itemIndex].price}pyб./шт.",
//                                    style = YouWaterTypography.subtitle1
                                )
                            }
                        }
                    }
            },
            confirmButton = {
                TextButton(onClick = { setOpenInfo(false) }) {
                    Text(text = stringResource(id = R.string.general_ok))
                }
            }
        )
    }

}
@Composable
fun ProductPlusButton(modifier: Modifier = Modifier , addProductInBasket: () -> Unit) {
    IconButton(
        onClick = { addProductInBasket() },
        modifier = Modifier
            .padding(0.dp)
            .size(24.dp)
    ) {
        Icon(
//            modifier = modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_btn_plus),
            contentDescription = stringResource(id = R.string.description_add_product),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun IconLike(idIconLike: Int) {
    Icon(
        modifier = Modifier.padding(1.dp),
        painter = painterResource(id = idIconLike),
        contentDescription = stringResource(id = R.string.description_add_product),
        tint = MaterialTheme.colorScheme.primary
    )
}



@Preview
@Composable
fun ProductCardPreview() {
    YourWaterTheme {
        val productTest = NewProduct(
            id = 84,
            name = "Plesca Классическая 19л в оборотной таре",
            appName = "Plesca Классическая оборотной таре",
            price = listOf(Price(1, -1)),
            category = 1,
            image = "lI32oIQ3rn0-z03sTaxk.jpg",
            onFavoriteClick = true
        )

        ProductCard(
            product = productTest,
            getAboutProduct = {},
            addProductInBasket = {},
            onCheckedFavorite = {}
        )
    }
}