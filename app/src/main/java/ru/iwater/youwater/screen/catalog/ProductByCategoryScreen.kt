package ru.iwater.youwater.screen.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.screen.home.CatalogName
import ru.iwater.youwater.screen.home.ProductCard
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun ProductByCategory(productsList: List<Product>, categoryName: String, getAboutProduct: (Int) -> Unit, addProductInBasket: (Product) -> Unit, addToFavorite: (Int) -> Unit, deleteFavorite: (Int) -> Unit) {
    val products = productsList.toMutableStateList()
    if (productsList.isNotEmpty()) {
        Column {
            CatalogName(
                name = categoryName,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            ProductGrid(
                productList = products,
                countGrid = 2,
                getAboutProduct = {getAboutProduct(it)},
                addProductInBasket = {addProductInBasket(it)},
                onCheckedFavorite = {product -> if (product.onFavoriteClick) deleteFavorite(product.id) else addToFavorite(product.id) }
            )
        }
    }

}

@Composable
fun ProductGrid(productList: List<Product>, countGrid: Int, getAboutProduct:(Int) -> Unit, addProductInBasket: (Product) -> Unit, onCheckedFavorite:(Product) -> Unit) {
    LazyVerticalGrid(
        modifier = Modifier.padding(bottom = 60.dp),
        columns = GridCells.Fixed(countGrid),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            productList.size
        ) {productIndex->
            ProductCard(
                product = productList[productIndex],
                getAboutProduct = {getAboutProduct(productList[productIndex].id)},
                addProductInBasket = {addProductInBasket(productList[productIndex])},
                onCheckedFavorite = {isFavorite -> onCheckedFavorite(productList[productIndex])}
            )
        }
    }
}

@Preview
@Composable
fun ProductByCategoryPreview() {
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
        Column {
            CatalogName(name = "Name Catalog", Modifier.padding(start = 16.dp, top = 16.dp))
            ProductGrid(
                productList = productsList1,
                countGrid = 2,
                {},
                {},
                {product -> {} }
            )
        }
        
    }
}