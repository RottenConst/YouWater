package ru.iwater.youwater.screen.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun FaqScreen() {
    QuestOne(modifier = Modifier)
}

@Composable
fun QuestOne(modifier: Modifier) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxSize().verticalScroll(state = scrollState, enabled = true)) {
        Text(
            text = stringResource(id = R.string.fragment_faq_one_quest),
            style = YouWaterTypography.h6,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(id = R.string.fragment_faq_one_quest_answer_1_1),
            style = YouWaterTypography.body1,
            modifier = modifier.padding(8.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_faq_one_quest_answer_1_2),
            style = YouWaterTypography.body1,
            modifier = modifier.padding(8.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_faq_one_quest_answer_1_3),
            style = YouWaterTypography.body1,
            modifier = modifier.padding(8.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_faq_one_quest_answer_1_4),
            style = YouWaterTypography.body1,
            modifier = modifier.padding(8.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_faq_one_quest_answer_1_5),
            style = YouWaterTypography.body1,
            modifier = modifier.padding(8.dp)
        )

    }
}

@Preview
@Composable
fun FaqScreenPreview() {
    YourWaterTheme {
        QuestOne(modifier = Modifier)
    }
}