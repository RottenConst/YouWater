package ru.iwater.youwater.screen.more

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeliveryInfoScreen() {
    val tabData = listOf(
        "В этот же день",
        "На следующий день",
        "В пригороды"
    )
    val pagerState = rememberPagerState(pageCount = {3})

    Column(modifier = Modifier.fillMaxSize()) {
        TabLayout(tabData = tabData, pagerState = pagerState)
        TabContent(tabData = tabData, pagerState = pagerState)
    }
}

@Composable
fun DeliveryInfoTwoHour(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_delivery_two_hour),
            contentDescription = "",
            modifier = modifier
                .padding(16.dp)
                .size(85.dp, 60.dp),
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_two_hour_logo),
            style = YouWaterTypography.h6,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(4.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_two_hour_description_text),
            style = YouWaterTypography.body1,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(4.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_two_hour_time_interval_text),
            style = YouWaterTypography.body1,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_two_hour_time_interval_delivery),
            style = YouWaterTypography.body1,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_two_hour_delivery_sat_two_hour),
            style = YouWaterTypography.body1,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
    }
}


@Composable
fun DeliveryInfoStd(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_delivery_std),
            contentDescription = "",
            modifier = modifier
                .padding(16.dp)
                .size(85.dp, 60.dp),
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_std_delivery_logo_stg),
            style = YouWaterTypography.h6,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(4.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_std_delivery_text_std ),
            style = YouWaterTypography.body1,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(4.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_std_time_delivery),
            style = YouWaterTypography.body1,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_std_delivery_std_1_4),
            style = YouWaterTypography.body1,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = stringResource(id = R.string.fragment_delivery_std_delivery_std_5),
            style = YouWaterTypography.body1,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun DeliveryInfoSuburbs(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_text_logo),
                style = YouWaterTypography.h6,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(8.dp)
            )
        }
        val scrollState = rememberScrollState()
        Column(
            modifier
                .fillMaxWidth()
                .verticalScroll(scrollState, true)) {
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_pushkin),
                style = YouWaterTypography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_pushkin_text),
                style = YouWaterTypography.body2,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_kolino),
                style = YouWaterTypography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_kolino_text),
                style = YouWaterTypography.body2,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_krondshtat),
                style = YouWaterTypography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_krondshtat_text),
                style = YouWaterTypography.body2,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_metalstroy),
                style = YouWaterTypography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_metalstroy_text),
                style = YouWaterTypography.body2,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_krasnoe_selo),
                style = YouWaterTypography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_krasnoe_selo_text),
                style = YouWaterTypography.body2,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_shushari),
                style = YouWaterTypography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_shushari_text),
                style = YouWaterTypography.body2,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_petergof),
                style = YouWaterTypography.body1,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.fragment_delivery_suburbs_petergof_text),
                style = YouWaterTypography.body2,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(tabData: List<String>, pagerState: PagerState){
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = {
            Spacer(modifier =Modifier.height(4.dp))
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier =
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Blue500
            )
        },
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        tabData.forEachIndexed { index, s ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(text = s)
                },
                selectedContentColor = Blue500,
                unselectedContentColor = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabContent(tabData: List<String>, pagerState: PagerState){
    HorizontalPager(state = pagerState) { index ->
        when(index) {
            0 -> {
                DeliveryInfoTwoHour(modifier = Modifier)
            }
            1 -> {
                DeliveryInfoStd(modifier = Modifier)
            }
            2 -> DeliveryInfoSuburbs(modifier = Modifier)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun DeliveryInfoPreview() {
    YourWaterTheme {

    }
}

@Preview
@Composable
fun DeliveryInfoSuburbsPreview() {
    YourWaterTheme {
        DeliveryInfoSuburbs(modifier = Modifier)
    }
}

@Preview
@Composable
fun DeliveryInfoStdPreview() {
    YourWaterTheme {
        DeliveryInfoStd(modifier = Modifier)
    }
}

@Preview
@Composable
fun DeliveryInfoTwoHourPreview() {
    YourWaterTheme {
        DeliveryInfoTwoHour(modifier = Modifier)
    }
}