package ru.iwater.youwater.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.vm.AuthViewModel
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.utils.MaskVisualTransformation
import ru.iwater.youwater.utils.NumberDefaults

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        var phone by rememberSaveable {
            mutableStateOf("")
        }
        var isEnabledButton by rememberSaveable {
            mutableStateOf(false)
        }
        LoginTitle(stringResource(id = R.string.login_fragment_enter_text))
        InputTelNumberField(
            phone = phone,
            setPhone = {
                phone = it
                authViewModel.isValidatePhone(it) },
            isFullPhoneNumber = {isEnabledButton = it}
        )
        ButtonEnter(text = stringResource(id = R.string.login_fragment_sent_code), isEnabledButton = isEnabledButton) {
            authViewModel.authPhone(
                phone = phone,
                navController = navController
            )
        }
        DescriptionText(
            text = stringResource(
                id = R.string.login_fragment_login_accept
            )
        )
    }
}

@Composable
fun LoginTitle(title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(top = 54.dp),
            painter = painterResource(id = R.drawable.ic_your_water_logo),
            contentDescription = stringResource(id = R.string.description_image_logo)
        )
        Text(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 24.dp,
                bottom = 16.dp
            ),
            text = title,
            style = YouWaterTypography.h4,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InputTelNumberField(phone: String, setPhone: (String) -> Boolean, isFullPhoneNumber: (Boolean) -> Unit) {
    var isValidatePhone by rememberSaveable {
        mutableStateOf(true)
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        value = phone,
        onValueChange = {number ->
            if (number.length <= NumberDefaults.INPUT_LENGTH) {
                isValidatePhone = setPhone(number)
                isFullPhoneNumber(number.length == NumberDefaults.INPUT_LENGTH && isValidatePhone)
            }
        },
        isError = !isValidatePhone,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        label = { Text(text = stringResource(id = R.string.login_fragment_tel_number))},
        visualTransformation =
        MaskVisualTransformation(NumberDefaults.MASK_LOGIN_PHONE),
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Blue500,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            focusedLabelColor = Blue500,
            cursorColor = Blue500
        )
    )
}
@Composable
fun DescriptionText(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
        textAlign = TextAlign.Center,
        style = YouWaterTypography.body2,
        text = text
    )
}

@Preview
@Composable
fun InputTelNumPreview() {
    YourWaterTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            var phone by rememberSaveable {
                mutableStateOf("")
            }
            var isEnabledButton by rememberSaveable {
                mutableStateOf(false)
            }
            LoginTitle(title = stringResource(id = R.string.login_fragment_enter_text))
            InputTelNumberField(
                phone = phone,
                setPhone = {
                    phone = it
                    it.contains(Regex("""7\d{10}"""))
                           },
                isFullPhoneNumber = {
                    isEnabledButton = it
                })
            ButtonEnter(
                stringResource(id = R.string.login_fragment_sent_code),
                onEvent = {},
                isEnabledButton = isEnabledButton
            )
            DescriptionText(
                text = stringResource(
                    id = R.string.login_fragment_login_accept
                )
            )
        }

    }
}