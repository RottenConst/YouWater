package ru.iwater.youwater.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
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
        ).build()
}