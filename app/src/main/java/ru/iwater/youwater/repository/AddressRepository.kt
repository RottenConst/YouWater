package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.AddressDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.Address
import javax.inject.Inject

class AddressRepository @Inject constructor(
    youWaterDB: YouWaterDB
) {
    private val addressDao: AddressDao = youWaterDB.addressDao()

    suspend fun getAddressList(): List<Address>? {
        return addressDao.getAllAddresses()
    }

    suspend fun saveAddress(address: Address) {
        addressDao.save(address)
    }

    suspend fun deleteAddress(address: Address) {
        addressDao.delete(address)
    }
}