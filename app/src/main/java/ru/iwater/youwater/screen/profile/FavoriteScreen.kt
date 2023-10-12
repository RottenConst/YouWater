package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.screen.catalog.ProductGrid
import ru.iwater.youwater.screen.home.CatalogName
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.utils.StatusData
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun FavoriteScreen(
    watterViewModel: WatterViewModel = viewModel(),
    navController: NavHostController
) {

    LaunchedEffect(Unit) {
        watterViewModel.getFavoriteProductList()
    }
    val favoriteProductList by watterViewModel.favoriteProductList.observeAsState()
    val modifier = Modifier
    val statusData by watterViewModel.statusData.observeAsState()
    when (statusData) {
        StatusData.LOAD -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        StatusData.DONE -> {
            if (favoriteProductList?.isNotEmpty() == true) {
                Column(modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)) {
                    CatalogName(name = stringResource(id = R.string.general_favorite), modifier = modifier)
                    ProductGrid(
                        modifier = modifier,
                        productsList = favoriteProductList ?: emptyList(),
                        countGrid = 2,
                        getAboutProduct = {
                            navController.navigate(
                                MainNavRoute.AboutProductScreen.withArgs(it.toString())
                            )
                        },
                        addProductInBasket = {watterViewModel.addProductToBasket(it)},
                        onCheckedFavorite = { product, isFavorite ->
                            watterViewModel.onChangeFavorite(productId = product.id, isFavorite)
                        }
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.fragment_my_order_nothing_order),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.fragment_my_order_nothing_order),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}


@Preview
@Composable
fun FavoritePreview() {
    val modifier = Modifier
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
    YourWaterTheme {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(16.dp)) {
            CatalogName(name = stringResource(id = R.string.general_favorite), modifier = modifier)
            ProductGrid(
                modifier = modifier,
                productsList = productsList1,
                countGrid = 2,
                getAboutProduct = {},
                addProductInBasket = {},
                onCheckedFavorite = {_, _ -> run {}}
            )
        }

    }
}