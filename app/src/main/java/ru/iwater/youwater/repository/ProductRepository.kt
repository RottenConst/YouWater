package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.ProductDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.*
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject
import kotlin.Exception

@OnScreen
class ProductRepository @Inject constructor(
    youWaterDB: YouWaterDB,
    private val authClient: StorageStateAuthClient
) {

    private val productDao: ProductDao = youWaterDB.productDao()
    private val apiWater: ApiWater = RetrofitFactory.makeRetrofit()

    /**
     * получить список продуктов добавленых в корзину
     */
    suspend fun getProductListOfCategory(): List<Product>? {
        return try {
            val clientFirst = apiWater.isStartPocket(getAuthClient().clientId)
            if (clientFirst.isSuccessful) {
                val isFirst = clientFirst.body()?.get("status")?.asBoolean
                if (isFirst == true) {
                    productDao.getAllProduct()
                } else {
                    val startProductList = productDao.getAllProduct()?.filter { product -> product.category == 20 }
                    startProductList?.forEach {
                        productDao.delete(it)
                    }
                    productDao.getAllProduct()
                }
            } else {
                Timber.d("error start pocket api")
                null
            }
        } catch (e: Exception) {
            Timber.e("Get product error: $e")
            null
        }
    }

    /**
     * получить продукт в корзину
     */
    suspend fun addProductInBasket(product: Product) {
        productDao.save(product)
    }

    /**
     * получить продукт по id
     */
    suspend fun getProductFromDB(id: Int): Product? {
        return productDao.getProduct(id)
    }

    /**
     * обновить продукт в корзине
     */
    suspend fun updateProductInBasket(product: Product) {
        productDao.updateProductInBasked(product)
    }

    /**
     * добавить продукт в корзину
     */
    suspend fun deleteProductFromBasket(product: Product) {
        productDao.delete(product)
    }

    /**
     * добавить избранный товар
     */
    suspend fun addToFavorite(productId: Int): Boolean? {
        return try {
            val clientId = getAuthClient().clientId
            val status = apiWater.addToFavoriteProduct(clientId, productId)
            if (status != null) {
                status["status"].asBoolean
            } else false
        } catch (e: Exception) {
            Timber.e("Error add to favorite : $e")
            null
        }
    }

    /**
     * удалить избранный товар
     */
    suspend fun deleteFavoriteProduct(productId: Int): Boolean? {
        return try {
            val status = apiWater.deleteFavoriteProduct(getAuthClient().clientId, productId)
            if (status != null) {
                status["status"].asBoolean
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e("Error delete favorite product: $e")
            null
        }

    }

    /**
     * получить список избранных товаров
     */
    suspend fun getFavoriteProducts(): List<Int>? {
        return try {
            val products = apiWater.getFavoriteProduct(getAuthClient().clientId)
            if (products != null) {

                val favorite = mutableListOf<Int>()
                products["favorites_list"].asJsonArray.forEach {
                    favorite.add(it.asInt)
                }
                Timber.d("favorite $favorite")
                favorite
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e("Error get favorite product: $e")
            null
        }
    }

    /**
     * получить список банеров по акциям
     */
    suspend fun getPromoBanners(): List<PromoBanner> {
        return try {
            val promoBanners = apiWater.getPromo()
            if (promoBanners.isNullOrEmpty()) emptyList() else promoBanners
        }catch (e: Exception) {
            Timber.e("Error get promo banner: $e")
            emptyList()
        }
    }


    /**
     * получить список продуктов
     */
    suspend fun getProductList(): List<Product> {
        return try {
            val productList = apiWater.getProductList()
            if (productList.isNotEmpty()) {
                productList.filter { it.app == 1 }
            } else emptyList()
        } catch (e: Exception) {
            Timber.e("Error get product list: $e")
            emptyList()
        }
    }

    /**
     * получить список товаров определённой категории
     */
    suspend fun getProductListOfCategory(category: Int): List<Product> {
        return try {
            val productList = apiWater.getProductList()
            if (productList.isNotEmpty()) {
                productList.filter { it.app == 1 && it.category == category }
            } else emptyList()
        }catch (e: Exception) {
            Timber.e("Error getProductListOfCategory: $e")
            emptyList()
        }
    }

    /**
     * загрузить информацию о товаре по id
     */
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

    /**
     * получить id последней заявки
     */
    suspend fun getLastOrder(): Int? {
        return try {
            val listOrders = apiWater.getOrderClient(getAuthClient().clientId)
            if (!listOrders.isNullOrEmpty()) {
                val order = listOrders[0]
                order.id
            } else null
        }catch (e:Exception) {
            Timber.d("Error get last order: $e")
            null
        }
    }

    /**
     * получить информацю о клиенте
     */
    private fun getAuthClient(): AuthClient = authClient.get()
}