package ru.iwater.youwater.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.iwater.youwater.data.Address
import ru.iwater.youwater.data.Product

@Database(version = 1, entities = [Product::class, Address::class])
abstract class YouWaterDB: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun addressDao(): AddressDao

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