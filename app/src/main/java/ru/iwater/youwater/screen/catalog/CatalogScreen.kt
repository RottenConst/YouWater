package ru.iwater.youwater.screen.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import ru.iwater.youwater.R
import ru.iwater.youwater.data.TypeProduct
import ru.iwater.youwater.network.ImageUrl
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.CatalogListViewModel

@Composable
fun CatalogScreen(
    viewModel: CatalogListViewModel,
    navController: NavHostController) {

    CatalogList(
        catalogList = viewModel.catalogList.sortedBy { it.priority },
        countColumn = 2
    ) {
        navController.navigate(
            CatalogFragmentDirections.actionShowTypeCatalog(it.id, it.category)
        )
    }
}

@Composable
fun CategoryCard(catalog: TypeProduct, getProductThisCategory: (TypeProduct) -> Unit) {
    Surface(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { getProductThisCategory(catalog) },
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlideImage(
                modifier = Modifier.height(96.dp),
                imageModel = { "$ImageUrl/${catalog.image}" },
                loading = {
                    Box(modifier = Modifier.matchParentSize()) {
                        CircularProgressIndicator(
                            Modifier.align(Alignment.Center)
                        )
                    }
                },
                failure = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_your_water_logo),
                        contentDescription = stringResource(id = R.string.description_image_product),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop
                    )
                },
                previewPlaceholder = R.drawable.ic_your_water_logo,
                imageOptions = ImageOptions(
                    alignment = Alignment.Center,
                    contentDescription = stringResource(id = R.string.description_image_product),
                    contentScale = ContentScale.Inside
                )
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = catalog.category,
                textAlign = TextAlign.Center,
                style = YouWaterTypography.body1,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CatalogList(
    catalogList: List<TypeProduct>,
    countColumn: Int,
    getProductThisCategory: (TypeProduct) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(bottom = 60.dp),
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
        val catalogList: List<TypeProduct> = List(100) {
            TypeProduct(
                "Category $it",
                company_id = "007",
                id = it,
                image = "cat-6.png",
                priority = 1,
                visible_app = 1
            )
        }
        CatalogList(catalogList = catalogList, countColumn = 2) {}
    }
}