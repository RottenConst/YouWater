package ru.iwater.youwater.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun PriceListScreen(prices: List<String>){
    val modifier = Modifier
    Column(modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.bottom_sheet_price_list_logo),
            style = YouWaterTypography.body1,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        LazyColumn(modifier = modifier.fillMaxWidth().padding(8.dp)) {
            items(prices.size) { itemIndex ->
                val price = prices[itemIndex].split(":")
                Row(
                    modifier = modifier.padding(8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (price.first() == "1") {
                        Text(
                            text = "От одной шт.",
                            style = YouWaterTypography.subtitle1
                        )
                    } else {
                        Text(
                            text = "От ${price.first()} шт.",
                            style = YouWaterTypography.subtitle1
                        )
                    }
                    Text(
                        text = "${price.last()}pyб./шт.",
                        style = YouWaterTypography.subtitle1
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PriceListScreenPreview(){
    YourWaterTheme {
        val modifier = Modifier
        val prices = listOf<String>("1:100", "2:200", "3:300")
        Column(modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.bottom_sheet_price_list_logo),
                style = YouWaterTypography.body1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            LazyColumn(modifier = modifier.fillMaxWidth()) {
                items(prices.size) { itemIndex ->
                    val price = prices[itemIndex].split(":")
                    Row(
                        modifier = modifier.padding(8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (price.first() == "1") {
                            Text(
                                text = "От одной шт.",
                                style = YouWaterTypography.subtitle2
                            )
                        } else {
                            Text(
                                text = "От ${price.first()} шт.",
                                style = YouWaterTypography.subtitle2
                            )
                        }
                        Text(
                            text = "${price.last()}pyб./шт.",
                            style = YouWaterTypography.subtitle2
                        )
                    }
                }
            }
        }
    }
}