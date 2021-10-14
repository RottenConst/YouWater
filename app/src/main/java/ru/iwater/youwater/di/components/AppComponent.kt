package ru.iwater.youwater.di.components

import android.content.Context
import dagger.Component
import ru.iwater.youwater.di.AuthClientModule
import ru.iwater.youwater.di.ContextModule
import ru.iwater.youwater.di.viewModel.SharedPreferencesModule
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.iteractor.StorageStateAuthClient

@OnApplication
@Component(modules = [ContextModule::class, AuthClientModule::class, SharedPreferencesModule::class])
interface AppComponent {
    fun appContext(): Context
    fun clientStorage(): StorageStateAuthClient
}