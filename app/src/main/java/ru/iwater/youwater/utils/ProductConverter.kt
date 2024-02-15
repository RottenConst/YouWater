package ru.iwater.youwater.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Price
import ru.iwater.youwater.data.Product
import timber.log.Timber
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.util.*

class ProductConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<NewProduct> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<NewProduct>>() {}.type
        return gson.fromJson<List<NewProduct>>(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(list: List<NewProduct>): String {
        return gson.toJson(list)
    }
}

class PriceConverter {

    var gson = Gson()
    @TypeConverter
    fun fromPrice(prices: List<Price>): String {
        val pricesString = StringBuilder()
        prices.forEach {
            pricesString.append("${it.border},${it.price};")
        }
        return pricesString.toString()
    }

    @TypeConverter
    fun toPrice(stringPrice: String): List<Price> {
        val price = stringPrice.removeSuffix(";").split(";")
        val prices = mutableListOf<Price>()
        if (price.size == 1 && price.first().isEmpty()) {
            prices.add(Price(1, -1))
        } else {
            price.forEach {
                val border = it.split(",").first().toInt()
                val priceProduct = it.split(",").last().toInt()
                prices.add(Price(border, priceProduct))
            }
        }
        return prices
    }
}
