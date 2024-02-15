package ru.iwater.youwater.bd

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.iwater.youwater.data.NewProduct
@Dao
interface NewProductDao {

    @Insert
    suspend fun save(newProduct: NewProduct)

    @Query("SELECT * FROM NewProduct")
    suspend fun getAllNewProduct(): List<NewProduct>?

    @Update
    suspend fun updateNewProductInBasked(newProduct: NewProduct)

    @Query("SELECT * FROM NewProduct WHERE id IS :id")
    suspend fun getProduct(id: Int): NewProduct?

    @Delete
    suspend fun delete(newProduct: NewProduct)
}