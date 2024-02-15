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
import ru.iwater.youwater.data.Favorite
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Price
import ru.iwater.youwater.screen.component.product.ProductCard
import ru.iwater.youwater.screen.home.CatalogName
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.HomeViewModel

@Composable
fun ProductByCategory(
    favorite: Favorite,
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit,
    catalogId: Int,
    watterViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    navController: NavHostController
) {
    watterViewModel.getProductOfCategory(categoryId = catalogId, favorite = favorite)
    val startPocket by watterViewModel.startPocket.observeAsState()
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
                Snackbar(snackbarData = snackbarData)
            }
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            CatalogName(
                name = watterViewModel.catalogList.find { typeProduct -> typeProduct.id == catalogId }?.name ?: "",
                modifier = modifier.padding(start = 16.dp, top = 16.dp)
            )
            productsList?.filter { product -> product.category == catalogId }?.let {
                ProductGrid(
                    modifier = modifier,
                    productsList = it,
                    countGrid = 2,
                    getAboutProduct = { productId ->
                        navController.navigate(
                            MainNavRoute.AboutProductScreen.withArgs(productId.toString())
                        )
                    },
                    addProductInBasket = { product ->
                        addProductInBasket(product, 1, startPocket ?: false)
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
                        onCheckedFavorite(product, isFavorite)
                    }
                )
            }
        }
    }

}

@Composable
fun ProductGrid(
    modifier: Modifier,
    productsList: List<NewProduct>,
    countGrid: Int,
    getAboutProduct: (Int) -> Unit,
    addProductInBasket: (NewProduct) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit
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
                addProductInBasket = {
                    addProductInBasket(productsList[productIndex]) },
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