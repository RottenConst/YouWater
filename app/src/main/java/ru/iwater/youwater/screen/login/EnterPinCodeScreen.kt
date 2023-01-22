package ru.iwater.youwater.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun EnterPincodeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginTitle(title = stringResource(id = R.string.fragment_enter_pin_code_enter_text))
        EnterPinCodeText()
        OtpTextFieldTheme(Blue500, 4)
        ButtonEnter()
    }
}

@Composable
private fun EnterPinCodeText() {
    Text(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        textAlign = TextAlign.Center,
        text = stringResource(id = R.string.fragment_enter_pin_code_enter_code_on_sms)
    )
}

@Composable
fun ButtonEnter() {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 52.dp, start = 52.dp, end = 52.dp),
        onClick = { /*TODO*/ }) {
        Text(text = stringResource(id = R.string.fragment_enter_pin_code_enter_text))
    }
}

@Composable
fun OtpTextFieldTheme(color: Color, maxLength: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp),
        color = Color.White
    ) {
        var otpValue by remember {
            mutableStateOf("")
        }

        BasicTextField(
            value = otpValue,
            onValueChange = {
                if (it.length <= maxLength) {
                    otpValue = it
                } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(4) {index ->
                        Box(
                            modifier = Modifier
                                .width(26.dp)
                                .height(26.dp)
                                .border(1.dp, color, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    if (index < otpValue.length) color
                                    else Color.Transparent
                                )
                                .padding(2.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        )
    }
}


@Preview
@Composable
private fun EnterPinCodeScreenPreview() {
    YourWaterTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginTitle(title = stringResource(id = R.string.fragment_enter_pin_code_enter_text))
            EnterPinCodeText()
            OtpTextFieldTheme(Blue500, 4)
            ButtonEnter()
        }
    }
}