package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.utils.MaskVisualTransformation
import ru.iwater.youwater.utils.NumberDefaults

@Suppress("NAME_SHADOWING")
@Composable
fun EditUserDataScreen(
    clientName: String,
    clientPhone: String,
    clientEmail: String,
    getEditUserPhone: (String) -> String,
    setClientName: (String, String, String) -> Boolean,
    setClientPhone: (String, String, String) -> Boolean,
    setClientEmail: (String, String, String) -> Boolean,
) {
    val modifier = Modifier

    var clientName by rememberSaveable {
        mutableStateOf(clientName)
    }
    var phoneClient by rememberSaveable {
        mutableStateOf(getEditUserPhone(clientPhone))
    }
    var emailClient by rememberSaveable {
        mutableStateOf(clientEmail)
    }


    Column(modifier = modifier.fillMaxSize()) {
        NameEditClient(
            modifier = modifier,
            name = clientName,
            editUserName = {
                clientName = it
                setClientName(it, clientPhone, emailClient)
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
                setClientPhone(it, clientName, emailClient)
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
                setClientEmail(it, clientName, clientPhone)
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
                style = MaterialTheme.typography.bodyMedium
            )},
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_cancel_24), contentDescription = "", tint = Color.Gray, modifier = modifier.clickable { clearName() })
            },
            isError = !isValidateName,
            supportingText = { if (!isValidateName) Text(text = stringResource(id = R.string.support_text_edit_name)) },
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                disabledTrailingIconColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                errorContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
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
                style = MaterialTheme.typography.bodyMedium
            )},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_cancel_24), contentDescription = "", tint = Color.Gray, modifier = modifier.clickable { clearPhone() })
            },
            isError = !isValidatePhone,
            supportingText = { if (!isValidatePhone) Text(text = stringResource(id = R.string.support_text_edit_phone)) },
            visualTransformation = MaskVisualTransformation(NumberDefaults.MASK_EDIT_PHONE),
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                disabledTrailingIconColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                errorContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
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
                style = MaterialTheme.typography.bodyMedium
            )},
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_cancel_24), contentDescription = "", tint = Color.Gray, modifier = modifier.clickable { clearEmail() })
            },
            isError = !isValidateEmail,
            supportingText = {if (!isValidateEmail) Text(text = stringResource(id = R.string.support_text_edit_email))},
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                disabledTrailingIconColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                errorContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
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
            NameEditClient(modifier = modifier, name = name, editUserName = {true}, clearName = {name = ""})

            PhoneEditClient(modifier = modifier, phone = phone, editUserPhone = {true}, clearPhone = {phone = ""})
            EmailEditClient(modifier = modifier, email = email, editUserEmail = {true}, clearEmail = { email = ""})
        }

    }
}