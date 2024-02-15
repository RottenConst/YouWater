package ru.iwater.youwater.screen.component.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
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
import ru.iwater.youwater.theme.YourWaterTheme

/*
 * Карточка продукта для корины
 */
@Composable
fun CardProductInBasket(
    product: NewProduct,
    getAboutProduct: (Int) -> Unit,
    deleteProduct: () -> Unit,
    plusCount: (Int) -> Unit,
    minusCount: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(102.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .height(102.dp)
                .clickable { getAboutProduct(product.id) }
        ) {
            Row {
                ImageProduct(image = product.image)
                Column(modifier = Modifier.wrapContentWidth()) {
                    TitleProduct(product.name) { deleteProduct() }
                    CostProducts(
                        product = product,
                        plusCount = { plusCount(it) },
                        minusCount = { minusCount(it) }
                    )
                }
            }
        }
    }
}

/*
 * Название товара
 */
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
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            maxLines = 2,
            modifier = Modifier.width(200.dp)
        )
        Icon(
            modifier = Modifier.clickable { isVisible = true },
            painter = painterResource(id = R.drawable.ic_cancel),
            contentDescription = stringResource(id = R.string.description_ic_delete),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

/*
 * Изображение товара
 */
@Composable
fun ImageProduct(image: String) {
//    GlideImage(
//        imageModel = { "$ImageUrl/$image" },
//        loading = {
//            Box(
//                modifier = Modifier.wrapContentWidth()
//            ) {
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//        },
//        failure = {
//            Image(
//                painter = painterResource(id = R.drawable.ic_your_water_logo),
//                contentDescription = stringResource(id = R.string.description_image_product),
//                alignment = Alignment.Center
//            )
//        },
//        previewPlaceholder = R.drawable.ic_your_water_logo,
//        imageOptions = ImageOptions(
//            alignment = Alignment.Center,
//            contentDescription = stringResource(id = R.string.description_image_product),
//            contentScale = ContentScale.Inside
//        ),
//        modifier = Modifier
//            .padding(8.dp)
//    )

    val painter = rememberAsyncImagePainter(
        model = "$ImageUrl/$image/",
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
        alignment = Alignment.Center,
        contentScale = ContentScale.Inside,
        modifier = Modifier
            .padding(8.dp)
            .width(72.dp)
    )
}

/*
 * Отображение стоимости товара
 */
@Composable
fun CostProducts(product: NewProduct, plusCount: (Int) -> Unit, minusCount: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        if (product.id == 81 || product.id == 84) {
            Column {
                Text(
                    text = "${product.getPriceNoDiscount(product.count)}₽",
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
                Text(
                    text = "${product.getPriceOnCount(product.count)}₽",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Text(
                text = "${product.getPriceOnCount(product.count)}₽",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            contentAlignment = Alignment.BottomEnd) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween) {
                MinusProductButton {
                    minusCount(product.count)
                }
                Text(
                    text = "${product.count}",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                PlusProductButton {
                    plusCount(product.count)
                }
            }
        }

    }
}

@Composable
fun PlusProductButton(plusClick: () -> Unit) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Icon(
            modifier = Modifier.clickable { plusClick() },
            painter = painterResource(id = R.drawable.ic_btn_plus),
            contentDescription = stringResource(id = R.string.description_ic_delete),
            tint = MaterialTheme.colorScheme.primary
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
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun DeleteProductDialog(productName: String, isVisible: Boolean, setVisible: (Boolean) -> Unit, deleteProduct: () -> Unit) {
    if (isVisible) {
        AlertDialog(
            icon = {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(id = R.string.delete_product_text), tint = MaterialTheme.colorScheme.primary)
            },
            title = {
                Text(text = "Удалить $productName из корзины?", textAlign = TextAlign.Center)
            },
            onDismissRequest = { setVisible(false) },
            dismissButton = {
                TextButton(onClick = { setVisible(false) }) {
                    Text(text = stringResource(id = R.string.general_no), color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    setVisible(false)
                    deleteProduct()
                }) {
                    Text(text = stringResource(id = R.string.general_yes), color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Preview
@Composable
fun CardProductInBasketPreview() {
    YourWaterTheme {
        val product = NewProduct(
            appName = "Plesca Натуральная в оборотной таре",
            category = 1,
            id = 12,
            name = "Plesca Натуральная 19л в оборотной таре",
            image = "cat-1.png",
            price = listOf(Price(1, 355), Price(2, 325), Price(4, 300), Price(8, 280), Price(10, 250), Price(20, 240)),
            onFavoriteClick = false,
            count = 2
        )
        CardProductInBasket(
            product = product,
            getAboutProduct = {},
            deleteProduct = {},
            plusCount = {}
        ) {}
    }
}