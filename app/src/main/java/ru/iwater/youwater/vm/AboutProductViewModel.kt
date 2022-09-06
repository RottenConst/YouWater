package ru.iwater.youwater.vm

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.repository.ProductRepository
import javax.inject.Inject

class AboutProductViewModel @Inject constructor(
    private val productRepo: ProductRepository
): ViewModel() {
    //продукт
    private val _product: MutableLiveData<Product> = MutableLiveData()
    val product: LiveData<Product> get() = _product

    //подробная цена товара
    private val _navigateToPriceProduct: MutableLiveData<String> = MutableLiveData()
    val navigateToPriceProduct: LiveData<String> get() = _navigateToPriceProduct

    //+1 количество товара
    fun plusCountProduct(product: Product) {
        if (product.category != 20) {
            product.count += 1
            _product.value = product
        }
    }

    //-1 количество товара
    fun minusCountProduct(product: Product) {
        if (product.count > 1) {
            product.count -= 1
            _product.value = product
        }
    }

    //сохранить(добавить в корзину)
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

    //инициалезация товара
    fun initProduct(productId: Int) {
        viewModelScope.launch {
            var product = productRepo.getProductFromDB(productId) //был ли уже добавлен товар
            if (product != null) {
                _product.value = product
            } else { //загружаем товар из црм
                product = productRepo.getProduct(productId)
                if (product != null) {
                    product.count = 1
                    _product.value = product
                }
            }
        }
    }

    // показать подробную цену товара
    fun displayPrice(price: String) {
        _navigateToPriceProduct.value = price
    }

    // обнулить подробную цену
    fun displayPriceComplete() {
        _navigateToPriceProduct.value = null
    }
}