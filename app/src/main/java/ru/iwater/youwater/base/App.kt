package ru.iwater.youwater.base

import android.app.Application
import ru.iwater.youwater.di.ContextModule
import ru.iwater.youwater.di.DataBaseModule
import ru.iwater.youwater.di.components.AppComponent
import ru.iwater.youwater.di.components.DaggerAppComponent
import ru.iwater.youwater.di.components.DaggerScreenComponent
import ru.iwater.youwater.di.components.ScreenComponent
import timber.log.Timber

class App() : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        initAppComponent()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .dataBaseModule(DataBaseModule())
            .build()
    }

    fun buildScreenComponent(): ScreenComponent {
        return DaggerScreenComponent.builder()
            .appComponent(appComponent)
            .build()
    }
}