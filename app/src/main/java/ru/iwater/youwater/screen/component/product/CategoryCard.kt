package ru.iwater.youwater.screen.component.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import okhttp3.OkHttpClient
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Category
import ru.iwater.youwater.network.ImageUrl

@Composable
fun CategoryCard(catalog: Category, getProductThisCategory: (Category) -> Unit) {
    Card(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { getProductThisCategory(catalog) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            GlideImage(
//                modifier = Modifier.height(96.dp),
//                imageModel = { "$ImageUrl/${catalog.image}" },
//                loading = {
//                    Box(modifier = Modifier.matchParentSize()) {
//                        CircularProgressIndicator(
//                            Modifier.align(Alignment.Center)
//                        )
//                    }
//                },
//                failure = {
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_your_water_logo),
//                        contentDescription = stringResource(id = R.string.description_image_product),
//                        alignment = Alignment.Center,
//                        contentScale = ContentScale.Crop
//                    )
//                },
//                previewPlaceholder = R.drawable.ic_your_water_logo,
//                imageOptions = ImageOptions(
//                    alignment = Alignment.Center,
//                    contentDescription = stringResource(id = R.string.description_image_product),
//                    contentScale = ContentScale.Inside
//                )
//            )

            val painter = rememberAsyncImagePainter(
                model = "$ImageUrl/${catalog.image}",
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
                androidx.compose.material3.CircularProgressIndicator()
            }
            Image(
                painter = painter,
                contentDescription = stringResource(id = R.string.description_image_product),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = catalog.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}