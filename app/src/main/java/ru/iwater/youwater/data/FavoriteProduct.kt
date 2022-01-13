package ru.iwater.youwater.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteProduct(
    val about: String,
    val app: Int,
    val app_name: String?,
    val category: Int,
    val company_id: String,
    val gallery: String,
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val price: String,
)
