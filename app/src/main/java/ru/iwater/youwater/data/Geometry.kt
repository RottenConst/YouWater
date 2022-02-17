package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class Geometry(
    val location: Location,
    val location_type: String,
    val viewport: Viewport
)