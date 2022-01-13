package ru.iwater.youwater.bd

import androidx.room.*
import ru.iwater.youwater.data.MyOrder
import ru.iwater.youwater.data.Product

@Dao
interface MyOrderDao {

    @Insert
    suspend fun save(myOrder: MyOrder)

    @Query("SELECT * FROM MyOrder")
    suspend fun getAllMyOrder(): List<MyOrder>?

    @Update
    suspend fun updateOrder(myOrder: MyOrder)

    @Query("SELECT * FROM MyOrder WHERE id IS :id")
    suspend fun getMyOrder(id: Int): MyOrder?

    @Delete
    suspend fun delete(myOrder: MyOrder)
}