package ru.iwater.youwater.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.screen.navigation.StartNavRoute
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.utils.StatusSession

@Composable
fun StartAppScreen(
    startActivity: StartActivity,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val statusSession by authViewModel.statusSession.observeAsState()
    Background()
    statusSession.let { status ->
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
            StatusSession.CHECKED -> {
                authViewModel.checkSession()
                StartLogoContent(visibleStartButton = false) {  }
            }

            else -> {}
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
        Card(
            modifier = Modifier
                .padding(8.dp)
                .width(246.dp)
                .height(246.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Image(
                modifier = Modifier.padding(52.dp).fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_your_water_logo),
                contentDescription = stringResource(id = R.string.description_your_watter_logo),
                alignment = Alignment.Center
            )
        }
        Text(
            text = stringResource(id = R.string.start_fragment_about_app_logo),
            color = MaterialTheme.colorScheme.primary,
            style = YouWaterTypography.titleMedium,
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
private fun Background() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_top),
            contentDescription = stringResource(id = R.string.description_image_background),
            alignment = Alignment.TopCenter
        )

        Image(
            painter = painterResource(id = R.drawable.splash_bottom),
            contentDescription = stringResource(id = R.string.description_image_background),
            alignment = Alignment.BottomCenter
        )
    }
}

@Preview
@Composable
private fun StartAppScreenPreview() {
    YourWaterTheme {
        Background()
        StartLogoContent(visibleStartButton = true) {
        }
    }
}