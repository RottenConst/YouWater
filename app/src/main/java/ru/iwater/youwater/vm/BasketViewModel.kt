package ru.iwater.youwater.vm

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.NewProduct
import timber.log.Timber

class BasketViewModel(
    val productList: List<NewProduct>,
): ViewModel() {

    private val _products = listOf<NewProduct>().toMutableStateList()
    val products: List<NewProduct> = _products
    private val _priceProduct = MutableLiveData<Int>(0)
    val priceProduct: LiveData<Int> = _priceProduct
    private val _generalPrice = MutableLiveData<Int>(0)
    val generalPrice: LiveData<Int> = _generalPrice

    init {
        _products.addAll(productList)
        refreshPrice(productList)
    }

    fun refreshPrice(productList: List<NewProduct>) {
        var priceProduct = 0
        var generalPrice = 0
        productList.forEach { product ->
            priceProduct += product.getPriceNoDiscount(product.count)
            generalPrice += product.getPriceOnCount(product.count)
        }
        _priceProduct.value = priceProduct
        _generalPrice.value = generalPrice
    }

    fun plusCountProduct(index: Int, updateProduct: (NewProduct, Int) -> Unit) {
        viewModelScope.launch {
            val product = _products[index]
            product.count += 1
            _products[index] = product
            updateProduct(_products[index], product.count)
            refreshPrice(_products)
        }
    }

    fun deleteProduct(productIndex: Int, delete: (Int) -> Unit) {
        viewModelScope.launch {
            val products = _products.filter { product -> product.id != _products[productIndex].id }
            Timber.d("is init")
            delete(_products[productIndex].id)
            _products.clear()
            _products.addAll(products)
            refreshPrice(_products)
        }
    }

    fun minusCountProduct(index: Int, updateProduct: (NewProduct, Int) -> Unit) {
        viewModelScope.launch {
            val product = _products[index]
            product.count -= 1
            _products[index] = product
            updateProduct(_products[index], product.count)
            refreshPrice(_products)
        }
    }
}

class BasketViewModelFactory(private val productList: List<NewProduct>): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return BasketViewModel(
            productList = productList,
        ) as T
    }
}