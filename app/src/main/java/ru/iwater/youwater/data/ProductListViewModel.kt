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
class ProductListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
) : ViewModel() {

    private val _catalogItem: MutableLiveData<TypeProduct> = MutableLiveData()
    val catalogItem: LiveData<TypeProduct> get() = _catalogItem

    private val _productsList: MutableLiveData<List<Product>> = MutableLiveData()
    val productsList: LiveData<List<Product>> get() = _productsList

    fun setCatalogItem(catalogId: Int) {
        viewModelScope.launch {
            _productsList.value = productRepo.getProductList(catalogId)
        }
    }
}