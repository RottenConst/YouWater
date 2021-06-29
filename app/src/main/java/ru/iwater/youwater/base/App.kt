package ru.iwater.youwater.base

import android.app.Application
import ru.iwater.youwater.di.ContextModule
import ru.iwater.youwater.di.components.AppComponent
import ru.iwater.youwater.di.components.DaggerAppComponent
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
            .build()
    }
}