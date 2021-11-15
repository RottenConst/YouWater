package ru.iwater.youwater.di.components

import dagger.Component
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.di.viewModel.ViewModelFactoryModule
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.screen.basket.BasketFragment
import ru.iwater.youwater.screen.catalog.CatalogFragment
import ru.iwater.youwater.screen.catalog.CatalogProductFragment
import ru.iwater.youwater.screen.home.AboutProductFragment
import ru.iwater.youwater.screen.home.HomeFragment
import ru.iwater.youwater.screen.login.LoginFragment
import ru.iwater.youwater.screen.login.StartFragment
import ru.iwater.youwater.screen.profile.ProfileFragment
import ru.iwater.youwater.screen.profile.UserDataFragment

@OnScreen
@Component(dependencies = [AppComponent::class], modules = [ViewModelFactoryModule::class])
interface ScreenComponent {
    fun clientStorage(): StorageStateAuthClient
    fun inject(homeFragment: HomeFragment)
    fun inject(catalogFragment: CatalogFragment)
    fun inject(catalogProductFragment: CatalogProductFragment)
    fun inject(loginFragment: LoginFragment)
    fun inject(startFragment: StartFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(userDataFragment: UserDataFragment)
    fun inject(aboutProductFragment: AboutProductFragment)
    fun inject(basketFragment: BasketFragment)
}