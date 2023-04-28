package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.iwater.youwater.R
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun EditUserDataScreen(clientProfileViewModel: ClientProfileViewModel = viewModel(), name: String, phone: String, email: String) {
    val modifier = Modifier
    var clientName by rememberSaveable {
        mutableStateOf(name)
    }
    var phoneClient by rememberSaveable {
        mutableStateOf(phone)
    }
    var emailClient by rememberSaveable {
        mutableStateOf(email)
    }
    Column(modifier = modifier.fillMaxSize()) {
        NameEditClient(
            modifier = modifier,
            name = clientName,
            editUserName = {
                clientName = it
                clientProfileViewModel.setEditClientData(
                    clientName = clientName,
                    clientPhone = phoneClient,
                    clientEmail = emailClient
                )
            },
            clearName = {
                clientName = ""
            }
        )
        PhoneEditClient(
            modifier = modifier,
            phone = phoneClient,
            editUserPhone = {
                phoneClient = it
                clientProfileViewModel.setEditClientData(
                    clientName = clientName,
                    clientPhone = it,
                    clientEmail = emailClient
                )
            },
            clearPhone = {
                phoneClient = ""
            }
        )
        EmailEditClient(
            modifier = modifier,
            email = emailClient,
            editUserEmail = {
                emailClient = it
                clientProfileViewModel.setEditClientData(
                    clientName = clientName,
                    clientPhone = phoneClient,
                    clientEmail = it
                )
            },
            clearEmail = {
                emailClient = ""
            }
        )
    }
}

@Composable
fun NameEditClient(modifier: Modifier, name: String, editUserName: (String) -> Unit, clearName: () -> Unit) {
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        TextField(
            value = name,
            onValueChange = { editUserName(it) },
            label = { Text(
                text = stringResource(id = R.string.fragment_user_data_name_user),
                style = YouWaterTypography.body2
            )},
            singleLine = true,
            textStyle = YouWaterTypography.body1,
            modifier = modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_cancel_24), contentDescription = "", tint = Color.Gray, modifier = modifier.clickable { clearName() })
            },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White, disabledTrailingIconColor = Color.Gray, trailingIconColor = Blue500)
        )
    }

}

@Composable
fun PhoneEditClient(modifier: Modifier, phone: String, editUserPhone: (String) -> Unit, clearPhone: () -> Unit) {
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        TextField(
            value = phone,
            onValueChange = {number ->
                editUserPhone(number) },
            label = { Text(
                text = stringResource(id = R.string.fragment_user_data_phone),
                style = YouWaterTypography.body2
            )},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle = YouWaterTypography.body1,
            modifier = modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_cancel_24), contentDescription = "", tint = Color.Gray, modifier = modifier.clickable { clearPhone() })
            },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White, disabledTrailingIconColor = Color.Gray, trailingIconColor = Blue500)
        )
    }
}

@Composable
fun EmailEditClient(modifier: Modifier, email: String, editUserEmail: (String) -> Unit, clearEmail: () -> Unit) {
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        TextField(
            value = email,
            onValueChange = { editUserEmail(it) },
            label = { Text(
                text = stringResource(id = R.string.fragment_user_data_email),
                style = YouWaterTypography.body2
            )},
            singleLine = true,
            textStyle = YouWaterTypography.body1,
            modifier = modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_cancel_24), contentDescription = "", tint = Color.Gray, modifier = modifier.clickable { clearEmail() })
            },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White, disabledTrailingIconColor = Color.Gray, trailingIconColor = Blue500)
        )
    }
}

@Preview
@Composable
fun EditUserDataPreview() {
    val modifier = Modifier
    var name by remember {
        mutableStateOf("Name")
    }
    var phone by remember {
        mutableStateOf("+12312312")
    }
    var email by remember {
        mutableStateOf("mail@mail.ru")
    }
    YourWaterTheme {
        Column(modifier = modifier.fillMaxSize()) {
            NameEditClient(modifier = modifier, name = name, editUserName = {name = it}, clearName = {name = ""})

            PhoneEditClient(modifier = modifier, phone = phone, editUserPhone = {phone = it}, clearPhone = {phone = ""})
            EmailEditClient(modifier = modifier, email = email, editUserEmail = {email = it}, clearEmail = { email = ""})
        }

    }
}