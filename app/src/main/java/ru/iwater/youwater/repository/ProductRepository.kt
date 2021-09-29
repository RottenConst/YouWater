package ru.iwater.youwater.repository

import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.TypeProduct
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@OnScreen
class ProductRepository @Inject constructor() {

    private val apiWater: ApiWater = RetrofitFactory.makeRetrofit()

    /**
     * получить список товаров определённой категории
     */
    suspend fun getProductList(category: Int): List<Product> {
        var productList: List<Product> = emptyList()
        try {
            productList = apiWater.getProductList()
            if (!productList.isNullOrEmpty()) {
                return productList.filter { it.category == category && it.app == 1 }
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return productList
    }

    /**
     * получить список категорий
     */
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