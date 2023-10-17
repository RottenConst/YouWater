package ru.iwater.youwater.screen.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.screen.home.CatalogName
import ru.iwater.youwater.screen.home.ProductCard
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun ProductByCategory(
    watterViewModel: WatterViewModel = viewModel(),
    catalogId: Int,
    navController: NavHostController
) {
    val productsList by watterViewModel.productList.observeAsState()
    val modifier = Modifier
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                snackbarData ->
                Snackbar(snackbarData = snackbarData, actionColor = Blue500)
            }
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            CatalogName(
                name = watterViewModel.catalogList.find { typeProduct -> typeProduct.id == catalogId }?.category ?: "",
                modifier = modifier.padding(start = 16.dp, top = 16.dp)
            )
            productsList?.filter { product -> product.category == catalogId }?.let {
                ProductGrid(
                    modifier = modifier,
                    productsList = it,
                    countGrid = 2,
                    getAboutProduct = {
                        navController.navigate(
                            MainNavRoute.AboutProductScreen.withArgs(it.toString())
                        )
                    },
                    addProductInBasket = { product ->
                        watterViewModel.addProductToBasket(product = product)
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
                        } },
                    onCheckedFavorite = { product, isFavorite ->
                        watterViewModel.onChangeFavorite(
                            productId = product.id,
                            isFavorite
                        )
                    }
                )
            }
        }
    }

}

@Composable
fun ProductGrid(
    modifier: Modifier,
    productsList: List<Product>,
    countGrid: Int,
    getAboutProduct: (Int) -> Unit,
    addProductInBasket: (Product) -> Unit,
    onCheckedFavorite: (Product, Boolean) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(countGrid),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            productsList.size
        ) { productIndex ->
            ProductCard(
                product = productsList[productIndex],
                getAboutProduct = { getAboutProduct(productsList[productIndex].id) },
                addProductInBasket = { addProductInBasket(productsList[productIndex]) },
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
                modifier = Modifier.padding(bottom = 60.dp),
                productsList = productsList1,
                countGrid = 2,
                getAboutProduct = {},
                addProductInBasket = {},
                onCheckedFavorite = { _, _ -> run {} }
            )
        }

    }
}