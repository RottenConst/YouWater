package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.bd.FavoriteProductDao
import ru.iwater.youwater.bd.ProductDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.*
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import ru.iwater.youwater.network.RetrofitSberApi
import ru.iwater.youwater.network.SberPaymentApi
import timber.log.Timber
import javax.inject.Inject
import kotlin.Exception

@OnScreen
class ProductRepository @Inject constructor(
    youWaterDB: YouWaterDB,
    private val authClient: StorageStateAuthClient
) {

    private val productDao: ProductDao = youWaterDB.productDao()
    private val favoriteDao: FavoriteProductDao = youWaterDB.favoriteProductDao()
    private val apiWater: ApiWater = RetrofitFactory.makeRetrofit()
    private val sberApi: SberPaymentApi = RetrofitSberApi.makeRetrofit()

    /**
     * получить список продуктов добавленых в корзину
     */
    suspend fun getProductListOfCategory(): List<Product> {
        return productDao.getAllProduct() ?: emptyList()
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
    suspend fun addToFavoriteProduct(favoriteProduct: FavoriteProduct) {
        favoriteDao.save(favoriteProduct)
    }

    /**
     * удалить избранный товар
     */
    suspend fun deleteFavoriteProduct(favoriteProduct: FavoriteProduct) {
        favoriteDao.delete(favoriteProduct)
    }

    /**
     * получить список избранных товаров
     */
    suspend fun getAllFavoriteProducts(): List<FavoriteProduct>{
        val favoriteProducts = favoriteDao.getAllProduct()
        return if (favoriteProducts.isNullOrEmpty()) {
            emptyList()
        } else favoriteProducts
    }

    suspend fun getFavorite(): Favorite? {
        return try {
            apiWater.getFavoriteProduct(authClient.get().clientId)
        } catch (e: Exception) {
            Timber.e("get favorite error: $e")
            null
        }
    }

    suspend fun addToFavoriteProduct(productId: Int): Boolean {
        return try {
            apiWater.addToFavoriteProduct(authClient.get().clientId, productId)?.status ?: false
        } catch (e: Exception) {
            Timber.e("add to favorite error: $e")
            false
        }
    }

    suspend fun deleteFavorite(productId: Int): Boolean {
        return try {
            apiWater.deleteFavoriteProduct(authClient.get().clientId, productId)?.status ?: false
        } catch (e: Exception) {
            Timber.e("delete favorite product error: $e")
            false
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
            val startPocket = apiWater.isStartPocket(getAuthClient().clientId)?.status ?: false //стартовый пакет
            val category = apiWater.getCategoryList()
            if (!category.isNullOrEmpty()) {
                if (!startPocket) {
                    return category.filter { it.id != 20 && it.visible_app == 1 && it.company_id == "0007" }.sortedBy { it.priority }
                }
                return category.filter { it.visible_app == 1 && it.company_id == "0007"}.sortedBy { it.priority }
            } else emptyList()
        }catch (e: Exception) {
            Timber.e("Error get catalog list: $e")
            emptyList()
        }
    }

    suspend fun isStartPocket(): Boolean {
        return try {
            apiWater.isStartPocket(getAuthClient().clientId)?.status ?: false
        } catch (e: Exception) {
            Timber.e("Error get catalog list: $e")
            false
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

    suspend fun getDelivery(address: RawAddress): DeliverySchedule? {
        return try {
            val region = address.region ?: address.fullAddress.split(",")[0]
            val city = address.factAddress.split(',')[0]
            val street = address.factAddress.split(',')[1]
            val house = address.factAddress.split(',')[2]
            val jsonAddress = JsonObject().apply {
                addProperty("region", region)
                addProperty("city", city)
                addProperty("floor", house)
                addProperty("entrance", "")
                addProperty("street", street)
                addProperty("house", house)
                addProperty("building", "")
                addProperty("flat", "")
            }
            return apiWater.getDeliverySchedule(jsonAddress)
        } catch (e: Exception) {
            Timber.d("Error getDelivery: $e")
            null
        }
    }

    suspend fun getAddress(): List<RawAddress> {
        return try {
            apiWater.getAllAddresses(getAuthClient().clientId)
        } catch (e: Exception) {
            Timber.e("Error get address: $e")
            emptyList()
        }
    }

    suspend fun createOrderApp(order: Order): Int {
        return try {
            apiWater.createOrder(order)?.data?.id ?: -1
        } catch (e: Exception) {
            Timber.e("error create order: $e")
            -1
        }
    }

    suspend fun payCard(paymentCard: PaymentCard): List<String> {
        try {
            val answer = sberApi.registerOrder(
                userName = paymentCard.userName,
                password = paymentCard.password,
                orderNumber = paymentCard.orderNumber,
                amount = paymentCard.amount,
                returnUrl = paymentCard.returnUrl,
                pageView = "MOBILE",
                phone = paymentCard.phone
            )
            val dataPayment = mutableListOf<String>()
            return run {
                dataPayment.add(answer["orderId"].toString())
                dataPayment.add(answer["formUrl"].toString())
                dataPayment
            }
        } catch (e: Exception) {
            Timber.e("error pay: $e")
        }
        return emptyList()
    }

    /**
     * получить информацю о клиенте
     */
    suspend fun getClientInfo(): Client? {
        try {
            val client = apiWater.getClientDetail(getAuthClient().clientId)
            if (client != null) {
                return client
            }
        } catch (e: Exception) {
            Timber.e("error get client: $e")
        }
        return null
    }
    private fun getAuthClient(): AuthClient = authClient.get()
}