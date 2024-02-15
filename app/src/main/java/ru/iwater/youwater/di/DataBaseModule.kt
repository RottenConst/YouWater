package ru.iwater.youwater.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.iwater.youwater.bd.MIGRATION_1_2
import ru.iwater.youwater.bd.MIGRATION_2_3
import ru.iwater.youwater.bd.MIGRATION_3_4
import ru.iwater.youwater.bd.MIGRATION_4_5
import ru.iwater.youwater.bd.MIGRATION_5_6
import ru.iwater.youwater.bd.MIGRATION_6_7
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.di.components.OnApplication

@Module(includes = [ContextModule::class])
class DataBaseModule {

    @Provides
    @OnApplication
    fun provideDataBase(context: Context): YouWaterDB =
        Room.databaseBuilder(
            context.applicationContext,
            YouWaterDB::class.java,
            "database"
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addMigrations(MIGRATION_5_6)
            .addMigrations(MIGRATION_6_7)
//            .addMigrations(MIGRATION_7_8)
            .build()
}