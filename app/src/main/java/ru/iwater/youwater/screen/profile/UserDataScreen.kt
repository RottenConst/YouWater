package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.iwater.youwater.R
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.Red800
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun UserDataScreen(
    watterViewModel: WatterViewModel = viewModel(),
    mainActivity: MainActivity,
    sendUserData: Boolean
) {
    LaunchedEffect(Unit) {
        watterViewModel.getClientInfo()
    }

    val client by watterViewModel.client.observeAsState()
    val modifier = Modifier

    var isVisibleDialog by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)) {
        NameInfoClient(modifier = modifier, name = client?.name ?: "")
        PhoneInfoClient(modifier = modifier, phone = client?.contact ?: "")
        EmailInfoClient(modifier = modifier, email = client?.email ?: "")
        if (sendUserData) {
            Text(
                text = stringResource(id = R.string.fragment_user_data_user_data_sent_for_moderation),
                style = YouWaterTypography.subtitle2,
                color = Blue500,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
        DeleteAccountDialog(isVisible = isVisibleDialog, setVisible = {isVisibleDialog = !isVisibleDialog}) {
            watterViewModel.deleteAccount(
                clientId = client?.client_id ?: 0,
                phone = client?.contact ?: "",
                mainActivity = mainActivity
            )
        }
        DeleteAccountButton(
            onDelete = {
                isVisibleDialog = true
            }
        )
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

@Composable
fun DeleteAccountButton(onDelete: () -> Unit) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 52.dp, end = 52.dp),
        onClick = { onDelete() }) {
        Text(
            text = "Удалить Аккаунт",
            style = YouWaterTypography.body1,
            color = Color.Red
        )
    }
}

@Composable
fun DeleteAccountDialog(isVisible: Boolean, setVisible: (Boolean) -> Unit, deleteAccount: () -> Unit) {
    if (isVisible) {
        AlertDialog(
            icon = {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "", tint = Red800)
            },
            title = {Text(text = stringResource(id = R.string.delete_account_quest), textAlign = TextAlign.Center)},
            onDismissRequest = { setVisible(false) },
            dismissButton = {
                TextButton(onClick = {
                    setVisible(false)
                }) {
                    Text(text = stringResource(id = R.string.general_no), color = Blue500)
                }
            },confirmButton = {
                TextButton(
                    onClick = {
                        setVisible(false)
                        deleteAccount()
                    }) {
                    Text(text = stringResource(id = R.string.general_yes), color = Blue500)
                }
            })
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
            DeleteAccountButton({})
        }

    }
}