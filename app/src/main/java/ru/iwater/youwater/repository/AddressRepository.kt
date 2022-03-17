package ru.iwater.youwater.repository

import retrofit2.Retrofit
import ru.iwater.youwater.bd.AddressDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.Address
import ru.iwater.youwater.data.AddressResult
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.GoogleMapApi
import ru.iwater.youwater.network.RetrofitFactory
import ru.iwater.youwater.network.RetrofitGoogleService
import timber.log.Timber
import javax.inject.Inject

class AddressRepository @Inject constructor(
    youWaterDB: YouWaterDB,
    private val authClient: StorageStateAuthClient
) {
    private val addressDao: AddressDao = youWaterDB.addressDao()
    private val waterApi: ApiWater = RetrofitFactory.makeRetrofit()
    private val googleApi: GoogleMapApi = RetrofitGoogleService.makeRetrofit()

    suspend fun getAddressList(): List<Address>? {
        return addressDao.getAllAddresses()
    }

    suspend fun saveAddress(address: Address) {
        addressDao.save(address)
    }

    suspend fun deleteAddress(address: Address) {
        addressDao.delete(address)
    }

    suspend fun getStreetOnCoordinate(placeId: String, googleKey: String): AddressResult? {
        try {
            val answer = googleApi.getAddressOnPlaceId(placeId, "ru", googleKey)
            if (answer != null) {
                return answer
            }
        }catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    suspend fun getInfoOnAddress(address: String, googleKey: String): AddressResult? {
        try {
            val addressResult = googleApi.getCoordinateOnAddress(address, "ru", googleKey)
            if (addressResult != null) {
                return addressResult
            }
        }catch (e: java.lang.Exception) {
            Timber.e(e)
        }
        return null
    }

    suspend fun getAllFactAddress(): List<String> {
        return try {
            val jsonAddress = waterApi.getAllAddresses(authClient.get().clientId)
            if (jsonAddress.isSuccessful) {
                val listAddress = mutableListOf<String>()
                jsonAddress.body()?.forEach {
                    listAddress.add(it["full_address"].toString())
                    listAddress.add(it["fact_address"].toString())
                }
                listAddress.toList()
            } else emptyList()
        } catch (e: Exception) {
            Timber.e("Error get address $e")
            emptyList()
        }
    }
}