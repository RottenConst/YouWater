package ru.iwater.youwater.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.iwater.youwater.di.components.OnApplication

@Module
class ContextModule(val context: Context) {

    @Provides
    @OnApplication
    fun provideContext(): Context = context.applicationContext
}