package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class AddressJson(
    val building: String?,
    val entrance: String,
    val flat: String,
    val floor: String,
    val house: String,
    val region: String,
    val street: String
)