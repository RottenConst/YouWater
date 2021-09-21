package ru.iwater.youwater.domain

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

    private val catalogs = mutableListOf<TypeProduct>()

    private val _catalogProductMap: MutableLiveData<Map<TypeProduct, List<Product>>> =
        MutableLiveData()
    val catalogProductMap: LiveData<Map<TypeProduct, List<Product>>>
        get() = _catalogProductMap

    private val _catalogList: MutableLiveData<List<TypeProduct>> = MutableLiveData()
    val catalogList: LiveData<List<TypeProduct>> get() = _catalogList

    private val _productLiveData: MutableLiveData<List<Product>> = MutableLiveData()
    val productLiveData: LiveData<List<Product>> get() = _productLiveData

    init {
        viewModelScope.launch {
            val category = productRepo.getCategoryList()
            val catalogMap = mutableMapOf<TypeProduct, List<Product>>()
            category.forEach {
                val products = productRepo.getProductList(it.id)
                catalogMap[it] = products
            }
            _catalogProductMap.value = catalogMap

        }
    }

    suspend fun getAllProducts() {
        val category = productRepo.getCategoryList()
        val catalogMap = mutableMapOf<TypeProduct, List<Product>>()
        viewModelScope.launch {
            category.forEach {
                val products = productRepo.getProductList(it.id)
                catalogMap[it] = products
            }
            _catalogProductMap.value = catalogMap
        }

    }

    fun refreshListProduct() {
//        viewModelScope.launch {
//            _productLiveData.value = productRepo.getProductList()
//        }

    }
}