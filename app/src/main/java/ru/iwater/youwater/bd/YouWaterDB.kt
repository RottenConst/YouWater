package ru.iwater.youwater.bd

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.iwater.youwater.data.*
import ru.iwater.youwater.utils.ProductConverter

@Database(
    version = 2,
    entities = [
        Product::class,
        Address::class,
        FavoriteProduct::class,
        MyOrder::class,
        BankCard::class,
        RawAddress::class
    ],
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ],
    exportSchema = false
)
@TypeConverters(ProductConverter::class )
abstract class YouWaterDB: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun addressDao(): AddressDao
    abstract fun favoriteProductDao(): FavoriteProductDao
    abstract fun myOrderDao(): MyOrderDao
    abstract fun bankCard(): BankCardDao
    abstract fun rawAddressDao(): RawAddressDao

    companion object {
        private var INSTANCE: YouWaterDB? = null

        fun getYouWaterDB(context: Context): YouWaterDB? {
            if (INSTANCE == null) {
                synchronized(YouWaterDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        YouWaterDB::class.java,
                        "database"
                    )
                        .build()
                }
            }
            return INSTANCE
        }

        fun getDestroyDataBase() {
            INSTANCE = null
        }
    }
}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE RawAddress ADD COLUMN notice TEXT"
        )
    }
}