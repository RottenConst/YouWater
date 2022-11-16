package ru.iwater.youwater.data

import androidx.annotation.Keep


@Keep
data class ProductXXX(
    val about: String,
    val category: Int,
    val discount: Int,
    val gallery: String,
    val name: String,
    val price: String,
    val product_id: Int,
    val quantity: Int,
    val shname: Any
)