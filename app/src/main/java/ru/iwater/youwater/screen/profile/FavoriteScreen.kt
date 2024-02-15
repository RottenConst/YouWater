package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
import ru.iwater.youwater.data.Favorite
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Price
import ru.iwater.youwater.screen.catalog.ProductGrid
import ru.iwater.youwater.screen.home.CatalogName
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.HomeViewModel

@Composable
fun FavoriteScreen(
    favorite: Favorite,
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit,
    watterViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    navController: NavHostController
) {

    LaunchedEffect(Unit) {
        watterViewModel.getFavoriteProductList(favorite)
    }
    val startPocket by watterViewModel.startPocket.observeAsState()
    val favoriteProductList by watterViewModel.productList.observeAsState()
    val modifier = Modifier
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
                    ) },
                addProductInBasket = {
                    addProductInBasket(it, 1, startPocket ?: false)
//                    watterViewModel.addProductToBasket(it)
                                     },
                onCheckedFavorite = { product, isFavorite ->
                    onCheckedFavorite(product, isFavorite)
                }
            )
        }
    } else  {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.fragment_favorite_nothing_text),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview
@Composable
fun FavoritePreview() {
    val modifier = Modifier
    val productsList1: List<NewProduct> = List(100) {
        NewProduct(
            id = it,
            name = "Plesca Натуральная 19л в оборотной таре",
            price = listOf(
                Price(1,355),
                Price(2,325)
            ),
//                "1:355;2:325;4:300;8:280;10:250;20:240;"
            image = "cat-1.png",
            appName = "Plesca Натуральная в оборотной таре",
            category = 7,
            onFavoriteClick = true
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