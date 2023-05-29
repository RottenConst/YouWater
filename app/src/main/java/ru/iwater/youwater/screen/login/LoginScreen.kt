package ru.iwater.youwater.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.R
import ru.iwater.youwater.vm.AuthViewModel
import ru.iwater.youwater.theme.YouWaterTypography
import kotlin.math.absoluteValue

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController
//    navController: NavController
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
        InputTelNumberField(phone = phone, setPhone = {phone = it}, isFullPhoneNumber = {isEnabledButton = it})
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
fun InputTelNumberField(phone: String, setPhone: (String) -> Unit, isFullPhoneNumber: (Boolean) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        value = phone,
        onValueChange = {number ->
            if (number.length <= NumberDefaults.INPUT_LENGTH) {
                setPhone(number)
                isFullPhoneNumber(number.length == NumberDefaults.INPUT_LENGTH)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        label = { Text(text = stringResource(id = R.string.login_fragment_tel_number))},
        visualTransformation =
        MaskVisualTransformation(NumberDefaults.MASK),
        maxLines = 1
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

object NumberDefaults {
    const val MASK = "+7(###) ###-####"
    const val INPUT_LENGTH = 10
}

class MaskVisualTransformation(private val mask: String) : VisualTransformation {

    private val specialSymbolsIndices = mask.indices.filter { mask[it] != '#'}
    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while (specialSymbolsIndices.contains(maskIndex)) {
                out += mask[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return TransformedText(AnnotatedString(out), offsetTranslator())
    }
    private fun offsetTranslator() = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val offsetValue = offset.absoluteValue
            if (offsetValue == 0) return 0
            var numberOfHashtags = 0
            val masked = mask.takeWhile {
                if (it == '#') numberOfHashtags++
                numberOfHashtags < offsetValue
            }
            return masked.length + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            return mask.take(offset.absoluteValue).count { it == '#' }
        }
    }
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
                setPhone = {phone = it},
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