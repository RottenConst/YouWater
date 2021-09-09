package ru.iwater.youwater.network

import retrofit2.http.GET
import ru.iwater.youwater.domain.Product

interface ApiWater {

    @GET("iwaterProducts_list/")
    suspend fun getProductList():List<Product>
}