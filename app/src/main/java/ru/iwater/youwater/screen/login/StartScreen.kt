package ru.iwater.youwater.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.iwater.youwater.R
import ru.iwater.youwater.vm.AuthViewModel
import ru.iwater.youwater.vm.StatusSession
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.screen.navigation.StartNavRoute
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun StartAppScreen(
    startActivity: StartActivity,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    authViewModel.checkSession()
    val statusSession by authViewModel.statusSession.observeAsState()
    Background(
        R.drawable.splash_bottom,
        R.drawable.splash_top,
        R.string.description_image_background
    )
    statusSession?.let { status ->
        when(status) {
            StatusSession.TRY -> {
                StartLogoContent(visibleStartButton = false) { navController.navigate(StartFragmentDirections.actionStartFragmentToLoginFragment()) }
                MainActivity.start(navController.context)
                startActivity.finish()
            }
            StatusSession.FALSE -> {
                StartLogoContent(visibleStartButton = true) { navController.navigate(StartNavRoute.LoginScreen.path) }
            }
            StatusSession.ERROR -> {
                StartLogoContent(visibleStartButton = true) { navController.navigate(StartNavRoute.LoginScreen.path) }
            }
        }
    }

}

@Composable
private fun StartLogoContent(visibleStartButton: Boolean, navigateToLoginScreen: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .width(246.dp)
                .height(246.dp),
            shape = CircleShape,
            elevation = 2.dp,
        ) {
            Image(
                modifier = Modifier.padding(52.dp),
                painter = painterResource(id = R.drawable.ic_your_water_logo),
                contentDescription = stringResource(id = R.string.description_your_watter_logo)
            )
        }
        Text(
            text = stringResource(id = R.string.start_fragment_about_app_logo),
            color = Blue500,
            style = YouWaterTypography.h6,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp),
        )
        if (visibleStartButton) {
            Button(
                onClick = navigateToLoginScreen,
                modifier = Modifier
                    .padding(start = 52.dp, top = 32.dp, end = 52.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.start_fragment_begin)
                )
            }
        }
    }
}

@Composable
private fun Background(
    idImageBottom: Int,
    idImageTop: Int,
    idContentDescription: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = idImageTop),
            contentDescription = stringResource(id = idContentDescription),
            alignment = Alignment.TopCenter
        )

        Image(
            painter = painterResource(id = idImageBottom),
            contentDescription = stringResource(id = idContentDescription),
            alignment = Alignment.BottomCenter
        )
    }
}

@Preview
@Composable
private fun StartAppScreenPreview() {
    YourWaterTheme {
        Background(idImageBottom = R.drawable.splash_bottom, idImageTop = R.drawable.splash_top, idContentDescription = R.string.description_image_background)
        StartLogoContent(visibleStartButton = true) {
        }
    }
}