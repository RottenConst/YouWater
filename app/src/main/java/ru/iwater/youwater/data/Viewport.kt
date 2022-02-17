package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)