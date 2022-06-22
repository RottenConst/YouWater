package ru.iwater.youwater.base

import android.app.Application
import com.pusher.pushnotifications.PushNotifications
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

    private val instanceID = "60f03696-9ffd-4b38-95ea-910ad9b90d3a"

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        initAppComponent()
        PushNotifications.start(applicationContext, instanceID)
        PushNotifications.addDeviceInterest("hello")
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