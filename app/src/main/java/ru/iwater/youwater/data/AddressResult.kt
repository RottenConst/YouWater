package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class AddressResult(
    val results: List<Result>,
    val status: String
)