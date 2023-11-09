package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.utils.MaskVisualTransformation
import ru.iwater.youwater.utils.NumberDefaults
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun EditUserDataScreen(
    watterViewModel: WatterViewModel = viewModel(),
) {

    LaunchedEffect(Unit) {
        watterViewModel.getClientInfo()
    }



    val modifier = Modifier
    val client by watterViewModel.client.observeAsState()
    var clientName by rememberSaveable {
        mutableStateOf(client?.name)
    }
    var phoneClient by rememberSaveable {
        mutableStateOf(watterViewModel.getNumberFromPhone(client?.contact ?: ""))
    }
    var emailClient by rememberSaveable {
        mutableStateOf(client?.email)
    }
    Column(modifier = modifier.fillMaxSize()) {
        NameEditClient(
            modifier = modifier,
            name = clientName ?: "",
            editUserName = {
                clientName = it
                watterViewModel.setEditClientName(
                    clientName = it,
                    clientPhone = phoneClient,
                    clientEmail = emailClient ?: ""
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
                watterViewModel.setEditClientPhone(
                    clientPhone = it,
                    clientName = clientName ?: "",
                    clientEmail = emailClient ?: ""
                )
            },
            clearPhone = {
                phoneClient = ""
            }
        )
        EmailEditClient(
            modifier = modifier,
            email = emailClient ?: "",
            editUserEmail = {
                emailClient = it
                watterViewModel.setEditClientEmail(
                    clientEmail = it,
                    clientName = clientName ?: "",
                    clientPhone = phoneClient
                )
            },
            clearEmail = {
                emailClient = ""
            }
        )
    }
}

@Composable
fun NameEditClient(modifier: Modifier, name: String, editUserName: (String) -> Boolean, clearName: () -> Unit) {
    var isValidateName by rememberSaveable {
        mutableStateOf(true)
    }
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        TextField(
            value = name,
            onValueChange = { isValidateName = editUserName(it) },
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
            isError = !isValidateName,
            supportingText = { if (!isValidateName) Text(text = stringResource(id = R.string.support_text_edit_name)) },
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = Blue500,
                focusedIndicatorColor = Blue500,
                disabledTrailingIconColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedLabelColor = Blue500,
                cursorColor = Blue500
            )
        )
    }

}

@Composable
fun PhoneEditClient(modifier: Modifier, phone: String, editUserPhone: (String) -> Boolean, clearPhone: () -> Unit) {
    var isValidatePhone by rememberSaveable {
        mutableStateOf(true)
    }
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        TextField(
            value = phone,
            onValueChange = {number ->
                if (number.length <= 11) {
                    isValidatePhone = editUserPhone(number)
                } },
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
            isError = !isValidatePhone,
            supportingText = { if (!isValidatePhone) Text(text = stringResource(id = R.string.support_text_edit_phone)) },
            visualTransformation = MaskVisualTransformation(NumberDefaults.MASK_EDIT_PHONE),
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = Blue500,
                focusedIndicatorColor = Blue500,
                disabledTrailingIconColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedLabelColor = Blue500,
                cursorColor = Blue500
            )
        )
    }
}

@Composable
fun EmailEditClient(modifier: Modifier, email: String, editUserEmail: (String) -> Boolean, clearEmail: () -> Unit) {
    var isValidateEmail by rememberSaveable {
        mutableStateOf(true)
    }
    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        TextField(
            value = email,
            onValueChange = {
                isValidateEmail = editUserEmail(it)
                            },
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
            isError = !isValidateEmail,
            supportingText = {if (!isValidateEmail) Text(text = stringResource(id = R.string.support_text_edit_email))},
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = Blue500,
                focusedIndicatorColor = Blue500,
                disabledTrailingIconColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedLabelColor = Blue500,
                cursorColor = Blue500
            )
//            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White, disabledTrailingIconColor = Color.Gray, trailingIconColor = Blue500)
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
//            NameEditClient(modifier = modifier, name = name, editUserName = {name = it}, clearName = {name = ""})

//            PhoneEditClient(modifier = modifier, phone = phone, editUserPhone = {phone = it}, clearPhone = {phone = ""})
//            EmailEditClient(modifier = modifier, email = email, editUserEmail = {email = it}, clearEmail = { email = ""})
        }

    }
}