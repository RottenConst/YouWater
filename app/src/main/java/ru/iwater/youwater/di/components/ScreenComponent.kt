package ru.iwater.youwater.di.components

import dagger.Component
import ru.iwater.youwater.di.viewModel.ViewModelFactoryModule
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.StartActivity

@OnScreen
@Component(dependencies = [AppComponent::class], modules = [ViewModelFactoryModule::class])
interface ScreenComponent {
    fun clientStorage(): StorageStateAuthClient
    fun inject(startActivity: StartActivity)
    fun inject(mainActivity: MainActivity)
}