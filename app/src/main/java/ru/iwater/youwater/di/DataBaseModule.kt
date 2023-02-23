package ru.iwater.youwater.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.iwater.youwater.bd.*
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
            .build()
}