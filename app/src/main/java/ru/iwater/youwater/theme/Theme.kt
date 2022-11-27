package ru.iwater.youwater.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun YourWaterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColors,
        typography = YouWaterTypography,
        content = content
    )
}

private val LightColors = lightColors(
    primary = Blue500,
    primaryVariant = Aqua700,
    onPrimary = Color.White,
    secondary = Blue500,
    secondaryVariant = Aqua700,
    onSecondary = Color.White,
    onError = Red800
)