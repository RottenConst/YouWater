package ru.iwater.youwater.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.R
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.theme.YouWaterTypography

@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        LoginTitle(stringResource(id = R.string.login_fragment_enter_text))

        InputTelNumberField(stringResource(id = R.string.login_fragment_sent_code), authViewModel, navController)
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
fun InputTelNumberField(textButton: String, authViewModel: AuthViewModel, navController: NavController) {
    var telNum by remember { mutableStateOf(TextFieldValue("")) }
    var buttonEnabled by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = telNum,
            onValueChange = {number ->
                if (number.text.length <= 16) {
                    telNum = if (number.text.length == 1) {
                        TextFieldValue("+7(${number.text}", TextRange(4))
                    }
                    else if (telNum.text.length == 5) {
                        TextFieldValue("${number.text}) ", TextRange(8))
                    }
                    else if (telNum.text.length == 10) {
                        TextFieldValue("${number.text}-", TextRange(12))
                    }
                    else number
                }
                buttonEnabled = telNum.text.length == 16
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = { Text(text = stringResource(id = R.string.login_fragment_tel_number))},
            maxLines = 1
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 52.dp,
                    end = 52.dp,
                    top = 52.dp
                ),
            shape = RoundedCornerShape(8.dp),
            enabled = buttonEnabled,
            onClick = {
                authViewModel.authPhone(telNum.text, navController)
            }
        ) {
            Text(
                style = YouWaterTypography.h6,
                text = textButton
            )
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            textAlign = TextAlign.Center,
            style = YouWaterTypography.body2,
            text = stringResource(id = R.string.login_fragment_login_accept)
        )
    }
}

@Preview
@Composable
fun TextInputPreview() {
    YourWaterTheme {
    }
}

@Preview
@Composable
fun LoginTitlePreview() {
    YourWaterTheme {
        LoginTitle("Enter")
    }
}

@Preview
@Composable
fun InputTelNumPreview() {
    YourWaterTheme {
    }
}