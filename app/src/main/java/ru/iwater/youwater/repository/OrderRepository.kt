package ru.iwater.youwater.repository

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ru.iwater.youwater.bd.NewProductDao
import ru.iwater.youwater.data.CreatedOrder
import ru.iwater.youwater.data.DeliverySchedule
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Order
import ru.iwater.youwater.network.ApiClient
import ru.iwater.youwater.network.ApiOrder
import timber.log.Timber

class OrderRepository(
    private val serviceOrder: ApiOrder,
    private val serviceClient: ApiClient,
    private val productDao: NewProductDao
) {

    /**
     * получить список продуктов добавленых в корзину
     */
    suspend fun getProductListOfCategory(): List<NewProduct> {
        return productDao.getAllNewProduct() ?: emptyList()
    }

    suspend fun updateNewProductInBasket(product: NewProduct) {
        productDao.updateNewProductInBasked(product)
    }

    suspend fun deleteProductFromBasket(product: NewProduct) {
        productDao.delete(product)
    }

    suspend fun getOrdersList(): List<CreatedOrder> {
        return try {
            serviceOrder.getOrderClient() ?: emptyList()
        } catch (e: Exception) {
            Timber.e("error get ordersList: $e")
            emptyList()
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
            serviceOrder.createOrder(newOrder)?.id ?: -1
        } catch (e: Exception) {
            Timber.e("error create order: $e")
            -1
        }
    }

    suspend fun getDelivery(address: NewAddress): DeliverySchedule? {
        return try {
            serviceClient.getDeliverySchedule(address.getDeliveryProperty())
        } catch (e: Exception) {
            Timber.d("Error getDelivery: $e")
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
}