package ru.iwater.youwater.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.vm.AuthViewModel
import ru.iwater.youwater.theme.YourWaterTheme
import timber.log.Timber

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    phone: String,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var nameClient by rememberSaveable {
            mutableStateOf("")
        }
        var emailClient by rememberSaveable {
            mutableStateOf("")
        }
        var checkMessage by rememberSaveable {
            mutableStateOf(true)
        }
        LoginTitle(title = stringResource(id = R.string.fragment_register_register_text))
        RegisterNameField(nameClient = nameClient, setNameClient = {nameClient = it})
        RegisterEmailClient(emailClient = emailClient, setEmailClient = {emailClient = it})
        ButtonRegisterClient(isEnabledButton = (nameClient.isNotEmpty() && emailClient.isNotEmpty())) {
            authViewModel.viewModelScope.launch {
                authViewModel.registerClient(phone, nameClient, emailClient, checkMessage, navController)
            }
        }
        CheckMessage(
            message = stringResource(id = R.string.distributionAnswer),
            check = checkMessage,
            setChecked = {
                checkMessage = it

            }
        )
    }
}

@Composable
fun RegisterNameField(nameClient: String, setNameClient: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        value = nameClient,
        onValueChange =  { name ->
            setNameClient(name)
        },
        label = { Text(text = stringResource(id = R.string.fragment_register_name_lastname))}
    )
}

@Composable
fun RegisterEmailClient(emailClient: String, setEmailClient: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        value = emailClient,
        onValueChange = { email ->
            setEmailClient(email)
        },
        label = { Text(text = stringResource(id = R.string.general_email))}
    )
}

@Composable
fun ButtonRegisterClient(isEnabledButton: Boolean, registerClient: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 52.dp,
                end = 52.dp,
                top = 24.dp
            ),
        shape = RoundedCornerShape(8.dp),
        enabled = isEnabledButton,
        onClick = {
            registerClient()
        }) {
        Text(text = stringResource(id = R.string.fragment_register_finish))
    }
}

@Composable
fun CheckMessage(message: String, check: Boolean, setChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 52.dp, top = 8.dp, end = 52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = check,
            onCheckedChange = {setChecked(it)})
        Text(
            text = message,
            style = YouWaterTypography.body2,
            textAlign = TextAlign.Start
        )
    }

}

@Preview
@Composable
private fun InputRegisterDataPreview() {
    var nameClient by rememberSaveable {
        mutableStateOf("")
    }
    var emailClient by rememberSaveable {
        mutableStateOf("")
    }
    var check by rememberSaveable {
        mutableStateOf(true)
    }
    YourWaterTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginTitle(title = stringResource(id = R.string.fragment_register_register_text))
            RegisterNameField(nameClient = nameClient, setNameClient = {nameClient = it})
            RegisterEmailClient(emailClient = emailClient, setEmailClient = {emailClient = it})
            ButtonEnter(
                text = stringResource(id = R.string.fragment_register_finish),
                isEnabledButton = (nameClient.isNotEmpty() && emailClient.isNotEmpty())
            ) { /*TODO*/ }
            CheckMessage(message = stringResource(id = R.string.distributionAnswer), check = check) {
                check = it
                Timber.d("CHECK MESSAGE = $check")
            }
        }
    }

}