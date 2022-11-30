package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.bd.RawAddressDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.*
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject

/**
 * репоситорий адресов
 */
class AddressRepository @Inject constructor(
    youWaterDB: YouWaterDB,
    private val authClient: StorageStateAuthClient
) {
    private val addressDao: RawAddressDao = youWaterDB.rawAddressDao()
    private val waterApi: ApiWater = RetrofitFactory.makeRetrofit()

    /**
     * получить все сохраненные адреса
     */
    suspend fun getAddressList(): List<RawAddress> {
        val rawAddress = addressDao.getAddresses()
        return if (rawAddress.isNullOrEmpty()) emptyList() else rawAddress
    }

    /**
     * получить адрес
     */
    suspend fun getAddress(id: Int): RawAddress? {
        return addressDao.getAddress(id)
    }

    /**
     * сохранить адрес
     */
    suspend fun saveAddress(rawAddress: RawAddress) {
        addressDao.save(rawAddress)
    }

    /**
     *  удалить адрес
     */
    suspend fun deleteAddress(rawAddress: RawAddress) {
        addressDao.deleteAddress(rawAddress)
    }

    /**
     * послать запрос на деактивироватцию адреса
     */
    suspend fun inactiveAddress(id: Int): Boolean {
        return try {
            val active = waterApi.deleteAddress(id)
            Timber.d("DELETE ADDRESS Message: ${active.body()}")
            active.isSuccessful
        } catch (e: Exception) {
            Timber.e("delete address error $e")
            false
        }
    }

    suspend fun getAllFactAddress(): List<RawAddress> {
        return try {
            val rawAddress = waterApi.getAllAddresses(authClient.get().clientId)
            return rawAddress.ifEmpty { emptyList() }
        } catch (e: Exception) {
            Timber.e("Error get address $e")
            emptyList()
        }
    }

    suspend fun getFactAddress(addressId: Int): RawAddress? {
        return try {
            val rawAddress = waterApi.getAddress(addressId)
            rawAddress
        } catch (e: Exception) {
            Timber.e("Error get address $e")
            null
        }
    }

    /**
     * отправить запрос на создание адреса
     */
    suspend fun createAddress(
        clientId: Int,
        contact: String,
        region: String,
        factAddress: String,
        address: String,
        coords: String,
        fullAddress: String,
        returnTare: Int,
        phoneContact: String,
        nameContact: String,
        addressJson: JsonObject,
        notice: String
    ): String? {
        return try {
            val response = waterApi.createNewAddress(
                clientId,
                contact,
                region,
                factAddress,
                address,
                coords,
                1,
                fullAddress,
                returnTare,
                phoneContact,
                nameContact,
                addressJson,
                notice
            )
            if (response.isSuccessful) {
                response.body()?.get("message")?.asString
            } else null
        } catch (e: Exception) {
            Timber.e("Error create address: $e")
            null
        }
    }

    /**
     * получить информацию о авторизированном клиенте
     */
    suspend fun getClientInfo(): Client? {
        try {
            val client = waterApi.getClientDetail(authClient.get().clientId)
            if (client != null) {
                return client
            }
        }catch (e: Exception) {
            Timber.e("error get client: $e")
        }
        return null
    }
}