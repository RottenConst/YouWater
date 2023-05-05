package ru.iwater.youwater.screen.more

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.Green100
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun AboutCompanyScreen() {
    val modifier = Modifier
    AboutCompany(modifier = modifier)
}

@Composable
fun AboutCompany(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_your_water_logo),
            contentDescription = stringResource(id = R.string.description_image_logo),
            contentScale = ContentScale.None,
            modifier = modifier.padding(16.dp)
        )
        Text(
            text = stringResource(id = R.string.about_company),
            textAlign = TextAlign.Center,
            style = YouWaterTypography.body2
        )
        Text(
            text = stringResource(id = R.string.why_choose_us),
            color = Blue500,
            style = YouWaterTypography.body1,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(16.dp)
        )
        AnswerQuestion(
            modifier = modifier,
            image = painterResource(id = R.drawable.ic_heal),
            title = stringResource(id = R.string.heal),
            text = stringResource(id = R.string.heal_text)
        )
        AnswerQuestion(
            modifier = modifier,
            image = painterResource(id = R.drawable.ic_quality),
            title = stringResource(id = R.string.quality),
            text = stringResource(id = R.string.quality_text))
        AnswerQuestion(
            modifier = modifier,
            image = painterResource(id = R.drawable.ic_tasty),
            title = stringResource(id = R.string.taste),
            text = stringResource(id = R.string.taste_text))
        AnswerQuestion(
            modifier = modifier,
            image = painterResource(id = R.drawable.ic_confidence),
            title = stringResource(id = R.string.confidence),
            text = stringResource(id = R.string.confidence_text))
        AnswerQuestion(
            modifier = modifier,
            image = painterResource(id = R.drawable.ic_benefit),
            title = stringResource(id = R.string.benefit),
            text = stringResource(id = R.string.benefit_text))
    }
}

@Composable
fun AnswerQuestion(modifier: Modifier, image: Painter, title: String, text: String) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = image,
            contentDescription = stringResource(id = R.string.heal),
            modifier = modifier.width(36.dp),
            contentScale = ContentScale.FillWidth
        )
        Column(modifier = modifier.padding(4.dp)) {
            Text(
                text = title,
                style = YouWaterTypography.body2,
                color = Green100
            )
            Text(
                text = text,
                style = YouWaterTypography.body2
            )
        }
    }
}

@Preview
@Composable
fun AboutCompanyScreenPreview() {
    YourWaterTheme {
        val modifier = Modifier
        AboutCompany(modifier)
    }
}