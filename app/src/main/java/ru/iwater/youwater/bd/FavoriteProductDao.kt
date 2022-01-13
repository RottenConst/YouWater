package ru.iwater.youwater.bd

import androidx.room.*
import ru.iwater.youwater.data.FavoriteProduct

@Dao
interface FavoriteProductDao {
    @Insert
    suspend fun save(favoriteProduct: FavoriteProduct)

    @Query("SELECT * FROM FavoriteProduct")
    suspend fun getAllProduct(): List<FavoriteProduct>?

    @Update
    suspend fun updateProductInBasked(favoriteProduct: FavoriteProduct)

    @Query("SELECT * FROM FavoriteProduct WHERE id IS :id")
    suspend fun getFavoriteProduct(id: Int): FavoriteProduct?

    @Delete
    suspend fun delete(favoriteProduct: FavoriteProduct)
}