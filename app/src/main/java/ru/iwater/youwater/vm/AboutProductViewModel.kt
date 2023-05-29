package ru.iwater.youwater.vm

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.repository.ProductRepository
import timber.log.Timber
import javax.inject.Inject

class AboutProductViewModel @Inject constructor(
    private val productRepo: ProductRepository
): ViewModel() {
    //продукт
    private val _product: MutableLiveData<Product?> = MutableLiveData()
    val product: LiveData<Product?> get() = _product

    fun addProductToBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = productRepo.getProductFromDB(product.id)
            try {
                if (dbProduct == null) {
                    productRepo.addProductInBasket(product = product)
                } else {
                    productRepo.updateProductInBasket(product = product)
                }
            } catch (e: Exception) {
                Timber.d("Error add in basket: $e")
            }
        }

    }

    //инициалезация товара
    fun initProduct(productId: Int) {
        viewModelScope.launch {
               val product = productRepo.getProduct(productId)
                if (product != null) {
                    product.count = 1
                    _product.value = product
            }
        }
    }
}