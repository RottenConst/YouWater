package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.bd.*
import ru.iwater.youwater.data.*
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import ru.iwater.youwater.network.RetrofitSberApi
import ru.iwater.youwater.network.SberPaymentApi
import timber.log.Timber
import javax.inject.Inject

class OrderRepository @Inject constructor(
    youWaterDB: YouWaterDB,
    private val authClient: StorageStateAuthClient,
) {
    private val apiAuth: ApiWater = RetrofitFactory.makeRetrofit()
    private val sberApi: SberPaymentApi = RetrofitSberApi.makeRetrofit()
    private val addressDao: RawAddressDao = youWaterDB.rawAddressDao()
    private val productDao: ProductDao = youWaterDB.productDao()
    private val myOrderDao: MyOrderDao = youWaterDB.myOrderDao()

    suspend fun getClientInfo(clientId: Int): Client? {
        try {
            val client = apiAuth.getClientDetail(clientId)
            if (client != null) {
                return client
            }
        } catch (e: Exception) {
            Timber.e("error get client: $e")
        }
        return null
    }

    fun getAuthClient(): AuthClient = authClient.get()

    // получить все адреса из бд
    suspend fun getAllAddress(): List<RawAddress> {
        val address = addressDao.getAddresses()
        return if (address.isNullOrEmpty()) emptyList() else address
    }

    suspend fun getAddress(id: Int): RawAddress? {
        return addressDao.getAddress(id)
    }

    suspend fun saveAddress(rawAddress: RawAddress) {
        addressDao.save(rawAddress)
    }

    suspend fun getAllFactAddress(): List<RawAddress> {
        return try {
            val rawAddress = apiAuth.getAllAddresses(authClient.get().clientId)
            if (!rawAddress.isNullOrEmpty()) {
                rawAddress
            } else emptyList()
        } catch (e: Exception) {
            Timber.e("Error get address $e")
            emptyList()
        }
    }

    suspend fun getAllProduct(): List<Product> {
        return productDao.getAllProduct() ?: emptyList()
    }

    suspend fun getProduct(productId: Int): Product? {
        try {
            val product = apiAuth.getProductList()
            if (!product.isNullOrEmpty())
                return product.filter { it.id == productId }[0]
        } catch (e: java.lang.Exception) {
            Timber.e("Exception get product $e")
        }
        return null
    }

    suspend fun deleteProduct(product: Product) {
        productDao.delete(product)
    }

    suspend fun getAllOrder(clientId: Int): List<OrderFromCRM> {
        try {
            val listOrder = apiAuth.getOrderClient(clientId)
            return if (listOrder.isNullOrEmpty()) {
                emptyList()
            } else listOrder.take(15)
        } catch (e: Exception) {
            Timber.e("error get order: $e")
        }
        return emptyList()
    }

    suspend fun getStatusOrder(orderId: Int): Int? {
        return try {
            val statusOrder = apiAuth.getStatusOrder(orderId)
            if (statusOrder.isSuccessful) {
                Timber.d("Status order = ${statusOrder.body()?.get(0)?.get("status")}")
                statusOrder.body()?.get(0)?.get("status")?.asInt
            } else {
                0
            }
        }catch (e: Exception) {
            Timber.e("error get status order $e")
            0
        }
    }

    suspend fun saveMyOrder(myOrder: MyOrder) {
        myOrderDao.save(myOrder)
    }

    suspend fun getOrder(clientId: Int, orderId: Int): List<OrderFromCRM> {
        try {
            val listOrder = apiAuth.getOrderClient(clientId)
            return if (listOrder.isNullOrEmpty()) {
                emptyList()
            } else {
                listOrder.filter { it.id == orderId }
            }
        } catch (e: Exception) {
            Timber.e("error get order: $e")
        }
        return emptyList()
    }

    suspend fun getMyAllOrder(): List<MyOrder>? {
        return myOrderDao.getAllMyOrder()
    }

    suspend fun getMyOrder(id: Int): List<MyOrder?> {
        return listOf(myOrderDao.getMyOrder(id))
    }

    suspend fun createOrder(order: Order): String {
        try {
            val orderCreate = apiAuth.createOrder(order)
            return if (orderCreate.isSuccessful) {
                orderCreate.body().toString()
            } else "error"
        } catch (e: Exception) {
            Timber.e("error create order: $e")
        }
        return "error"
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
            return if (answer != null) {
                dataPayment.add(answer["orderId"].toString())
                dataPayment.add(answer["formUrl"].toString())
                dataPayment
            } else emptyList()
        } catch (e: Exception) {
            Timber.e("error pay: $e")
        }
        return emptyList()
    }

    suspend fun getPaymentStatus(orderId: String): Pair<Int, Int> {
        try {
//            val answer = sberApi.getOrderStatus("t602720481107-api", "ZwUEyuso", orderId) //test
            val answer = sberApi.getOrderStatus("p602720481107-api", "r6tMp1y78", orderId) //prod
            if (answer != null) {
                return Pair(
                    answer["orderNumber"].toString().removePrefix("\"").removeSuffix("\"").toInt(),
                    answer["orderStatus"].toString().toInt()
                )
            }
        }catch (e: Exception) {
            Timber.e("error get status pay: $e")
        }
        return Pair(0, 0)
    }

    suspend fun setStatusPayment(orderId: Int, parameters: JsonObject): Boolean {
        try {
            val answer = apiAuth.setStatusPayment(orderId, parameters)
            if (answer.isSuccessful) {
                Timber.d(answer.body()?.get("ready").toString())
                return true
            }
        }catch (e: Exception) {
            Timber.e("error set payment status: $e")
        }
        return false
    }
}