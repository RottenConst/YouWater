package ru.iwater.youwater.repository

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ru.iwater.youwater.bd.NewProductDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.*
import ru.iwater.youwater.data.payModule.yookassa.Amount
import ru.iwater.youwater.data.payModule.yookassa.Payment
import ru.iwater.youwater.data.payModule.yookassa.PaymentInfo
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiClient
import ru.iwater.youwater.network.ApiOrder
import ru.iwater.youwater.network.ApiYookassa
import timber.log.Timber
import javax.inject.Inject
import kotlin.Exception

@OnScreen
class ProductRepository @Inject constructor(
    youWaterDB: YouWaterDB,
    private val authClient: StorageStateAuthClient
) {

    private val productDao: NewProductDao = youWaterDB.newProductDao()
    private val apiOrder: ApiOrder = ApiOrder.makeOrderApi(getAuthClient().accessToken)
    private val apiClient: ApiClient = ApiClient.makeClientApi(getAuthClient().accessToken)
    private val yookassa: ApiYookassa = ApiYookassa.makeYookassaApi()

    /**
     * получить список продуктов добавленых в корзину
     */
    suspend fun getProductListOfCategory(): List<NewProduct> {
        return productDao.getAllNewProduct() ?: emptyList()
    }

    /**
     * получить продукт в корзину
     */
    suspend fun addProductInBasket(product: NewProduct) {
        productDao.save(product)
    }

    /**
     * получить продукт по id
     */
    suspend fun getProductFromDB(id: Int): NewProduct? {
        return productDao.getProduct(id)
    }

    /**
     * обновить продукт в корзине
     */
    suspend fun updateNewProductInBasket(product: NewProduct) {
        productDao.updateNewProductInBasked(product)
    }

    /**
     * добавить продукт в корзину
     */
    suspend fun deleteProductFromBasket(product: NewProduct) {
        productDao.delete(product)
    }

    suspend fun getFavorite(): Favorite? {
        return try {
            apiClient.getFavoriteList()
        } catch (e: Exception) {
            Timber.e("get favorite error: $e")
            null
        }
    }

    suspend fun getProductByCategory(categoryId: Int): List<NewProduct> {
        return try {
            apiOrder.getProductByCategory(categoryId) ?: emptyList()
        } catch (e: Exception) {
            Timber.e("error get product by category $e")
            emptyList()
        }
    }

    suspend fun addToFavoriteProduct(productId: Int): Boolean {
        return try {
            val favourite = JsonObject()
            favourite.addProperty("product_id", productId)
            apiClient.addFavoriteProduct(favourite)?.status ?: false
        } catch (e: Exception) {
            Timber.e("add to favorite error: $e")
            false
        }
    }

    suspend fun deleteFavorite(productId: Int): Boolean {
        return try {
            val favourite = JsonObject()
            favourite.addProperty("product_id", productId)
            apiClient.deleteFavoriteProduct(favourite)?.status ?: false
        } catch (e: Exception) {
            Timber.e("delete favorite product error: $e")
            false
        }
    }

    /**
     * Получить список банеров по акциям
     *
     * @return список банеров
     */
    suspend fun getPromoBanners(): List<Banner> {
        return try {
            val promoBanners = apiOrder.getPromo()
            promoBanners.banners.ifEmpty { emptyList() }
        }catch (e: Exception) {
            Timber.e("Error get promo banner: $e")
            emptyList()
        }
    }


    /**
     * получить список продуктов
     */
    suspend fun getProductList(): List<NewProduct> {
        return try {
            val productList = apiOrder.getProductList()
            productList.ifEmpty { emptyList() }
        } catch (e: Exception) {
            Timber.e("Error get product list: $e")
            emptyList()
        }
    }

    /**
     * загрузить информацию о товаре по id
     * @param productId индификатор продукта
     */
    suspend fun getProduct(productId: Int): InfoProduct? {
        return try {
            apiOrder.getAboutProduct(productId)
        } catch (e: Exception) {
            Timber.e("Exception get product $e")
            null
        }
    }

    /**
     * загрузить информацию о товаре по id
     */
    suspend fun getNewProduct(productId: Int): NewProduct? {
        return try {
            val infoProduct = apiOrder.getAboutProduct(productId)
            if (infoProduct != null) {
                NewProduct(
                    appName = infoProduct.appName ?: "",
                    category = infoProduct.category,
                    id = infoProduct.id,
                    name = infoProduct.name,
                    image = infoProduct.image ?: "",
                    price = infoProduct.price
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e("Exception get product $e")
            null
        }
    }

    suspend fun getMeasureList():List<Measure> {
        return try {
            apiOrder.getMeasuresList()
        }catch (e: Exception) {
            Timber.e("Exeption get List measure $e")
            emptyList()
        }
    }

    /**
     * получить список категорий
     */
    suspend fun getCategoryList(isStartPocket: Boolean): List<TypeProduct> {
        return try {
            val category = apiOrder.getCategoryList() ?: emptyList()
            if (category.isNotEmpty()) {
                Timber.d("Category is not empty")
                return if (isStartPocket) {
                    category.filter { it.visibleApp }
                } else {
                    category.filter { it.id != 20 && it.visibleApp }
                }
            }
            Timber.d("Category is empty")
            category
        } catch (e: Exception) {
            Timber.e("Error get catalog list: $e")
            emptyList()
        }
    }

    suspend fun isStartPocket(): Boolean {
        return try {
            apiOrder.isStartPocket()?.status ?: false
        } catch (e: Exception) {
            Timber.e("Error get start pocket: $e")
            false
        }
    }

    /**
     * получить id последней заявки
     */
    suspend fun getLastOrder(): Int? {
        return try {
            val listOrders = apiOrder.getOrderClient()
            if (!listOrders.isNullOrEmpty()) {
                val order = listOrders[0]
                order.id
            } else null
        }catch (e:Exception) {
            Timber.d("Error get last order: $e")
            null
        }
    }

    suspend fun getDelivery(address: NewAddress): DeliverySchedule? {
        return try {
            apiClient.getDeliverySchedule(address.getDeliveryProperty())
        } catch (e: Exception) {
            Timber.d("Error getDelivery: $e")
            null
        }
    }

    suspend fun getAddress(): List<NewAddress> {
        return try {
            apiClient.getAddressList() ?: emptyList()
        } catch (e: Exception) {
            Timber.e("Error get address: $e")
            emptyList()
        }
    }

    suspend fun createOrderApp(order: Order): Int {
        return try {
            val productList = JsonArray()
            order.productList.forEach {
                productList.add(it)
            }
            val newOrder = JsonObject()
            newOrder.apply {
                addProperty("client_id", order.clientId)
                addProperty("date", order.date)
                addProperty("time_from", order.timeFrom)
                addProperty("time_to", order.timeTo)
                addProperty("notice", order.notice)
                addProperty("total_cost", order.totalCost)
                addProperty("payment_type", order.paymentType)
                addProperty("address_id", order.addressId)
                add("product_list", productList)
            }
            apiOrder.createOrder(newOrder)?.id ?: -1
        } catch (e: Exception) {
            Timber.e("error create order: $e")
            -1
        }
    }


    suspend fun createPay(amount: String, description: String, paymentToken: String, capture: Boolean): PaymentInfo? {
        return try {
            val payment = Payment(Amount(value = amount, currency = "RUB"), description, paymentToken, capture)
            yookassa.createPayment(paymentToken.removeRange(5..9), payment)
        } catch (e: Exception) {
            Timber.e("error create pay $e")
            null
        }
    }

    suspend fun getOrderPayStatus(idOrderPay: String): Boolean {
        return try {
            val messagePay = yookassa.getPayment(paymentId = idOrderPay)
            messagePay?.paid ?: false
        } catch (e: Exception) {
            Timber.d("error get pay order $e")
            false
        }
    }

    suspend fun setStatusPayment(orderId: Int, parameters: JsonObject): Boolean {
        return try {
            val answer = apiOrder.updatePayStatusInfo(orderId, parameters)
            answer.isSuccessful
        }catch (e: Exception) {
            Timber.e("error set payment status: $e")
            false
        }
    }

    suspend fun getOrder(orderId: Int): CreatedOrder? {
       return try {
           Timber.d("get ORDER, ${getAuthClient().clientId}")
            apiOrder.getCreatedOrder(orderId)
        } catch (e: Exception) {
            Timber.e("error get order: $e")
            null
        }
    }

    suspend fun getOrdersList(): List<CreatedOrder> {
        return try {
            apiOrder.getOrderClient() ?: emptyList()
        } catch (e: Exception) {
            Timber.e("error get ordersList: $e")
            emptyList()
        }
    }

    /**
     * получить информацю о клиенте
     */
    suspend fun getClientInfo(): Client? {
        return try {
            apiClient.getClient()
        } catch (e: Exception) {
            Timber.e("error get client: $e")
            null
        }
    }

    suspend fun deleteAccount(): DeleteMessage? {
        return try {
            apiClient.deleteAccount()
            null
        } catch (e: Exception) {
            Timber.e("Error delete account: $e")
            null
        }
    }

    suspend fun editUserData(client: ClientEditData): Boolean {
        try {
            val answer = apiClient.setDataClient(client)
            return answer.isSuccessful
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
            val active = apiClient.deleteAddress(id)
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
        newAddressParameters: AddressParameters
    ): NewAddress? {
        return try {
            apiClient.createNewAddress(newAddressParameters)
        } catch (e: Exception) {
            Timber.e("Error create address: $e")
            null
        }
    }

    suspend fun setMailing(isMailing: Boolean) {
        try {
            val mailing = JsonObject()
            mailing.addProperty("mailing_consent", isMailing)
            apiClient.mailing(mailing)
        } catch (e: Exception) {
            Timber.e("error set mailing: $e")
        }
    }

    fun deleteClient() {
//        val apiClient = ApiAuthClient.makeClientApi()
        authClient.remove()
//        return try {
//            val client = getAuthClient()
//            if (apiClient.logout(client.clientId, client.refreshToken)) {
//                authClient.remove()
//                true
//            } else {
//                false
//            }
//        } catch (e: Exception) {
//            false
//        }

    }

    fun getAuthClient(): AuthClient = authClient.get()
}