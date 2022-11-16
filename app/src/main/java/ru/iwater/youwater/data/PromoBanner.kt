package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class PromoBanner(
    val description: String,
    val discount: Double,
    val discount_type: Boolean,
    val display_in_app: Boolean,
    val end_date: String,
    val id: Int,
    val is_active: Boolean,
    val name: String,
    val picture: String,
    val products: List<ProductXX>,
    val promocode: String,
    val start_date: String
)