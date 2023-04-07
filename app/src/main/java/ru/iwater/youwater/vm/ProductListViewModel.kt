package ru.iwater.youwater.vm

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import javax.inject.Inject

@OnScreen
class ProductListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
) : ViewModel() {

    private val _productsList = listOf<Product>().toMutableStateList()
    val productsList: List<Product> get() = _productsList

    private val _priceNoDiscount: MutableLiveData<Int> = MutableLiveData()
    val priceNoDiscount: LiveData<Int> get() = _priceNoDiscount

    private val _generalCost: MutableLiveData<Int> = MutableLiveData()
    val generalCost: LiveData<Int>
        get() = _generalCost

    fun getBasket() {
        viewModelScope.launch {
            _productsList.clear()
            _productsList.addAll(productRepo.getProductListOfCategory())
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    fun deleteProductFromBasket(productId: Int) {
        viewModelScope.launch {
            val product = _productsList.find { it.id == productId }
            if (product != null) {
                productRepo.deleteProductFromBasket(product)
            }
            _productsList.remove(product)
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    private fun getCostProduct() {
        var generalCostProducts = 0
        productsList.forEach { product ->
            generalCostProducts += product.getPriceOnCount(product.count)
        }
        _generalCost.value = generalCostProducts
    }

    private fun getPriceNoDiscount() {
        var generalCostProducts = 0
        productsList.forEach { product ->
            generalCostProducts += product.getPriceNoDiscount(product.count)
        }
        _priceNoDiscount.value = generalCostProducts
    }

    fun plusCountProduct(productId: Int) {
        viewModelScope.launch {
            val product = _productsList.find { it.id == productId }
            if (product != null) {
                product.count += 1
                productRepo.updateProductInBasket(product)
            }
            getPriceNoDiscount()
            getCostProduct()
        }

    }

    fun minusCountProduct(productId: Int) {
        viewModelScope.launch {
            val product = _productsList.find { it.id == productId }
            if (product != null) {
                when {
                    product.count > 1 -> {
                        product.count -= 1
                        productRepo.updateProductInBasket(product)
                    }
                }
            }
            getPriceNoDiscount()
            getCostProduct()
        }
    }
}