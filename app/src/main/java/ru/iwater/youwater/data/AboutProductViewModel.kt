package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.ProductRepository
import javax.inject.Inject

class AboutProductViewModel @Inject constructor(
    private val productRepo: ProductRepository
): ViewModel() {

    private val _product: MutableLiveData<Product> = MutableLiveData()
    val product: LiveData<Product>
        get() = _product


    fun plusCountProduct() {
        val product = _product.value
        if (product != null) {
            product.count += 1
            _product.value = product
        }
    }

    fun minusCountProduct() {
        val product = _product.value
        if (product != null) {
            if (product.count > 1) {
                product.count -= 1
                _product.value = product
            }
        }
    }

    fun addProductToBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = productRepo.getProductFromDB(product.id)
            if (dbProduct != null) {
                productRepo.updateProductInBasket(product)
            } else {
                productRepo.addProductInBasket(product)
            }
        }
    }

    fun initProduct(productId: Int) {
        viewModelScope.launch {
            var product = productRepo.getProductFromDB(productId)
            if (product != null) {
                _product.value = product
            } else {
                product = productRepo.getProduct(productId)
                if (product != null) {
                    product.count = 1
                    _product.value = product
                }
            }
        }
    }
}