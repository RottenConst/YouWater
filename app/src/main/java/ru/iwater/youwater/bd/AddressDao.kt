package ru.iwater.youwater.bd

import androidx.room.*
import ru.iwater.youwater.data.Address

@Dao
interface AddressDao {
    @Insert
    suspend fun save(address: Address)

    @Query("SELECT * FROM Address")
    suspend fun getAllAddresses(): List<Address>?

    @Update
    suspend fun updateAddress(address: Address)

    @Query("SELECT * FROM Address WHERE id IS :id")
    suspend fun getAddress(id: Int): Address?

    @Delete
    suspend fun delete(address: Address)
}