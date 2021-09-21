package ru.iwater.youwater.repository

import ru.iwater.youwater.domain.Product
import ru.iwater.youwater.domain.TypeProduct
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import ru.iwater.youwater.utils.Generator
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class ProductRepository @Inject constructor() {

    val apiWater: ApiWater = RetrofitFactory.makeRetrofit()

//    fun getProductList(generator: Generator): List<Product> {
//        return generator.getProduct()
//    }

    suspend fun getProductList(): List<Product> {
        var productList: List<Product> = emptyList()
        try {
            productList = apiWater.getProductList()
            if (!productList.isNullOrEmpty()) {
                return productList.filter { it.category == 1 && it.app == 1 }
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return productList
    }

    suspend fun getCategoryList(): List<TypeProduct> {
        var category: List<TypeProduct> = emptyList()
        try {
            category = apiWater.getCategoryList()
            if (!category.isNullOrEmpty()) {
                return category.filter { it.visible_app == 1 && it.company_id == "0007"}
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return category
    }
}