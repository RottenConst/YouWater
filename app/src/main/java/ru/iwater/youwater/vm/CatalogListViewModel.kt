package ru.iwater.youwater.vm

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.*
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.Exception

@OnScreen
class CatalogListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
) : ViewModel() {

    val promoBanners: LiveData<List<PromoBanner>> = liveData {
        emit(productRepo.getPromoBanners())
    }

    val lastOrder: LiveData<Int?> = liveData {
        emit(getLastOrder())
    }

    private val _productsList = listOf<Product>().toMutableStateList()
    val productList: List<Product>
        get() = _productsList

    private val _favoriteProducts: MutableLiveData<List<FavoriteProduct>> = MutableLiveData()
    val favoriteProducts: LiveData<List<FavoriteProduct>>
        get() = _favoriteProducts

    private val _catalogList = listOf<TypeProduct>().toMutableStateList()
    val catalogList: List<TypeProduct>
        get() = _catalogList

    private val _navigateToSelectProduct: MutableLiveData<Int?> = MutableLiveData()
    val navigateToSelectProduct: LiveData<Int?>
        get() = _navigateToSelectProduct

    fun getFavoriteProduct() {
        viewModelScope.launch {
            _favoriteProducts.value = productRepo.getAllFavoriteProducts()
        }
    }

    init {
        getFavoriteProduct()
        getCatalogList()
    }

    private fun getCatalogList() {
        viewModelScope.launch {
            _catalogList.addAll(productRepo.getCategoryList())
        }
    }

    fun getProductsList() {
        viewModelScope.launch {
            _productsList.clear()
            val favoriteList = productRepo.getFavorite()?.favorites_list?.map { it.toInt() }
            _productsList.addAll(productRepo.getProductList())
            _productsList.forEach{ product ->
                product.onFavoriteClick = favoriteList?.contains(product.id) == true
            }
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
    fun addProductToBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = productRepo.getProductFromDB(product.id)
            try {
                if (dbProduct != null && dbProduct.category != 20) {
                    dbProduct.count += 1
                    productRepo.updateProductInBasket(dbProduct)
                } else {
                    if (product.category == 20 && productRepo.isStartPocket()) {
                        productRepo.addProductInBasket(product.copy(count = 1))
                    } else {
                        product.count += 1
                        productRepo.addProductInBasket(product)
                    }
                }
            } catch (e: Exception) {
                Timber.e("Error add in basket: $e")
            }
        }
    }

    fun addFavoriteProduct(favoriteProduct: FavoriteProduct) {
        viewModelScope.launch {
            productRepo.addToFavoriteProduct(favoriteProduct)
        }
    }

    fun onChangeFavorite(productId: Int, onFavorite: Boolean) {
        viewModelScope.launch {
            if (onFavorite) productRepo.deleteFavorite(productId)
                else productRepo.addToFavoriteProduct(productId)
            _productsList.find { product -> product.id == productId }?.onFavoriteClick = !onFavorite
        }

    }

    private suspend fun getLastOrder(): Int? {
        return productRepo.getLastOrder()
    }

    fun delFavoriteProduct(favoriteProduct: FavoriteProduct) {
        viewModelScope.launch {
            productRepo.deleteFavoriteProduct(favoriteProduct)
        }
    }

    fun displayProduct(productId: Int) {
        _navigateToSelectProduct.value = productId
    }

    fun displayProductComplete() {
        _navigateToSelectProduct.value = null
    }
}