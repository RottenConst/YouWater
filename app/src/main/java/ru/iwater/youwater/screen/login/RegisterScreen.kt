package ru.iwater.youwater.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun RegisterScreen(viewModel: AuthViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginTitle(title = stringResource(id = R.string.fragment_register_register_text))
        InputRegisterData()
    }
}

@Composable
fun InputRegisterData() {
    var nameClient by remember { mutableStateOf(TextFieldValue("")) }
    var emailClient by remember { mutableStateOf(TextFieldValue("")) }
    val isEnabled = nameClient.text.isNotEmpty() && emailClient.text.isNotEmpty()

    Column(
        Modifier.padding(vertical = 16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = nameClient,
            onValueChange =  { name ->
                nameClient = name
            },
            label = { Text(text = stringResource(id = R.string.fragment_register_name_lastname))}
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            value = emailClient,
            onValueChange = { email ->
                emailClient = email
            },
            label = { Text(text = stringResource(id = R.string.general_email))}
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 52.dp,
                    end = 52.dp,
                    top = 52.dp
                ),
            enabled = isEnabled,
            onClick = {  }) {
                Text(text = stringResource(id = R.string.fragment_register_finish))
        }
    }
}


@Preview
@Composable
private fun InputRegisterDataPreview() {
    YourWaterTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginTitle(title = stringResource(id = R.string.fragment_register_register_text))
            InputRegisterData()
        }
    }

}