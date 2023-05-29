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
    private val repository: ProductRepository,
) : ViewModel() {

    val promoBanners: LiveData<List<PromoBanner>> = liveData {
        emit(repository.getPromoBanners())
    }

    val lastOrder: LiveData<Int?> = liveData {
        emit(getLastOrder())
    }

    private val _statusData: MutableLiveData<StatusData> = MutableLiveData()
    val statusData: LiveData<StatusData> get() = _statusData

    private val _productsList = listOf<Product>().toMutableStateList()
    val productList: List<Product>
        get() = _productsList

    private val _catalogList = listOf<TypeProduct>().toMutableStateList()
    val catalogList: List<TypeProduct>
        get() = _catalogList

    //продукт
    private val _product: MutableLiveData<Product?> = MutableLiveData()
    val product: LiveData<Product?> get() = _product

    init {
        getCatalogList()
    }

    private fun getCatalogList() {
        viewModelScope.launch {
            _catalogList.addAll(repository.getCategoryList())
        }
    }

    fun getProductsList() {
        viewModelScope.launch {
            _productsList.clear()
            val favoriteList = repository.getFavorite()?.favorites_list?.map { it.toInt() }
            _productsList.addAll(repository.getProductList())
            _productsList.forEach{ product ->
                product.onFavoriteClick = favoriteList?.contains(product.id) == true
            }
        }
    }

    fun getFavoriteProductList() {
        viewModelScope.launch {
            _productsList.clear()
            _statusData.value = StatusData.LOAD
            val favoriteList = repository.getFavorite()?.favorites_list?.map { it.toInt() }
            val products = repository.getProductList()
            favoriteList?.forEach { favoriteId ->
                products.find { it.id == favoriteId }?.let { _productsList.add(it) }
            }
            _productsList.forEach {product ->
                product.onFavoriteClick = true
            }
            _statusData.value = StatusData.DONE
        }
    }

    //инициалезация товара
    fun initProduct(productId: Int) {
        viewModelScope.launch {
            val product = repository.getProduct(productId)
            if (product != null) {
                product.count = 1
                _product.value = product
            }
        }
    }

    fun addProductCountToBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = repository.getProductFromDB(product.id)
            try {
                if (dbProduct == null) {
                    repository.addProductInBasket(product = product)
                } else {
                    repository.updateProductInBasket(product = product)
                }
            } catch (e: Exception) {
                Timber.d("Error add in basket: $e")
            }
        }
    }

    fun addProductToBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = repository.getProductFromDB(product.id)
            try {
                if (dbProduct != null && dbProduct.category != 20) {
                    dbProduct.count += 1
                    repository.updateProductInBasket(dbProduct)
                } else {
                    if (product.category == 20 && repository.isStartPocket()) {
                        repository.addProductInBasket(product.copy(count = 1))
                    } else {
                        product.count += 1
                        repository.addProductInBasket(product)
                    }
                }
            } catch (e: Exception) {
                Timber.e("Error add in basket: $e")
            }
        }
    }

    fun onChangeFavorite(productId: Int, onFavorite: Boolean) {
        viewModelScope.launch {
            if (onFavorite) repository.deleteFavorite(productId)
                else repository.addToFavoriteProduct(productId)
            _productsList.find { product -> product.id == productId }?.onFavoriteClick = !onFavorite
        }

    }

    private suspend fun getLastOrder(): Int? {
        return repository.getLastOrder()
    }

}