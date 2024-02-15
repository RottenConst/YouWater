package ru.iwater.youwater.bd

import androidx.room.*
import ru.iwater.youwater.data.Product

@Dao
interface ProductDao {
    @Insert
    suspend fun save(product: Product)

    @Query("SELECT * FROM Product")
    suspend fun getAllProduct(): List<Product>?

    @Update
    suspend fun updateProductInBasked(product: Product)

    @Query("SELECT * FROM Product WHERE id IS :id")
    suspend fun getProduct(id: Int): Product?

    @Delete
    suspend fun delete(product: Product)
}