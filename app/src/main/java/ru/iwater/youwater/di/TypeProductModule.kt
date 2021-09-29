package ru.iwater.youwater.di

import dagger.Module
import dagger.Provides
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.data.TypeProduct

@Module
class TypeProductModule {

    @Provides
    @OnScreen
    fun provideTypeProduct(): TypeProduct = TypeProduct("", "", 0, "", 0, 0)
}