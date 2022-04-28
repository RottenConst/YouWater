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
class CatalogListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
) : ViewModel() {

    private val catalogs = mutableListOf<TypeProduct>()
    private val favoriteProducts = mutableListOf<FavoriteProduct>()

    private val _catalogProductMap: MutableLiveData<Map<TypeProduct, List<Product>>> =
        MutableLiveData()
    val catalogProductMap: LiveData<Map<TypeProduct, List<Product>>>
        get() = _catalogProductMap

    private val _catalogList: MutableLiveData<List<TypeProduct>> = MutableLiveData()
    val catalogList: LiveData<List<TypeProduct>> get() = _catalogList

    private val _navigateToSelectCategory: MutableLiveData<TypeProduct> = MutableLiveData()
    val navigateToSelectCategory: LiveData<TypeProduct>
        get() = _navigateToSelectCategory

    private val _navigateToSelectProduct: MutableLiveData<Int> = MutableLiveData()
    val navigateToSelectProduct: LiveData<Int>
        get() = _navigateToSelectProduct


    override fun onCleared() {
        super.onCleared()
        catalogClear()
    }

    private suspend fun getAllProducts(catalogs: List<TypeProduct>) {
        val catalogMap = mutableMapOf<TypeProduct, List<Product>>()
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
        _catalogProductMap.value = catalogMap
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
        }
    }

    private fun getFavoriteProduct() {
        viewModelScope.launch {
            productRepo.getAllFavoriteProducts()?.let { favoriteProducts.addAll(it) }
        }
    }

    fun refreshProduct() {
        viewModelScope.launch {
            _catalogProductMap.value = emptyMap()
            catalogs.addAll(productRepo.getCategoryList())
            _catalogList.value = catalogs
            getFavoriteProduct()
            getAllProducts(catalogs)
        }
    }

    private fun catalogClear() {
        _catalogProductMap.value = emptyMap()
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