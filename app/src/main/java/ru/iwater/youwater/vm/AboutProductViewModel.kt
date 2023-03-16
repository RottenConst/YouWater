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

    //подробная цена товара
    private val _navigateToPriceProduct: MutableLiveData<String?> = MutableLiveData()
    val navigateToPriceProduct: LiveData<String?> get() = _navigateToPriceProduct

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
    fun addProductToBasket(productId: Int) {
        viewModelScope.launch {
            val dbProduct = productRepo.getProductFromDB(productId)
            val product = productRepo.getProduct(productId)
            val productStart = productRepo.getProductListOfCategory()?.filter { it.category == 20 }
            val start = productStart.isNullOrEmpty()
            try {
                if (dbProduct != null && dbProduct.category != 20) {
                    dbProduct.count += 1
                    productRepo.updateProductInBasket(dbProduct)
                } else {
                    if (product?.category == 20 && start) {
                        product.count = 1
                        productRepo.addProductInBasket(product)
                    } else if (product?.category != 20) {
                        if (product != null) {
                            product.count += 1
                            productRepo.addProductInBasket(product)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e("Error add in basket: $e")
            }
        }
    }

    fun addProductToBasket1(product: Product) {
        viewModelScope.launch {
            val dbProduct = productRepo.getProductFromDB(product.id)
//            val start = productRepo.isStartPocket()
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

    suspend fun getProduct(productId: Int): Product? {
        val product = productRepo.getProduct(productId)
        return if (product!= null) {
            product.count = 1
            product
        } else null
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