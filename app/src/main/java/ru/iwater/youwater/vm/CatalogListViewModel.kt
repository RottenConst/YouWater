package ru.iwater.youwater.vm

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import okhttp3.internal.notifyAll
import ru.iwater.youwater.data.FavoriteProduct
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.TypeProduct
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import timber.log.Timber
import javax.inject.Inject

@OnScreen
class CatalogListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
) : ViewModel() {

    private val _favoriteProducts: MutableLiveData<List<FavoriteProduct>> = MutableLiveData()
    val favoriteProducts: LiveData<List<FavoriteProduct>>
        get() = _favoriteProducts

    val catalogProductMap: LiveData<MutableMap<TypeProduct, List<Product>>> = Transformations.switchMap(favoriteProducts) {
        liveData { emit(getAllProducts(it)) }
    }

    val catalogList: LiveData<List<TypeProduct>> = liveData {
        emit(productRepo.getCategoryList())
    }

    private val _navigateToSelectCategory: MutableLiveData<TypeProduct> = MutableLiveData()
    val navigateToSelectCategory: LiveData<TypeProduct>
        get() = _navigateToSelectCategory

    private val _navigateToSelectProduct: MutableLiveData<Int> = MutableLiveData()
    val navigateToSelectProduct: LiveData<Int>
        get() = _navigateToSelectProduct

    fun getFavoriteProduct() {
        viewModelScope.launch {
            _favoriteProducts.value = productRepo.getAllFavoriteProducts()
        }
    }

    init {
        getFavoriteProduct()
    }

    private suspend fun getAllProducts(favoriteProducts: List<FavoriteProduct>): MutableMap<TypeProduct, List<Product>> {
        val catalogMap = mutableMapOf<TypeProduct, List<Product>>()
        val catalogs = productRepo.getCategoryList()
        return if (catalogs.isNotEmpty()) {
            catalogs.forEach {
                val products = productRepo.getProductList(it.id)
                products.forEach { product ->
                    for (favoriteProduct in favoriteProducts) {
                        if (favoriteProduct.id == product.id) {
                            product.onFavoriteClick = true
                        }
                    }
                }
                catalogMap[it] = products
            }
            return catalogMap
        }
        else mutableMapOf()
    }

    fun addProductInBasket(productId: Int) {
        viewModelScope.launch {
            val dbProduct = productRepo.getProductFromDB(productId)
            val product = productRepo.getProduct(productId)
            val productStart = productRepo.getProductList()?.filter { it.category == 20 }
            val start = productStart.isNullOrEmpty()
            Timber.d("STAAAAAAAAAAAAAART == $start")
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

    fun addProductInFavorite(product: Product) {
        viewModelScope.launch {
            productRepo.addToFavoriteProduct(
                FavoriteProduct(product.about,
                                product.app,
                                product.app_name,
                                product.category,
                                product.company_id,
                                product.gallery,
                                product.id,
                                product.name,
                                product.price)
            )
        }
    }

    fun addFavoriteProduct(favoriteProduct: FavoriteProduct) {
        viewModelScope.launch {
            productRepo.addToFavoriteProduct(favoriteProduct)
        }
    }

    fun deleteFavoriteProduct(product: Product) {
        viewModelScope.launch {
            productRepo.deleteFavoriteProduct(
                FavoriteProduct(
                    product.about,
                    product.app,
                    product.app_name,
                    product.category,
                    product.company_id,
                    product.gallery,
                    product.id,
                    product.name,
                    product.price
                )
            )
        }
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

    fun displayCatalogList(catalog: TypeProduct) {
        _navigateToSelectCategory.value = catalog
    }

    fun displayCatalogListComplete() {
        _navigateToSelectCategory.value = null
    }
}