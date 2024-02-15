package ru.iwater.youwater.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import ru.iwater.youwater.utils.PriceConverter

@Keep
@Entity
data class NewProduct(
    @SerializedName("app_name")
    val appName: String,
    @SerializedName("category")
    val category: Int,
    @PrimaryKey val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    @TypeConverters(PriceConverter::class)
    val price: List<Price>,
    var onFavoriteClick: Boolean = false,
    var count: Int = 0
) {
    fun getPriceNoDiscount(count: Int): Int {
        var price = 0
        this.price.forEach {
            if (it.border <= count) {
                price = it.price * count
            }
        }
        return price
    }

    fun getPriceOnCount(count: Int): Int {
        var price = 0
        return when (this.id) {
            81 -> {
                this.price.forEach {
                    if(it.border <= count) {
                        price = if (count < 10) {
                            (it.price - 30) * count
                        } else {
                            (it.price - 10) * count
                        }
                    }
                }
                price
            }
            84 -> {
                this.price.forEach {
                    if(it.border <= count) {
                        price = if (count < 10) {
                            (it.price - 30) * count
                        } else {
                            (it.price - 10) * count
                        }
                    }
                }
                price
            }
            else -> {
                this.price.forEach {
                    if(it.border <= count) {
                        price = it.price * count
                    }
                }
                price
            }
        }
    }
}