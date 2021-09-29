package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import javax.inject.Inject

@OnScreen
class CatalogListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
) : ViewModel() {

    private val catalogs = mutableListOf<TypeProduct>()

    private val _catalogProductMap: MutableLiveData<Map<TypeProduct, List<Product>>> =
        MutableLiveData()
    val catalogProductMap: LiveData<Map<TypeProduct, List<Product>>>
        get() = _catalogProductMap

    private val _catalogList: MutableLiveData<List<TypeProduct>> = MutableLiveData()
    val catalogList: LiveData<List<TypeProduct>> get() = _catalogList

    private val _productLiveData: MutableLiveData<List<Product>> = MutableLiveData()
    val productLiveData: LiveData<List<Product>> get() = _productLiveData

    private val _navigateToSelectCategory: MutableLiveData<TypeProduct> = MutableLiveData()
    val navigateToSelectCategory: LiveData<TypeProduct>
        get() = _navigateToSelectCategory

    init {
        viewModelScope.launch {
            catalogs.addAll(productRepo.getCategoryList())
            _catalogList.value = catalogs
            getAllProducts(catalogs)
        }
    }

    private suspend fun getAllProducts(catalogs: List<TypeProduct>) {
        val catalogMap = mutableMapOf<TypeProduct, List<Product>>()
        catalogs.forEach {
            val products = productRepo.getProductList(it.id)
            catalogMap[it] = products
        }
        _catalogProductMap.value = catalogMap
    }

    fun displayCatalogList(catalog: TypeProduct) {
        _navigateToSelectCategory.value = catalog
    }

    fun displayCatalogListComplete() {
        _navigateToSelectCategory.value = null
    }
}