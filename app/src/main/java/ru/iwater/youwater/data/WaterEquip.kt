package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class WaterEquip(
    val id: Int,
    val price: Int,
    val amount: Int
)