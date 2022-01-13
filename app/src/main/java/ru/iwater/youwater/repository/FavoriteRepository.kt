package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.FavoriteProductDao
import ru.iwater.youwater.bd.ProductDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.FavoriteProduct
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    youWaterDB: YouWaterDB
) {
    private val favoriteDao: FavoriteProductDao = youWaterDB.favoriteProductDao()
    private val productDao: ProductDao = youWaterDB.productDao()
    private val apiWater: ApiWater = RetrofitFactory.makeRetrofit()

    private suspend fun getFavoriteList(): List<FavoriteProduct>? {
        return favoriteDao.getAllProduct()
    }

    suspend fun saveFavoriteProduct(favoriteProduct: FavoriteProduct) {
        favoriteDao.save(favoriteProduct)
    }

    suspend fun getFavoriteProduct(id: Int): FavoriteProduct? {
        return favoriteDao.getFavoriteProduct(id)
    }

    suspend fun deleteFavoriteProduct(favoriteProduct: FavoriteProduct) {
        favoriteDao.delete(favoriteProduct)
    }

    suspend fun saveProduct(product: Product) {
        productDao.save(product)
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProductInBasked(product)
    }

    suspend fun getProductFromDB(id: Int): Product? {
        return productDao.getProduct(id)
    }

    suspend fun getFavoriteProduct(): List<Product> {
        val productList: List<Product>
        val favoriteProducts: List<FavoriteProduct>? = getFavoriteList()
        try {
            productList = apiWater.getProductList()
            return if (productList.isNotEmpty() && favoriteProducts?.isNotEmpty() == true) {
                val favorite = mutableListOf<Product>()
                productList.forEach { product ->
                    favoriteProducts.forEach {
                        if (product.id == it.id) favorite.add(product)
                    }
                }
                favorite
            } else emptyList()
        } catch (e: Exception) {
            Timber.e(e)
        }
        return emptyList()
    }

//    suspend fun getFavoriteProduct(favoriteProduct: FavoriteProduct){
//        TODO()
//    }
}