package ru.iwater.youwater.bd

import androidx.room.*
import ru.iwater.youwater.data.RawAddress

@Dao
interface RawAddressDao {

    @Insert
    suspend fun save(rawAddress: RawAddress)

    @Query("SELECT * FROM RawAddress")
    suspend fun getAddresses(): List<RawAddress>?

    @Update
    suspend fun updateAddress(rawAddress: RawAddress)

    @Query("SELECT * FROM RawAddress WHERE id is :id")
    suspend fun getAddress(id: Int): RawAddress?

    @Delete
    suspend fun deleteAddress(rawAddress: RawAddress)
}