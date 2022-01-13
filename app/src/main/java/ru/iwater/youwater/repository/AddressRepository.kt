package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.AddressDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.Address
import ru.iwater.youwater.data.AddressResult
import ru.iwater.youwater.network.GoogleMapApi
import ru.iwater.youwater.network.RetrofitGoogleService
import timber.log.Timber
import javax.inject.Inject

class AddressRepository @Inject constructor(
    youWaterDB: YouWaterDB
) {
    private val addressDao: AddressDao = youWaterDB.addressDao()
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
}