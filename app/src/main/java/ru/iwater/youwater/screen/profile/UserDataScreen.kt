package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.iwater.youwater.R
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun UserDataScreen(clientProfileViewModel: ClientProfileViewModel = viewModel(), sendUserData: Boolean) {
    val client by clientProfileViewModel.client.observeAsState()
    val modifier = Modifier
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        NameInfoClient(modifier = modifier, name = client?.name ?: "")
        PhoneInfoClient(modifier = modifier, phone = client?.contact ?: "")
        EmailInfoClient(modifier = modifier, email = client?.email ?: "")
        if (sendUserData) {
            Text(
                text = stringResource(id = R.string.fragment_user_data_user_data_sent_for_moderation),
                style = YouWaterTypography.subtitle2,
                color = Blue500,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Composable
fun NameInfoClient (modifier: Modifier, name: String) {
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()){
        Text(
            text = stringResource(id = R.string.fragment_user_data_name_user),
            style = YouWaterTypography.body2
        )
        Text(
            text = name,
            style = YouWaterTypography.body1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LastNameInfoClient(modifier: Modifier, lastName: String) {
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.fragment_user_data_last_name),
            style = YouWaterTypography.body2
        )
        Text(
            text = lastName,
            style = YouWaterTypography.body1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PhoneInfoClient(modifier: Modifier, phone: String) {
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.fragment_user_data_phone),
            style = YouWaterTypography.body2
        )
        Text(
            text = phone,
            style = YouWaterTypography.body1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmailInfoClient(modifier: Modifier, email: String) {
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.fragment_user_data_email),
            style = YouWaterTypography.body2
        )
        Text(
            text = email,
            style = YouWaterTypography.body1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun UserDataPreview() {
    YourWaterTheme {
        val modifier = Modifier
        Column(modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)) {
            NameInfoClient(modifier = modifier, name = "Name")
            LastNameInfoClient(modifier = modifier, lastName = "Lastname")
            PhoneInfoClient(modifier = modifier, phone = "+123123123")
            EmailInfoClient(modifier = modifier, email = "mail@mail.com")
        }

    }
}