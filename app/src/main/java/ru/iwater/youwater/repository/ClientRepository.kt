package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.bd.NewProductDao
import ru.iwater.youwater.data.AddressParameters
import ru.iwater.youwater.data.Client
import ru.iwater.youwater.data.ClientEditData
import ru.iwater.youwater.data.DeleteMessage
import ru.iwater.youwater.data.DeliverySchedule
import ru.iwater.youwater.data.Favorite
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.network.ApiClient
import timber.log.Timber

class ClientRepository(
    private val serviceClient: ApiClient,
    private val productDao: NewProductDao
) {

    /**
     * получить информацю о клиенте
     */
    suspend fun getClientInfo(): Client? {
        return try {
            serviceClient.getClient()
        } catch (e: Exception) {
            Timber.e("error get client: $e")
            null
        }
    }

    suspend fun editUserData(client: ClientEditData): Boolean {
        try {
            val answer = serviceClient.setDataClient(client)
            return answer.isSuccessful
        } catch (e: java.lang.Exception) {
            Timber.e("error edit user data: $e")
        }
        return false
    }

    suspend fun logoutClient(clientId: Int, refreshToken: String): Boolean {
        return try {
            val clientInfo = JsonObject()
            clientInfo.addProperty("client_id", clientId)
            clientInfo.addProperty("refresh_token", refreshToken)
            val status = serviceClient.logout(clientInfo).status
            Timber.d("Status $status")
            status
        } catch (e: Exception) {
            Timber.e("Error logout $e")
            false
        }
    }

    suspend fun deleteAccount(): DeleteMessage? {
        return try {
            serviceClient.deleteAccount()
            null
        } catch (e: Exception) {
            Timber.e("Error delete account: $e")
            null
        }
    }

    suspend fun setMailing(isMailing: Boolean) {
        try {
            val mailing = JsonObject()
            mailing.addProperty("mailing_consent", isMailing)
            serviceClient.mailing(mailing)
        } catch (e: Exception) {
            Timber.e("error set mailing: $e")
        }
    }

    suspend fun getFavorite(): Favorite? {
        return try {
            serviceClient.getFavoriteList()
        } catch (e: Exception) {
            Timber.e("get favorite error: $e")
            null
        }
    }

    suspend fun addToFavoriteProduct(productId: Int): Boolean {
        return try {
            val favourite = JsonObject()
            favourite.addProperty("product_id", productId)
            serviceClient.addFavoriteProduct(favourite)?.status ?: false
        } catch (e: Exception) {
            Timber.e("add to favorite error: $e")
            false
        }
    }

    suspend fun deleteFavorite(productId: Int): Boolean {
        return try {
            val favourite = JsonObject()
            favourite.addProperty("product_id", productId)
            serviceClient.deleteFavoriteProduct(favourite)?.status ?: false
        } catch (e: Exception) {
            Timber.e("delete favorite product error: $e")
            false
        }
    }

    suspend fun getAddress(): List<NewAddress> {
        return try {
            serviceClient.getAddressList() ?: emptyList()
        } catch (e: Exception) {
            Timber.e("Error get address: $e")
            emptyList()
        }
    }

    suspend fun inactiveAddress(id: Int): Boolean {
        return try {
            val active = serviceClient.deleteAddress(id)
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
            serviceClient.createNewAddress(newAddressParameters)
        } catch (e: Exception) {
            Timber.e("Error create address: $e")
            null
        }
    }

    suspend fun getDelivery(address: NewAddress): DeliverySchedule {
        return try {
            serviceClient.getDeliverySchedule(address.getDeliveryProperty())
        } catch (e: Exception) {
            Timber.d("Error getDelivery: $e")
            DeliverySchedule(
                common = emptyList(),
                exceptions = emptyList()
            )
        }
    }

    suspend fun getProductSizeInBasket(): Int {
        return productDao.getAllNewProduct()?.size ?: 0
    }

    suspend fun getProductsInBasket(): List<NewProduct> {
        return productDao.getAllNewProduct() ?: emptyList()
    }

    suspend fun getProductFromDB(id: Int): NewProduct? {
        return productDao.getProduct(id)
    }

    suspend fun deleteProductFromBasket(product: NewProduct) {
        productDao.delete(product)
    }

    suspend fun addProductInBasket(product: NewProduct) {
        productDao.save(product)
    }

    suspend fun updateNewProductInBasket(product: NewProduct) {
        productDao.updateNewProductInBasked(product)
    }

}