package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class AddressJson(
    val building: String,
    val entrance: Int,
    val flat: Int,
    val floor: Int,
    val house: Int,
    val region: String,
    val street: String
)