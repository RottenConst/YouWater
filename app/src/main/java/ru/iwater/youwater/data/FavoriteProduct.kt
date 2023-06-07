package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class FavoriteProduct(
    val about: String,
    val app: Int,
    val app_name: String?,
    val category: Int,
    val company_id: String,
    val gallery: String,
    val id: Int,
    val name: String,
    val price: String,
)
