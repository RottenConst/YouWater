package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.NewProductDao
import ru.iwater.youwater.data.NewProduct

class BasketRepository(
    private val productDao: NewProductDao
) {

    /**
     * получить список продуктов добавленых в корзину
     */
    suspend fun getProductListOfCategory(): List<NewProduct> {
        return productDao.getAllNewProduct() ?: emptyList()
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
}