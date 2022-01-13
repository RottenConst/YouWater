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

    fun getBasket() {
        viewModelScope.launch {
            _productsList.value = productRepo.getProductList()
        }
    }

    fun deleteProductFromBasket(product: Product) {
        viewModelScope.launch {
            productRepo.deleteProductFromBasket(product)
        }
    }

    fun addCountProduct(product: Product) {
        viewModelScope.launch {
            val productDB = productRepo.getProductFromDB(product.id)
            if (productDB != null) {
                productDB.count += 1
                productRepo.updateProductInBasket(productDB)
            }
        }
    }

    fun addProductInBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = productRepo.getProductFromDB(product.id)
            if (dbProduct != null) {
                dbProduct.count += 1
                productRepo.updateProductInBasket(dbProduct)
            } else {
                product.count += 1
                productRepo.addProductInBasket(product)
            }
            _productsList.value = productRepo.getProductList()
        }
    }

    fun minusCountProduct(product: Product) {
        viewModelScope.launch {
            val productDB = productRepo.getProductFromDB(product.id)
            if (productDB != null) {
                when {
                    productDB.count > 1 -> {
                        productDB.count -= 1
                        productRepo.updateProductInBasket(productDB)
                    }
                    productDB.count == 1 -> {
                        productRepo.deleteProductFromBasket(productDB)
                    }
                }
                _productsList.value = productRepo.getProductList()
            }
        }
    }
}