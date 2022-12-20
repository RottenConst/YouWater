package ru.iwater.youwater.vm

import android.view.View
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
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

    private val _screenLoading: MutableLiveData<StatusLoading> = MutableLiveData()
    val screenLoading: LiveData<StatusLoading> get() = _screenLoading

    val lastOrder: LiveData<Int?> = liveData {
        emit(getLastOrder())
    }

    private val _favoriteProducts: MutableLiveData<List<FavoriteProduct>> = MutableLiveData()
    val favoriteProducts: LiveData<List<FavoriteProduct>>
        get() = _favoriteProducts

    private val _favorite: MutableLiveData<List<Int>> = MutableLiveData()
    val favorite: LiveData<List<Int>>
        get() = _favorite

    val catalogProductMap: LiveData<MutableMap<TypeProduct, List<Product>>> = Transformations.switchMap(favorite) {
        liveData { emit(getAllProducts(it)) }
    }

    val catalogList: LiveData<List<TypeProduct>> = liveData {
        emit(productRepo.getCategoryList())
    }

    private val _navigateToSelectCategory: MutableLiveData<TypeProduct?> = MutableLiveData()
    val navigateToSelectCategory: LiveData<TypeProduct?>
        get() = _navigateToSelectCategory

    private val _navigateToSelectProduct: MutableLiveData<Int?> = MutableLiveData()
    val navigateToSelectProduct: LiveData<Int?>
        get() = _navigateToSelectProduct

    private val _navigateToSelectBanner: MutableLiveData<PromoBanner?> = MutableLiveData()
    val navigateToSelectBanner: LiveData<PromoBanner?> get() = _navigateToSelectBanner

    fun getFavoriteProductId() {
        viewModelScope.launch {
            _favorite.value = productRepo.getFavoriteProducts()
        }
    }

    fun getFavoriteProduct(){
        viewModelScope.launch {
            val favoriteId = productRepo.getFavoriteProducts()
            if (favoriteId != null) {
                _favoriteProducts.value = getFavoriteProducts(favoriteId)
            }
        }
    }

    init {
        _screenLoading.value = StatusLoading.LOADING
        getFavoriteProductId()
    }

    private suspend fun getAllProducts(favoriteProducts: List<Int>): MutableMap<TypeProduct, List<Product>> {
        val catalogMap = mutableMapOf<TypeProduct, List<Product>>()
        val catalogs = productRepo.getCategoryList()
        return if (catalogs.isNotEmpty()) {
            val productsList = productRepo.getProductList()
            catalogs.forEach { category ->
                val products = productsList.filter { it.category == category.id }
                products.forEach { product ->
                    for (favoriteProduct in favoriteProducts) {
                        if (favoriteProduct == product.id) {
                            product.onFavoriteClick = true
                        }
                    }
                }
                catalogMap[category] = products
            }
            _screenLoading.value = StatusLoading.DONE
            return catalogMap
        }
        else mutableMapOf()
    }

    private suspend fun getFavoriteProducts(favoriteProductsId: List<Int>): List<FavoriteProduct> {
        val favorite = mutableListOf<FavoriteProduct>()
        for (productId in favoriteProductsId) {
            val product = productRepo.getProduct(productId)
            if (product != null) {
                favorite.add(
                    FavoriteProduct(
                        about = product.about,
                        app = product.app,
                        app_name = product.app_name,
                        category = product.category,
                        company_id = product.company_id,
                        gallery = product.gallery,
                        id = product.id,
                        name = product.name,
                        price = product.price
                    )
                )
            }
        }
        return favorite
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

    suspend fun addProductInFavorite(product: Product): Boolean {
        return productRepo.addToFavorite(product.id) == true
    }

    fun addFavoriteProduct(favoriteProduct: FavoriteProduct, view: View) {
        viewModelScope.launch {
            if (productRepo.addToFavorite(favoriteProduct.id) == true) {
                getFavoriteProduct()
            } else {
                Snackbar.make(view, "Ошибка", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun deleteFavoriteProduct(product: Product): Boolean {
        return productRepo.deleteFavoriteProduct(product.id) == true
    }

    private suspend fun getLastOrder(): Int? {
        return productRepo.getLastOrder()
    }

    suspend fun delFavoriteProduct(favoriteProduct: FavoriteProduct): Boolean {
        val status = productRepo.deleteFavoriteProduct(favoriteProduct.id)
        return if (status == true) {
            getFavoriteProduct()
            true
        } else false
    }

    fun displayPromoInfo(promoBanner: PromoBanner) {
        _navigateToSelectBanner.value = promoBanner
    }

    fun displayPromoInfoComplete() {
        _navigateToSelectBanner.value = null
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