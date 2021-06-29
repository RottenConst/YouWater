package ru.iwater.youwater.di.components

import android.content.Context
import dagger.Component
import ru.iwater.youwater.di.ContextModule

@OnApplication
@Component(modules = [ContextModule::class])
interface AppComponent {
    fun appContext(): Context
}