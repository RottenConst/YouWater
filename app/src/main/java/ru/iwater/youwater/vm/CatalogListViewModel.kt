package ru.iwater.youwater.vm

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.*
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import timber.log.Timber
import javax.inject.Inject

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

    var favorite: LiveData<List<String>> = liveData { emit(getFavorite()) }

    private val _favoriteProducts: MutableLiveData<List<FavoriteProduct>> = MutableLiveData()
    val favoriteProducts: LiveData<List<FavoriteProduct>>
        get() = _favoriteProducts

    var products: LiveData<List<Product>> = liveData { emit(getProducts()) }

    val catalogList: LiveData<List<TypeProduct>> = liveData { emit(productRepo.getCategoryList()) }

    private val _navigateToSelectProduct: MutableLiveData<Int?> = MutableLiveData()
    val navigateToSelectProduct: LiveData<Int?>
        get() = _navigateToSelectProduct

    private val _navigateToSelectBanner: MutableLiveData<PromoBanner?> = MutableLiveData()

    fun getFavoriteProduct() {
        viewModelScope.launch {
            _favoriteProducts.value = productRepo.getAllFavoriteProducts()
        }
    }

    init {
        getFavoriteProduct()
    }

    private suspend fun getFavorite(): List<String> {
        val favorite = productRepo.getFavorite()
        return favorite?.favorites_list ?: emptyList()

    }

    private suspend fun getProducts(): List<Product> {
        val favorite = getFavorite()
        val products = productRepo.getProductList()
        products.forEach {product: Product ->
            favorite.forEach { favoriteId ->
                product.onFavoriteClick = favoriteId.toInt() == product.id
            }
        }
        return products
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

    fun addToFavorite(productId: Int) {
        viewModelScope.launch {
            productRepo.addToFavoriteProduct(productId)
        }
    }

    fun deleteFavorite(productId: Int) {
        viewModelScope.launch {
            productRepo.deleteFavorite(productId)
            getFavorite()
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

    fun displayPromoInfo(promoBanner: PromoBanner) {
        _navigateToSelectBanner.value = promoBanner
    }

    fun displayProduct(productId: Int) {
        _navigateToSelectProduct.value = productId
    }

    fun displayProductComplete() {
        _navigateToSelectProduct.value = null
    }
}