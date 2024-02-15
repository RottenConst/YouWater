package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.NewProductDao
import ru.iwater.youwater.data.InfoProduct
import ru.iwater.youwater.data.Measure
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.network.ApiOrder
import timber.log.Timber

class InfoProductRepository (
    private val serviceOrder: ApiOrder,
    private val productDao: NewProductDao
) {

    /**
     * загрузить информацию о товаре по id
     * @param productId индификатор продукта
     */
    suspend fun getProduct(productId: Int): InfoProduct? {
        return try {
            serviceOrder.getAboutProduct(productId)
        } catch (e: Exception) {
            Timber.e("Exception get product $e")
            null
        }
    }

    suspend fun getMeasureList():List<Measure> {
        return try {
            serviceOrder.getMeasuresList()
        }catch (e: Exception) {
            Timber.e("Exeption get List measure $e")
            emptyList()
        }
    }

    suspend fun getProductFromDB(id: Int): NewProduct? {
        return productDao.getProduct(id)
    }

    suspend fun addProductInBasket(product: NewProduct) {
        productDao.save(product)
    }

    suspend fun updateNewProductInBasket(product: NewProduct) {
        productDao.updateNewProductInBasked(product)
    }

}