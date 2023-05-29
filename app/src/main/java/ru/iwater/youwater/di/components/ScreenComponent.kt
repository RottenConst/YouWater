package ru.iwater.youwater.di.components

import dagger.Component
import ru.iwater.youwater.di.viewModel.ViewModelFactoryModule
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.screen.basket.BasketFragment
import ru.iwater.youwater.screen.basket.CardPaymentFragment
import ru.iwater.youwater.screen.basket.CompleteOrderFragment
import ru.iwater.youwater.screen.basket.CreateOrderFragment
import ru.iwater.youwater.screen.catalog.CatalogFragment
import ru.iwater.youwater.screen.catalog.CatalogProductFragment
import ru.iwater.youwater.screen.home.AboutProductFragment
import ru.iwater.youwater.screen.home.HomeFragment
import ru.iwater.youwater.screen.login.EnterPinCodeFragment
import ru.iwater.youwater.screen.login.LoginFragment
import ru.iwater.youwater.screen.login.RegisterFragment
import ru.iwater.youwater.screen.login.StartFragment
import ru.iwater.youwater.screen.profile.*

@OnScreen
@Component(dependencies = [AppComponent::class], modules = [ViewModelFactoryModule::class])
interface ScreenComponent {
    fun clientStorage(): StorageStateAuthClient
    fun inject(homeFragment: HomeFragment)
    fun inject(catalogFragment: CatalogFragment)
    fun inject(catalogProductFragment: CatalogProductFragment)
    fun inject(loginFragment: LoginFragment)
    fun inject(enterPinCodeFragment: EnterPinCodeFragment)
    fun inject(registerFragment: RegisterFragment)
    fun inject(startFragment: StartFragment)
    fun inject(startActivity: StartActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(profileFragment: ProfileFragment)
    fun inject(userDataFragment: UserDataFragment)
    fun inject(aboutProductFragment: AboutProductFragment)
    fun inject(basketFragment: BasketFragment)
    fun inject(addressesFragment: AddressesFragment)
    fun inject(addAddressFragment: AddAddressFragment)
    fun inject(favoriteFragment: FavoriteFragment)
    fun inject(createOrderFragment: CreateOrderFragment)
    fun inject(myOrdersFragment: MyOrdersFragment)
    fun inject(editUserDataFragment: EditUserDataFragment)
    fun inject(cardPaymentFragment: CardPaymentFragment)
    fun inject(completeOrderFragment: CompleteOrderFragment)
    fun inject(notificationFragment: NotificationFragment)
}