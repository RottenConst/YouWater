package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.Red800
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun UserDataScreen(
    clientName: String,
    clientPhone: String,
    clientEmail: String,
    deleteAccount: () -> Unit,
    sendUserData: Boolean
) {
    val modifier = Modifier

    var isVisibleDialog by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)) {
        NameInfoClient(modifier = modifier, name = clientName)
        PhoneInfoClient(modifier = modifier, phone = clientPhone)
        EmailInfoClient(modifier = modifier, email = clientEmail)
        if (sendUserData) {
            Text(
                text = stringResource(id = R.string.fragment_user_data_user_data_sent_for_moderation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
        DeleteAccountDialog(isVisible = isVisibleDialog, setVisible = {isVisibleDialog = !isVisibleDialog}) {
            deleteAccount()
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
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
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
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = lastName,
            style = MaterialTheme.typography.bodyLarge,
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
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = phone,
            style = MaterialTheme.typography.bodyLarge,
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
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
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
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
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
            DeleteAccountButton {}
        }

    }
}