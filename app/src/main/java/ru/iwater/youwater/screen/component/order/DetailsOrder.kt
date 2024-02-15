package ru.iwater.youwater.screen.component.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.screen.component.product.ImageProduct

@Composable
fun DetailsOrder(highSize: Int, products: List<NewProduct>, minusCount: (NewProduct) -> Unit, addCount: (NewProduct) -> Unit) {
    Text(
        text = stringResource(id = R.string.fragment_create_order_info_order),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(highSize.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        LazyColumn {
            items(products.size) {index ->
                val product = products[index]
                var count by remember {
                    mutableIntStateOf(product.count)
                }
                ItemProductInOrder(
                    productName = product.name,
                    productGallery = product.image,
                    productCount = count,
                    productsPrise = {product.getPriceOnCount(count)},
                    priseNoDiscount = {product.getPriceNoDiscount(count)},
                    minusCount = {
                        if (count > 1) count--
                        minusCount(product)
                    },
                    addCount = {
                        count++
                        addCount(product)
                    }
                )
            }
        }
    }
}

@Composable
fun ItemProductInOrder(
    productName: String,
    productGallery: String,
    productCount: Int,
    productsPrise: (Int) -> Int,
    priseNoDiscount: (Int) -> Int,
    minusCount: () -> Unit,
    addCount: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ImageProduct(image = productGallery)
            Text(
                text = productName,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .width(156.dp)
                    .weight(1f)
            )
            IconButton(onClick = { minusCount() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_btn_minus),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            PriceAndCount(productCount = productCount, productsPrise = productsPrise, priceNoDiscount = priseNoDiscount)
            IconButton(onClick = { addCount() }) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
    }
}

@Composable
fun PriceAndCount(productCount: Int, productsPrise: (Int) -> Int, priceNoDiscount:(Int) -> Int) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (priceNoDiscount(productCount) != productsPrise(productCount)) {
            Text(
                text = "${priceNoDiscount(productCount)}",
                style = MaterialTheme.typography.labelMedium,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Text(
            text = "${productsPrise(productCount)}P",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "$productCount шт.",
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )

    }
}