package ru.iwater.youwater.screen.profile


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun NotificationScreen(
    mailingConsent: Boolean,
    setMailing: (Boolean) -> Unit
) {

    val modifier = Modifier
    var check by rememberSaveable {
        mutableStateOf(mailingConsent)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.MailingNotification),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )
        Switch(
            checked = check,
            onCheckedChange = {
                check = it
                setMailing(it)
                              },
            modifier.padding(8.dp),
//            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, uncheckedThumbColor = MaterialTheme.colorScheme.outlineVariant)
        )
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
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
            Switch(
                checked = check,
                onCheckedChange = {check = it},
                modifier.padding(8.dp),
//                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, uncheckedThumbColor = MaterialTheme.colorScheme.outlineVariant)
            )
        }

    }

}