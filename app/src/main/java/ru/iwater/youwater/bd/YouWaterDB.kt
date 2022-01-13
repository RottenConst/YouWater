package ru.iwater.youwater.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.iwater.youwater.data.*
import ru.iwater.youwater.utils.ProductConverter

@Database(version = 1, entities = [Product::class, Address::class, FavoriteProduct::class, MyOrder::class, BankCard::class])
@TypeConverters(ProductConverter::class )
abstract class YouWaterDB: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun addressDao(): AddressDao
    abstract fun favoriteProductDao(): FavoriteProductDao
    abstract fun myOrderDao(): MyOrderDao
    abstract fun bankCard(): BankCardDao

    companion object {
        private var INSTANCE: YouWaterDB? = null

        fun getYouWaterDB(context: Context): YouWaterDB? {
            if (INSTANCE == null) {
                synchronized(YouWaterDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        YouWaterDB::class.java,
                        "database"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun getDestroyDataBase() {
            INSTANCE = null
        }
    }
}