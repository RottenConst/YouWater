package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class Result(
    val address_components: List<AddressComponent>,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val types: List<String>
)