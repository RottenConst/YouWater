package ru.iwater.youwater.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
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
            .background(MaterialTheme.colorScheme.onPrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var nameClient by rememberSaveable {
            mutableStateOf("")
        }
        var isValidName by rememberSaveable {
            mutableStateOf(true)
        }
        var emailClient by rememberSaveable {
            mutableStateOf("")
        }
        var isValidEmail by rememberSaveable {
            mutableStateOf(true)
        }
        var checkMessage by rememberSaveable {
            mutableStateOf(true)
        }
        LoginTitle(title = stringResource(id = R.string.fragment_register_register_text))
        RegisterNameField(
            nameClient = nameClient,
            isValidateName = isValidName
        ) {
            nameClient = it
            isValidName = authViewModel.isValidName(it)
        }
        RegisterEmailClient(
            emailClient = emailClient,
            isValidateEmail = isValidEmail,
            setEmailClient = {
                emailClient = it
                isValidEmail = authViewModel.isValidEmail(it)
            })
        ButtonRegisterClient(isEnabledButton = (nameClient.isNotEmpty() && emailClient.isNotEmpty() && isValidName && isValidEmail)) {
            authViewModel.viewModelScope.launch {
                authViewModel.registerClient(phone, nameClient, emailClient, checkMessage, navController)
            }
        }
        CheckMessage(
            message = stringResource(id = R.string.distributionAnswer),
            check = checkMessage,
            setChecked = {
                checkMessage = !it
            }
        )
    }
}

@Composable
fun RegisterNameField(nameClient: String, isValidateName: Boolean, setNameClient: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        value = nameClient,
        onValueChange =  { name ->
            setNameClient(name)
        },
        label = { Text(text = stringResource(id = R.string.fragment_register_name_lastname))},
        isError = !isValidateName,
        supportingText = { if (!isValidateName) Text(text = stringResource(id = R.string.support_text_edit_name)) },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.onPrimary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun RegisterEmailClient(emailClient: String, isValidateEmail: Boolean, setEmailClient: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        value = emailClient,
        onValueChange = { email ->
            setEmailClient(email)
        },
        label = { Text(text = stringResource(id = R.string.general_email))},
        isError = !isValidateEmail,
        supportingText = { if (!isValidateEmail) Text(text = stringResource(id = R.string.support_text_edit_email)) },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            errorContainerColor = MaterialTheme.colorScheme.onPrimary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
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
//        colors = ButtonDefaults.buttonColors(containerColor = ),
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
            checked = !check,
            onCheckedChange = {setChecked(it)},
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
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
    var isValidName by remember {
        mutableStateOf(false)
    }
    var isValidEmail by remember {
        mutableStateOf(false)
    }
    var check by rememberSaveable {
        mutableStateOf(true)
    }
    YourWaterTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginTitle(title = stringResource(id = R.string.fragment_register_register_text))
            RegisterNameField(
                nameClient = nameClient,
                isValidateName = isValidName
            ) {
                nameClient = it
                isValidName = it.contains(Regex("""[^A-zА-я\s]"""))
            }
            RegisterEmailClient(
                emailClient = emailClient,
                isValidateEmail = isValidEmail,
                setEmailClient = {
                    emailClient = it
                    isValidEmail = it.contains(Regex("""(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})"""))
                })
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