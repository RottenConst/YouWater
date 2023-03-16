package ru.iwater.youwater.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.network.ImageUrl
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.AboutProductViewModel

@Composable
fun AboutProductScreen(aboutProductViewModel: AboutProductViewModel, productId: Int) {
    aboutProductViewModel.initProduct(productId)
    val product by aboutProductViewModel.product.observeAsState()
    if (product != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            InfoProduct(product = product!!)
            ProductPriceInfo(price = product!!.getMinPriceProduct())
            AboutProduct(product!!) {aboutProductViewModel.addProductToBasket1(it)}
        }
    }
}

@Composable
fun InfoProduct(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            imageModel = { "$ImageUrl/${product.gallery}" },
            modifier = Modifier
                .padding(16.dp)
                .height(248.dp)
                .fillMaxWidth(),
            previewPlaceholder = R.drawable.ic_your_water_logo,
            loading = {
                Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            failure = {
                Image(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(248.dp)
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.ic_your_water_logo),
                    contentDescription = stringResource(id = R.string.description_image_product))
            },
            imageOptions = ImageOptions(
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit,
                contentDescription = stringResource(id = R.string.description_image_product),
            )

        )
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = product.app_name ?: product.name,
            textAlign = TextAlign.Center,
            style = YouWaterTypography.h6,
            fontWeight = FontWeight.Bold
        )


    }
}

@Composable
fun ProductPriceInfo(price: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "от $price₽",
            style = YouWaterTypography.subtitle1,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
        Icon(
            modifier = Modifier.padding(start = 8.dp),
            painter = painterResource(id = R.drawable.ic_help_24),
            contentDescription = stringResource(id = R.string.description_image_product),
            tint = Blue500
        )
    }
}

@Composable
fun AddInBasketButton(product: Product, addProductInBasket: (Product) -> Unit) {
    Button(
        modifier = Modifier
            .padding(start = 8.dp, end = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = { addProductInBasket(product) }
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
fun AboutProduct(product: Product, addProductInBasket: (Product) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()
    ){
        val scrollState = rememberScrollState()
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 78.dp, top = 8.dp)
                .verticalScroll(enabled = true, state = scrollState),
            text = product.about,
            textAlign = TextAlign.Justify,
            color = Color.Gray,
            style = YouWaterTypography.body1
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            border = BorderStroke(0.5.dp, Color.Gray),
            elevation = 24.dp,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var count by rememberSaveable { mutableStateOf(product.count) }
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
                        tint = Color.Gray
                    )
                }

                Text(
                    text = "$count",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    style = YouWaterTypography.h5,
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
                        tint = Blue500
                    )
                }

                AddInBasketButton(product.copy(count = count), addProductInBasket)
            }
        }
    }
}

@Preview
@Composable
fun AboutProductScreenPreview() {
    YourWaterTheme {
        val product = Product(
            about = "About product",
            app = 1,
            app_name = "App Name product",
            category = 1,
            company_id = "007",
            date = "date",
            date_created = 1,
            discount = 0,
            gallery = "008.png",
            id = 81,
            name = "Name Product",
            price = "1:100;2:200;",
            shname = "",
            site = 1,
            count = 0,
            onFavoriteClick = false
        )
        Column(modifier = Modifier.fillMaxSize()) {
            InfoProduct(product)
            ProductPriceInfo(product.getMinPriceProduct())
            AboutProduct(product){}
        }


    }
}