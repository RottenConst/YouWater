package ru.iwater.youwater.bd

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.iwater.youwater.data.*
import ru.iwater.youwater.utils.ProductConverter

@Database(
    version = 7,
    entities = [
        Product::class,
        Address::class,
        MyOrder::class
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
    abstract fun myOrderDao(): MyOrderDao

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


val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "DROP TABLE IF EXISTS BankCard"
        )
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE RawAddress ADD COLUMN notice TEXT"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE RawAddress ADD COLUMN active INTEGER"
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "DROP TABLE IF EXISTS FavoriteProduct"
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE RawAddress ADD COLUMN region TEXT"
        )
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "DROP TABLE IF EXISTS RawAddress"
        )
    }
}