package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.FavoriteProductDao
import ru.iwater.youwater.bd.ProductDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.AuthClient
import ru.iwater.youwater.data.FavoriteProduct
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.TypeProduct
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@OnScreen
class ProductRepository @Inject constructor(
    youWaterDB: YouWaterDB,
    private val authClient: StorageStateAuthClient
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

    suspend fun getFavoriteProductFromDB(id: Int): FavoriteProduct? {
        return favoriteDao.getFavoriteProduct(id)
    }

    suspend fun addToFavoriteProduct(favoriteProduct: FavoriteProduct) {
        favoriteDao.save(favoriteProduct)
    }

    suspend fun deleteFavoriteProduct(favoriteProduct: FavoriteProduct) {
        favoriteDao.delete(favoriteProduct)
    }

    suspend fun getAllFavoriteProducts(): List<FavoriteProduct>{
        val favoriteProducts = favoriteDao.getAllProduct()
        return if (favoriteProducts.isNullOrEmpty()) {
            emptyList()
        } else favoriteProducts
    }


    /**
     * получить список товаров определённой категории
     */
    suspend fun getProductList(category: Int): List<Product> {
        return try {
            val productList = apiWater.getProductList()
            if (productList.isNotEmpty()) {
                productList.filter { it.app == 1 && it.category == category }
            } else emptyList()
        }catch (e: Exception) {
            Timber.e("Error getProductList: $e")
            emptyList()
        }
    }

    suspend fun getProduct(productId: Int): Product? {
        return try {
            apiWater.getProduct(productId)
        } catch (e: Exception) {
            Timber.e("Exception get product $e")
            null
        }
    }

    /**
     * получить список категорий
     */
    suspend fun getCategoryList(): List<TypeProduct> {
        return try {
            val startPocket = apiWater.isStartPocket(getAuthClient().clientId) //стартовый пакет
            var startClient: Boolean? = false // клиент новый?
            if (startPocket.isSuccessful) {
                startClient = startPocket.body()?.get("status")?.asBoolean
            }
            val category = apiWater.getCategoryList()
            if (!category.isNullOrEmpty()) {
                if (startClient == false || startClient == null) {
                    return category.filter { it.id != 20 && it.visible_app == 1 && it.company_id == "0007" }.sortedBy { it.priority }
                }
                return category.filter { it.visible_app == 1 && it.company_id == "0007"}.sortedBy { it.priority }
            } else emptyList()
        }catch (e: Exception) {
            Timber.e("Error get catalog list: $e")
            emptyList()
        }
    }

    private fun getAuthClient(): AuthClient = authClient.get()
}