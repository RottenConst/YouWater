package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.NewProductDao
import ru.iwater.youwater.data.Banner
import ru.iwater.youwater.data.Category
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.network.ApiOrder
import timber.log.Timber

class HomeRepository (
    private val serviceOrder: ApiOrder,
    private val productDao: NewProductDao
) {

//    val productDao: NewProductDao = YouWaterDB.getYouWaterDB(aplication)

    /**
     * Получить список банеров по акциям
     *
     * @return список банеров
     */
    suspend fun getPromoBanners(): List<Banner> {
        return try {
            val promoBanners = serviceOrder.getPromo()
            promoBanners.banners.ifEmpty { emptyList() }
        }catch (e: Exception) {
            Timber.e("Error get promo banner: $e")
            emptyList()
        }
    }
    suspend fun getLastOrder(): Int? {
        return try {
            val listOrders = serviceOrder.getOrderClient()
            if (!listOrders.isNullOrEmpty()) {
                val order = listOrders[0]
                order.id
            } else null
        }catch (e:Exception) {
            Timber.d("Error get last order: $e")
            null
        }
    }

    suspend fun isStartPocket(): Boolean {
        return try {
            serviceOrder.isStartPocket()?.status ?: false
        } catch (e: Exception) {
            Timber.e("Error get start pocket: $e")
            false
        }
    }

    suspend fun getCategoryListName(isStartPocket: Boolean): List<Category> {
        return try {
            val category = serviceOrder.getCategoryList1() ?: emptyList()
            if (category.isNotEmpty()) {
                Timber.d("Category is not empty")
                return if (isStartPocket) {
                    category.filter { it.visibleApp }
                } else {
                    category.filter { it.id != 20 && it.visibleApp }
                }
            }
            Timber.d("Category is empty")
            emptyList()
        } catch (e: Exception) {
            Timber.e("Error get catalog list: $e")
            emptyList()
        }
    }

    suspend fun getProductList(): List<NewProduct> {
        return try {
            val productList = serviceOrder.getProductList()
            productList.ifEmpty { emptyList() }
        } catch (e: Exception) {
            Timber.e("Error get product list: $e")
            emptyList()
        }
    }

    suspend fun getProductByCategory(categoryId: Int): List<NewProduct> {
        return try {
            serviceOrder.getProductByCategory(categoryId) ?: emptyList()
        } catch (e: Exception) {
            Timber.e("error get product by category $e")
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