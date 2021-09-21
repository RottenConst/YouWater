package ru.iwater.youwater.di.components

import dagger.Component
import ru.iwater.youwater.di.viewModel.ViewModelFactoryModule
import ru.iwater.youwater.screen.catalog.CatalogFragment
import ru.iwater.youwater.screen.home.HomeFragment

@OnScreen
@Component(dependencies = [AppComponent::class], modules = [ViewModelFactoryModule::class])
interface ScreenComponent {
    fun inject(homeFragment: HomeFragment)
    fun inject(catalogFragment: CatalogFragment)
}