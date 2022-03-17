package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.FavoriteProductDao
import ru.iwater.youwater.bd.ProductDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.FavoriteProduct
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.TypeProduct
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@OnScreen
class ProductRepository @Inject constructor(
    youWaterDB: YouWaterDB
) {

    private val productDao: ProductDao = youWaterDB.productDao()
    private val favoriteDao: FavoriteProductDao = youWaterDB.favoriteProductDao()
    private val apiWater: ApiWater = RetrofitFactory.makeRetrofit()

    suspend fun getProductList(): List<Product>? {
        return productDao.getAllProduct()
    }

    suspend fun addProductInBasket(product: Product) {
        productDao.save(product)
    }

    suspend fun getProductFromDB(id: Int): Product? {
        return productDao.getProduct(id)
    }

    suspend fun updateProductInBasket(product: Product) {
        productDao.updateProductInBasked(product)
    }

    suspend fun deleteProductFromBasket(product: Product) {
        productDao.delete(product)
    }

    suspend fun addToFavoriteProduct(favoriteProduct: FavoriteProduct) {
        favoriteDao.save(favoriteProduct)
    }

    suspend fun deleteFavoriteProduct(favoriteProduct: FavoriteProduct) {
        favoriteDao.delete(favoriteProduct)
    }

    suspend fun getAllFavoriteProducts(): List<FavoriteProduct>? = favoriteDao.getAllProduct()


    /**
     * получить список товаров определённой категории
     */
    suspend fun getProductList(category: Int): List<Product> {
        var productList: List<Product> = emptyList()
        try {
            productList = apiWater.getProductList()
            if (!productList.isNullOrEmpty()) {
                return productList.filter { it.app == 1 && it.category == category }
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return productList
    }

    suspend fun getProduct(productId: Int): Product? {
        try {
            val product = apiWater.getProductList()
            if (!product.isNullOrEmpty())
                return product.filter { it.id == productId }[0]
        }catch (e: Exception) {
            Timber.e("Exception get product $e")
        }
        return null
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