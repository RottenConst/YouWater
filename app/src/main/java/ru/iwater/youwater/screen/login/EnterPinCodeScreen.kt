package ru.iwater.youwater.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun EnterPincodeScreen(phone: String, clientId: Int, activityFragment: FragmentActivity?, viewModel: AuthViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val isEnabledEnter by viewModel.isFullPinCode.observeAsState()
        val pinCode by viewModel.pinCode.observeAsState()
        LoginTitle(title = stringResource(id = R.string.fragment_enter_pin_code_enter_text))
        EnterPinCodeText()
        OtpTextFieldTheme(Blue500, 4, viewModel)
        ButtonEnter({ checkPinCode(viewModel, getPicode(pinCode), clientId, activityFragment) }, isFullPinCode(isEnabledEnter))
        ThisPhoneNumber(phone = phone)
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
private fun ThisPhoneNumber(phone: String) {
    Text(modifier = Modifier
        .padding(top = 16.dp, start = 52.dp, end = 52.dp),
        textAlign = TextAlign.Center,
        text = "Код отправлен на номер:\n $phone"
    )
}

@Composable
fun ButtonEnter(checkPinCode: () -> Unit, isEnabled: Boolean) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 52.dp, start = 52.dp, end = 52.dp),
        enabled = isEnabled,
        onClick = { checkPinCode }) {
        Text(text = stringResource(id = R.string.fragment_enter_pin_code_enter_text))
    }
}

@Composable
fun OtpTextFieldTheme(color: Color, maxLength: Int, viewModel: AuthViewModel) {
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
            modifier = Modifier.focusTarget(),
            onValueChange = {
                when {
                    it.length <= maxLength -> {
                        otpValue = it
                    }
                }
                if (it.length == maxLength) {
                    viewModel.setFullPinCode(true, it)
                }
                            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(4) {index ->
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(16.dp)
                                .border(1.dp, color, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    if (index < otpValue.length) color
                                    else Color.Transparent
                                )
                                .padding(2.dp),
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        )
    }
}

private fun isFullPinCode(isFull: Boolean?) = isFull ?: false
private fun getPicode(pinCode: String?) = pinCode ?: ""
private fun checkPinCode(viewModel: AuthViewModel, pinCode: String, clientId: Int, fragmentActivity: FragmentActivity?) {
    viewModel.viewModelScope.launch {
        viewModel.checkPin(fragmentActivity, pinCode, clientId)
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
//            OtpTextFieldTheme(Blue500, 4)
//            ButtonEnter()
            ThisPhoneNumber(phone = "+7(123) 456-7890")
        }
    }
}