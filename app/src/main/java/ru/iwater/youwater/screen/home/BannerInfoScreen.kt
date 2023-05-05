package ru.iwater.youwater.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun BannerinfoScreen(namePromo: String, promoDescription: String){
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = namePromo,
            style = YouWaterTypography.body1,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = promoDescription,
            style = YouWaterTypography.body2,
            modifier = Modifier.fillMaxWidth().padding(8.dp).verticalScroll(state = scrollState, enabled = true
            )
        )
    }
}

@Preview
@Composable
fun BannerInfoScreenPreview() {
    YourWaterTheme {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Action",
                style = YouWaterTypography.body1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Info",
                style = YouWaterTypography.body2,
                modifier = Modifier.fillMaxWidth().padding(8.dp).verticalScroll(state = scrollState, enabled = true)
            )
        }
    }
}