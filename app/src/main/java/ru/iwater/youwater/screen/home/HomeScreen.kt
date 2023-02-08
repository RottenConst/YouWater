package ru.iwater.youwater.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

//@Composable
//fun HomeScreen() {
//
//}

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
fun ProductCard(product: String) {
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
fun CategoryProduct(categoryName: String, productsList: List<String>) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        CatalogName(name = categoryName)
        LazyRow {
            items(productsList.size) {product ->
                ProductCard(product = "Product #$product")
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
fun ProductInfo(name: String) {
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp),
        painter = painterResource(id = R.drawable.ic_your_water_logo),
        contentDescription = stringResource(id = R.string.description_image_product),
        alignment = Alignment.TopCenter
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
        text = name,
        style = YouWaterTypography.caption,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
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
            modifier = Modifier.clickable {  }
        )
    }
}

@Preview (widthDp = 320)
@Composable
fun HomeScreenPreview() {
    YourWaterTheme {
        val productsList1: List<String> = List(100) {"$it"}
        val catalogNames: List<String> = List(100) {"$it"}
        Column() {
            LazyColumn {
                items(catalogNames.size) {catalog ->
                    CategoryProduct(categoryName = "Catalog #$catalog", productsList = productsList1)
                }
            }
        }
    }
}