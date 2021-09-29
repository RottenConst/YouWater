package ru.iwater.youwater.di.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.data.CatalogListViewModel
import ru.iwater.youwater.data.ProductListViewModel

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
}