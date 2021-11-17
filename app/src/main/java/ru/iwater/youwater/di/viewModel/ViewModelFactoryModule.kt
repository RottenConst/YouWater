package ru.iwater.youwater.di.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.iwater.youwater.data.*
import ru.iwater.youwater.di.components.OnScreen

@Module
abstract class ViewModelFactoryModule {

    @Binds
    @OnScreen
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CatalogListViewModel::class)
    abstract fun bindCatalogListViewModel(catalogListViewModel: CatalogListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProductListViewModel::class)
    abstract fun bindProductListViewModel(productListViewModel: ProductListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ClientProfileViewModel::class)
    abstract fun bindClientProfileViewModel(clientProfileViewModel: ClientProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutProductViewModel::class)
    abstract fun bindingAboutProductViewModel(aboutProductViewModel: AboutProductViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddressViewModel::class)
    abstract fun bindingAddressViewModel(addressViewModel: AddressViewModel): ViewModel
}