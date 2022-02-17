package ru.iwater.youwater.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.iwater.youwater.utils.ProductConverter

@Keep
@Entity
data class MyOrder(
    val address: String,
    val cash: String,
    val date: String,
    @TypeConverters(ProductConverter::class)
    val products: List<Product>,
    val typeCash: String?,
    val status: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)