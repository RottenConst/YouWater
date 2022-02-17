package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)