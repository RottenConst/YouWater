package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.bd.ProductDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.*
import ru.iwater.youwater.data.payModule.MessagePay
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
            val productList = apiWater.getProductList().filter { it.app == 1 }
            productList.ifEmpty { emptyList() }
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
            Timber.d("Create order app?")
            apiWater.createOrder(order)?.data?.id ?: -1
        } catch (e: Exception) {
            Timber.e("error create order: $e")
            -1
        }
    }


    suspend fun createPay(amount: String, description: String, paymentToken: String, capture: Boolean): MessagePay? {
        return try {
            val payment = apiWater.createPay(amount, description, paymentToken, capture)
            if (payment?.message == "Order created") {
                payment
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e("error create pay $e")
            null
        }
    }

    suspend fun setStatusPayment(orderId: Int, parameters: JsonObject): Boolean {
        return try {
            val answer = apiWater.setStatusPayment(orderId, parameters)
            answer.isSuccessful
        }catch (e: Exception) {
            Timber.e("error set payment status: $e")
            false
        }
    }

    suspend fun getOrder(orderId: Int): OrderFromCRM? {
       return try {
           Timber.d("get ORDER, ${getAuthClient().clientId}")
            val listOrder = apiWater.getOrderClient(getAuthClient().clientId)
            return if (listOrder.isNullOrEmpty()) {
                null
            } else {
                listOrder.filter {
                        orderFromCRM -> orderFromCRM.order_id == orderId
                }
                listOrder[0]
            }
        } catch (e: Exception) {
            Timber.e("error get order: $e")
            null
        }
    }

    suspend fun getOrdersList(): List<OrderFromCRM> {
        return try {
            val order = apiWater.getOrderClient(getAuthClient().clientId) ?: emptyList()
            order
        } catch (e: Exception) {
            Timber.e("error get ordersList: $e")
            emptyList()
        }
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

    suspend fun editUserData(clientId: Int, clientUserData: JsonObject): Boolean {
        try {
            val answer = apiWater.editUserData(clientId, clientUserData)
            if (answer?.isSuccessful == true) {
                return answer.body()?.get("status")?.asBoolean == true
            }
        } catch (e: java.lang.Exception) {
            Timber.e("error edit user data: $e")
        }
        return false
    }

    /**
     * послать запрос на деактивироватцию адреса
     */
    suspend fun inactiveAddress(id: Int): Boolean {
        return try {
            val active = apiWater.deleteAddress(id)
            active.isSuccessful
        } catch (e: Exception) {
            Timber.e("delete address error $e")
            false
        }
    }

    /**
     * отправить запрос на создание адреса
     */
    suspend fun createAddress(
        newAddressParameters: JsonObject
    ): String? {
        return try {
            val response = apiWater.createNewAddress(newAddressParameters)
            if (response.isSuccessful) {
                response.body()?.get("message")?.asString
            } else null
        } catch (e: Exception) {
            Timber.e("Error create address: $e")
            null
        }
    }

    suspend fun setMailing(clientId: Int, isMailing: Boolean) {
        try {
            val mailing = JsonObject()
            if (isMailing) {
                mailing.addProperty("mailing_consent", 1)
                apiWater.mailing(clientId, mailing)
            } else {
                mailing.addProperty("mailing_consent", 0)
                apiWater.mailing(clientId, mailing)
            }
        } catch (e: Exception) {
            Timber.e("error set mailing: $e")
        }
    }

    fun deleteClient() {
        authClient.remove()
    }

    fun getAuthClient(): AuthClient = authClient.get()
}