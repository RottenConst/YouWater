package ru.iwater.youwater.screen.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.vm.AuthViewModel
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.utils.StatusPinCode.*
import timber.log.Timber

@Composable
fun EnterPinCodeScreen(
    phone: String,
    clientId: Int,
    context: Context?,
    authViewModel: AuthViewModel,
    startActivity: StartActivity
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val infoPinCode = stringResource(id = R.string.fragment_enter_pin_code_info_code)
        var isCheckPinCode by rememberSaveable { mutableStateOf(false) }
        var pinCode by rememberSaveable{ mutableStateOf("") }
        val statusPinCode by authViewModel.statusPinCode.observeAsState()
        when (statusPinCode) {
            DONE -> {
                Timber.d("DONE")
                startActivity.finish()
                MainActivity.start(context)
            }
            ERROR -> {
                Timber.d("Error")
                Toast.makeText(context, "Неверный пин код", Toast.LENGTH_LONG).show()
            }
            NET_ERROR -> {
                Timber.d("Net Error")
                Toast.makeText(context, "Ошибка соединения", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }

        LoginTitle(title = stringResource(id = R.string.fragment_enter_pin_code_enter_text))
        EnterPinCodeText()
        OtpTextFieldTheme(
            pinCode = pinCode,
            setPinCode = {pinCode = it},
            color = Blue500,
            maxLength = 4,
            isFullPinCode = {isCheckPinCode = it}
        )
        if (isCheckPinCode) {
            authViewModel.checkPin(pinCode, clientId)
            isCheckPinCode = false
        }
        DescriptionText(text = "$infoPinCode $phone")
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
fun ButtonEnter(text: String, isEnabledButton: Boolean, onEvent: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 52.dp, end = 52.dp),
        shape = RoundedCornerShape(8.dp),
        enabled = isEnabledButton,
        onClick = {
            onEvent()
        }) {
        Text(text = text)
    }
}

@Composable
fun OtpTextFieldTheme(pinCode: String = "", setPinCode: (String) -> Unit, color: Color, maxLength: Int, isFullPinCode: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp),
        color = Color.White
    ) {
        val focusRequester = remember { FocusRequester() }
        BasicTextField(
            modifier = Modifier
                .focusRequester(focusRequester),
            value = pinCode,
            onValueChange = {
                when {
                    it.length <= maxLength -> {
                        setPinCode(it)
                    }
                }
                    isFullPinCode(it.length == maxLength)
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
                                    if (index < pinCode.length) color
                                    else Color.Transparent
                                )
                                .padding(2.dp),
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview
@Composable
private fun EnterPinCodeScreenPreview() {
    var pinCode by rememberSaveable {
        mutableStateOf("")
    }
    var isEnabledButton by rememberSaveable {
        mutableStateOf(false)
    }
    val text = stringResource(id = R.string.fragment_enter_pin_code_info_code)
    val phone = "+7(123) 456-78-90"
    YourWaterTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginTitle(title = stringResource(id = R.string.fragment_enter_pin_code_enter_text))
            EnterPinCodeText()
            OtpTextFieldTheme(
                pinCode = pinCode,
                setPinCode = {pinCode = it},
                color = Blue500,
                maxLength = 4,
                isFullPinCode = {isEnabledButton = it},
            )
            ButtonEnter(
                stringResource(id = R.string.fragment_enter_pin_code_enter_text),
                isEnabledButton,
            ) {}
            DescriptionText(text = "$text $phone")
        }
    }
}