package ru.iwater.youwater.screen.profile


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.iwater.youwater.R
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun NotificationScreen(clientProfileViewModel: ClientProfileViewModel = viewModel()) {
    val client by clientProfileViewModel.client.observeAsState()
    val modifier = Modifier
    if (client != null) {
        var check by rememberSaveable {
            mutableStateOf(client?.mailing == 1)
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.MailingNotification),
                style = YouWaterTypography.body1,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
            Switch(
                checked = check,
                onCheckedChange = {
                    check = it
                    clientProfileViewModel.setMailing(client?.client_id!!, it)
                },
                modifier.padding(8.dp),
                colors = SwitchDefaults.colors(checkedThumbColor = Blue500, uncheckedThumbColor = Color.LightGray)
            )
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp")
@Composable
fun NotificationScreenPreview() {
    YourWaterTheme {
        val modifier = Modifier
        var check by rememberSaveable {
            mutableStateOf(false)
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.MailingNotification),
                style = YouWaterTypography.body1,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
            Switch(
                checked = check,
                onCheckedChange = {check = it},
                modifier.padding(8.dp),
                colors = SwitchDefaults.colors(checkedThumbColor = Blue500, uncheckedThumbColor = Color.LightGray)
            )
        }

    }

}