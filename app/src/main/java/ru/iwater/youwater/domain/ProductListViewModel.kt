package ru.iwater.youwater.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import ru.iwater.youwater.utils.Generator
import javax.inject.Inject

@OnScreen
class ProductListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
): ViewModel() {
    private val _catalogList: MutableLiveData<List<TypeProduct>> = MutableLiveData()
    val catalogList: LiveData<List<TypeProduct>> get() = _catalogList

    private val _productLiveData: MutableLiveData<List<Product>> = MutableLiveData()
    val productLiveData: LiveData<List<Product>> get() = _productLiveData

    init {
        viewModelScope.launch {
            _catalogList.value = productRepo.getCategoryList()
            _productLiveData.value = productRepo.getProductList()
        }
    }

    fun refreshListProduct() {
        viewModelScope.launch {
            _productLiveData.value = productRepo.getProductList()
        }

    }
}