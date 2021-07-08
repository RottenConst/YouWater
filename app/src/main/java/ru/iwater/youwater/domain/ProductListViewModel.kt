package ru.iwater.youwater.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import ru.iwater.youwater.utils.Generator
import javax.inject.Inject

@OnScreen
class ProductListViewModel @Inject constructor(
    val productRepo: ProductRepository,
): ViewModel() {
    private val mProductLiveData: MutableLiveData<List<Product>> = MutableLiveData()
    val productLiveData: LiveData<List<Product>> get() = mProductLiveData

    init {
        mProductLiveData.value = productRepo.getProductList(Generator)
    }

    fun refreshListProduct() {
        mProductLiveData.value = productRepo.getProductList(Generator)
    }
}