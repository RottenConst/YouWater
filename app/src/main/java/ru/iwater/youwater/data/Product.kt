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
    val price: String,
    val shname: String?,
    val site: Int,
    var count: Int = 0,
    var onFavoriteClick: Boolean = false
) {

    fun getMinPriceProduct(): Int {
        return this.price.split(';')[0].split(':')[1].toInt()
    }


    fun getPriceNoDiscount(count: Int): Int {
        val priceList = this.price.removeSuffix(";").split(';')
        var price = 0
        priceList.forEach {
            val priceCount = it.split(':')
            if(priceCount[0].toInt() <= count) {
                price = (priceCount[1].toInt()) * count
            }
        }
        return price
    }
    fun getPriceOnCount(count: Int): Int {
        val priceList = this.price.removeSuffix(";").split(';')
        var price = 0
        return when (this.id) {
            81 -> {
                priceList.forEach {
                    val priceCount = it.split(':')
                    if(priceCount[0].toInt() <= count) {
                        price = (priceCount[1].toInt() - 15) * count
                    }
                }
                price
            }
            84 -> {
                priceList.forEach {
                    val priceCount = it.split(':')
                    if(priceCount[0].toInt() <= count) {
                        price = (priceCount[1].toInt() - 15) * count
                    }
                }
                price
            }
            else -> {
                priceList.forEach {
                    val priceCount = it.split(':')
                    if(priceCount[0].toInt() <= count) {
                        price = (priceCount[1].toInt()) * count
                    }
                }
                price
            }
        }
    }


}