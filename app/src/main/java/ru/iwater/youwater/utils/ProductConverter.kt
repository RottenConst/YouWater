package ru.iwater.youwater.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.iwater.youwater.data.Product
import java.lang.reflect.Type
import java.util.*

class ProductConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Product> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson<List<Product>>(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(list: List<Product>): String {
        return gson.toJson(list)
    }
}