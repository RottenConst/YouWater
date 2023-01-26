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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.R
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.theme.YouWaterTypography
import timber.log.Timber
import kotlin.math.absoluteValue

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
//    var telNum by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember {
        mutableStateOf("")
    }
    var buttonEnabled by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = phone,
            onValueChange = {number ->
                if (number.length <= NumberDefaults.INPUT_LENGTH) {
                    phone = number
                    Timber.d("TEXT = $phone")
                }
                buttonEnabled = phone.length == NumberDefaults.INPUT_LENGTH
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = { Text(text = stringResource(id = R.string.login_fragment_tel_number))},
            visualTransformation =
                MaskVisualTransformation(NumberDefaults.MASK),
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
                authViewModel.authPhone(phone, navController)
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