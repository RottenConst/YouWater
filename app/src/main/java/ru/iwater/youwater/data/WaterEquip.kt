package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class WaterEquip(
    val count: Int,
    val id: Int,
    val price: Int
)