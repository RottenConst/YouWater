package ru.iwater.youwater.network

import retrofit2.http.GET
import ru.iwater.youwater.domain.Product
import ru.iwater.youwater.domain.TypeProduct

interface ApiWater {

    @GET("iwaterProducts_list/")
    suspend fun getProductList():List<Product>

    @GET("categoryProducts_list/")
    suspend fun getCategoryList():List<TypeProduct>
}