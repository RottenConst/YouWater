package ru.iwater.youwater.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.YourWaterTheme
import timber.log.Timber

@Composable
fun EnterPincodeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginTitle(title = stringResource(id = R.string.fragment_enter_pin_code_enter_text))
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun OtpChar(
    modifier: Modifier = Modifier,
    onTextEdit: (String) -> Unit,
    text: String
) {
//    var text = remember { mutableStateOf("") }
    val pattern = remember { Regex("^[^\\t]*\$") }
    val focusManager = LocalFocusManager.current
    val maxChar = 1
    
    LaunchedEffect(key1 = text) {
        if (text.isNotEmpty()) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Next
            )
        }
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = text,
            onValueChange = onTextEdit,
            modifier = modifier
                .width(50.dp)
                .onKeyEvent {
                    if (it.key == Key.Tab) {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                    if (it.key == Key.Backspace) {
                        focusManager.moveFocus(FocusDirection.Previous)
                    }
                    false
                },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 40.sp
            ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.NumberPassword
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        Divider(
            Modifier
                .width(28.dp)
                .padding(bottom = 2.dp)
                .offset(y = (-10).dp),
            color = Color.Blue,
            thickness = 1.dp)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PinCodeField() {
    val (item1, item2, item3, item4) = FocusRequester.createRefs()
    val pattern = remember { Regex("^[^\\t]*\$") }
    val focusItems: List<FocusRequester> = listOf(item1, item2, item3, item4)
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(4) {
            val text = remember {
                mutableStateOf("")
            }
            OtpChar(
                Modifier
                    .focusRequester(focusItems[it])
                    .focusProperties {
                        next =
                            if (it != focusItems.lastIndex) focusItems[it + 1] else focusItems.last()
                        previous = if (it != 0) focusItems[it - 1] else focusItems.first()
                    },
                onTextEdit = { char ->
                    if (char.length <= 1 &&
                    ((char.isEmpty() || char.matches(pattern))))
                    text.value = char
                             }, text.value)
        }
    }
//    Text(text = "Text $text")
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
            PinCodeField()
            ButtonEnter()
        }
    }
}