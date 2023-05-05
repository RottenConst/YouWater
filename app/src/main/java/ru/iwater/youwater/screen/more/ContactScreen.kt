package ru.iwater.youwater.screen.more

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.screen.profile.MenuButton
import ru.iwater.youwater.theme.Blue500

@Composable
fun ContactScreen(contactFragment: ContactFragment) {
    val modifier = Modifier
    Column(modifier = modifier.fillMaxSize()) {
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_call_me),
            tint = Blue500,
            nameButton = stringResource(id = R.string.fragment_contact_call_me),
            description = stringResource(id = R.string.fragment_contact_call_me)
        )
        {
            val callIntent: Intent = Uri.parse("tel:+78129477993").let { number ->
                Intent(Intent.ACTION_DIAL, number)
            }
            contactFragment.startActivity(callIntent)
        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_send_mail),
            tint = Blue500,
            nameButton = stringResource(id = R.string.fragment_contact_send_mail),
            description = stringResource(id = R.string.fragment_contact_send_mail)
        )
        {
            val sendMail = Intent(Intent.ACTION_SENDTO)
            sendMail.data = Uri.parse("mailto:info@yourwater.ru")
            contactFragment.startActivity(sendMail)
        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_director),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_phone_bos),
            description = stringResource(id = R.string.fragment_contact_phone_bos)
        )
        {
            val callBoss = Intent(Intent.ACTION_SENDTO)
            callBoss.data = Uri.parse("mailto:nadobnikov@allforwater.ru")
            contactFragment.startActivity(callBoss)
        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_tg),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_telegram),
            description = stringResource(id = R.string.fragment_contact_telegram)
        ) {
            val openTelegram = Intent(Intent.ACTION_VIEW)
            openTelegram.data = Uri.parse("https://t.me/yourwater_ru_bot")
            contactFragment.startActivity(openTelegram)
        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_vk),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_vk),
            description = stringResource(id = R.string.fragment_contact_vk)
        ) {
            val openVK = Intent(Intent.ACTION_VIEW)
            openVK.data = Uri.parse("https://vk.com/write-23344137")
            contactFragment.startActivity(openVK)
        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_whatsapp),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_whats_app),
            description = stringResource(id = R.string.fragment_contact_whats_app)
        ) {
            val openWhatsApp = Intent(Intent.ACTION_VIEW)
            openWhatsApp.data = Uri.parse("https://wa.me/78129477993")
            contactFragment.startActivity(openWhatsApp)
        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_insta),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_instagram),
            description = stringResource(id = R.string.fragment_contact_instagram)
        ) {
            val openInsta = Intent(Intent.ACTION_VIEW)
            openInsta.data = Uri.parse("https://instagram.com/yourwater_delivery")
            contactFragment.startActivity(openInsta)
        }

    }
}

@Preview
@Composable
fun ContactScreenPreview() {
    val modifier = Modifier
    Column(modifier = modifier.fillMaxSize()) {
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_call_me),
            tint = Blue500,
            nameButton = stringResource(id = R.string.fragment_contact_call_me),
            description = ""
        ) {

        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_send_mail),
            tint = Blue500,
            nameButton = stringResource(
                id = R.string.fragment_contact_send_mail
            ),
            description = ""
        ) {

        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_director),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_phone_bos), description = ""
        ) {

        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_tg),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_telegram), description = ""
        ) {

        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_vk),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_vk), description = ""
        ) {

        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_whatsapp),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_whats_app),
            description = ""
        ) {

        }
        MenuButton(
            modifier = modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_insta),
            tint = Color.Unspecified,
            nameButton = stringResource(id = R.string.fragment_contact_instagram),
            description = ""
        ) {

        }

    }
}