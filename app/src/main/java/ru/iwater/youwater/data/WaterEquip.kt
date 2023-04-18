package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class WaterEquip(
    val amount: Int,
    val id: Int,
    val name: String,
    val price: Int
)