package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.bd.NewProductDao
import ru.iwater.youwater.data.CreatedOrder
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.payModule.yookassa.Amount
import ru.iwater.youwater.data.payModule.yookassa.Payment
import ru.iwater.youwater.data.payModule.yookassa.PaymentInfo
import ru.iwater.youwater.network.ApiOrder
import ru.iwater.youwater.network.ApiYookassa
import timber.log.Timber

class PaymentRepository(
    private val servicePayment: ApiYookassa,
    private val serviceOrder: ApiOrder,
    private val productDao: NewProductDao
) {

    /**
     * получить список продуктов добавленых в корзину
     */
    suspend fun getProductListOfCategory(): List<NewProduct> {
        return productDao.getAllNewProduct() ?: emptyList()
    }

    suspend fun deleteProductFromBasket(product: NewProduct) {
        productDao.delete(product)
    }

    suspend fun createPay(amount: String, description: String, paymentToken: String, capture: Boolean): PaymentInfo? {
        return try {
            val payment = Payment(Amount(value = amount, currency = "RUB"), description, paymentToken, capture)
            servicePayment.createPayment(paymentToken.removeRange(5..9), payment)
        } catch (e: Exception) {
            Timber.e("error create pay $e")
            null
        }
    }

    suspend fun getOrder(orderId: Int): CreatedOrder? {
        return try {
            serviceOrder.getCreatedOrder(orderId)
        } catch (e: Exception) {
            Timber.e("error get order: $e")
            null
        }
    }

    /**
     * загрузить информацию о товаре по id
     */
    suspend fun getNewProduct(productId: Int): NewProduct? {
        return try {
            val infoProduct = serviceOrder.getAboutProduct(productId)
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

    suspend fun getOrderPayStatus(idOrderPay: String): Boolean {
        return try {
            val messagePay = servicePayment.getPayment(paymentId = idOrderPay)
            messagePay?.paid ?: false
        } catch (e: Exception) {
            Timber.d("error get pay order $e")
            false
        }
    }

    suspend fun setStatusPayment(orderId: Int, parameters: JsonObject): Boolean {
        return try {
            val answer = serviceOrder.updatePayStatusInfo(orderId, parameters)
            answer.isSuccessful
        }catch (e: Exception) {
            Timber.e("error set payment status: $e")
            false
        }
    }
}