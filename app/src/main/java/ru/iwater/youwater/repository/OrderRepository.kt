package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.AddressDao
import ru.iwater.youwater.bd.MyOrderDao
import ru.iwater.youwater.bd.ProductDao
import ru.iwater.youwater.bd.YouWaterDB
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
    private val addressDao: AddressDao = youWaterDB.addressDao()
    private val productDao: ProductDao = youWaterDB.productDao()
    private val myOrderDao: MyOrderDao = youWaterDB.myOrderDao()

    suspend fun getClientInfo(clientId: Int): Client? {
        try {
            val client = apiAuth.getClientDetail(clientId)
            if (client != null) {
                return client
            }
        }catch (e: Exception) {
            Timber.e("error get client: $e")
        }
        return null
    }

    fun getAuthClient(): AuthClient = authClient.get()

    suspend fun getAllAddress(): Address? {
        val address = addressDao.getAllAddresses()
        return if (address.isNullOrEmpty()) {
            null
        } else address.last()
    }

    suspend fun getAllProduct(): List<Product> {
        return productDao.getAllProduct() ?: emptyList()
    }

    suspend fun deleteProduct(product: Product) {
        productDao.delete(product)
    }

    suspend fun saveMyOrder(myOrder: MyOrder) {
        myOrderDao.save(myOrder)
    }

    suspend fun getMyOrder(): List<MyOrder>? {
        return myOrderDao.getAllMyOrder()
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

    suspend fun payCard(paymentCard: PaymentCard): String {
        try {
            val answer = sberApi.registerOrder(
                userName = paymentCard.userName,
                password = paymentCard.password,
                orderNumber = paymentCard.orderNumber,
                amount = paymentCard.amount,
                returnUrl = paymentCard.returnUrl,
                pageView = "MOBILE"
            )
            return if (answer != null) {
                answer["formUrl"].toString()
            } else "qwerty"
        } catch (e: Exception) {
            Timber.e("error pay: $e")
        }
        return "error"
    }
}