package ru.iwater.youwater.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class Product(
    val about: String,
    val app: Int,
    val app_name: String?,
    val category: Int,
    val company_id: String,
    val date: String,
    val date_created: Int,
    val discount: Int,
    val gallery: String,
    @PrimaryKey
    val id: Int,
    val name: String,
    var price: String,
    val shname: String?,
    val site: Int,
    var count: Int = 0,
    var onFavoriteClick: Boolean = false
)