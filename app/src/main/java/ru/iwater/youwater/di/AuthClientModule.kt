package ru.iwater.youwater.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.iwater.youwater.di.components.OnApplication
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.iteractor.StorageStateAuthClient

@Module(includes = [ContextModule::class])
class AuthClientModule {

    @Provides
    @OnApplication
    fun provideAccountStorage(context: Context): StorageStateAuthClient =
        ClientStorage(context)
}