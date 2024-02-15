package ru.iwater.youwater.screen.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.iwater.youwater.data.Category
import ru.iwater.youwater.screen.component.product.CategoryCard
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.HomeViewModel

@Composable
fun CatalogScreen(
    watterViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    navController: NavHostController
) {

    CatalogList(
        catalogList = watterViewModel.catalogList,
        countColumn = 2
    ) {
        navController.navigate(
            MainNavRoute.ProductsByCategoryScreen.withArgs(it.id.toString())
        )
    }
}

@Composable
fun CatalogList(
    catalogList: List<Category>,
    countColumn: Int,
    getProductThisCategory: (Category) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier,
        columns = GridCells.Fixed(countColumn),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(count = catalogList.size) {
            CategoryCard(catalogList[it], getProductThisCategory)
        }
    }
}

@Composable
@Preview
fun CatalogScreenPreview() {
    YourWaterTheme {
        val catalogList: List<Category> = List(100) {
            Category(
                name = "Category $it",
                companyId = 7,
                id = it,
                image = "cat-6.png",
                visibleApp = true,
//                status = true
            )
        }
        CatalogList(catalogList = catalogList, countColumn = 2) {}
    }
}