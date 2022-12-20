package ru.iwater.youwater.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import timber.log.Timber
import javax.inject.Inject

@OnScreen
class ProductListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
) : ViewModel() {

    private val _productsList: MutableLiveData<List<Product>> = MutableLiveData()
    val productsList: LiveData<List<Product>> get() = _productsList

    private val _navigateToSelectProduct: MutableLiveData<Int> = MutableLiveData()
    val navigateToSelectProduct: LiveData<Int>
        get() = _navigateToSelectProduct

    fun setCatalogItem(catalogId: Int) {
        viewModelScope.launch {
            val favoriteProducts = productRepo.getFavoriteProducts()
            val products = productRepo.getProductListOfCategory(catalogId)
            products.forEach { product ->
                if (favoriteProducts != null) {
                    for (favoriteProduct in favoriteProducts) {
                        if (favoriteProduct == product.id){
                            product.onFavoriteClick = true
                        }
                    }
                }
            }
            _productsList.value = products
        }
    }

    fun getBasket() {
        viewModelScope.launch {
            _productsList.value = productRepo.getProductListOfCategory()
        }
    }

    fun deleteProductFromBasket(product: Product) {
        viewModelScope.launch {
            productRepo.deleteProductFromBasket(product)
        }
    }

    fun addProductInBasket(productId: Int) {
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
                _productsList.value = productRepo.getProductListOfCategory()
            }
        }
    }

    suspend fun deleteFavoriteProduct(product: Product): Boolean {
        return productRepo.deleteFavoriteProduct(product.id) == true
    }

    suspend fun addProductInFavorite(product: Product): Boolean {
        return productRepo.addToFavorite(product.id) == true
    }

    fun displayProduct(productId: Int) {
        _navigateToSelectProduct.value = productId
    }

    fun displayProductComplete() {
        _navigateToSelectProduct.value = null
    }
}