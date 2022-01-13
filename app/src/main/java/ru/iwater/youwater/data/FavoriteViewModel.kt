package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.FavoriteRepository
import javax.inject.Inject

@OnScreen
class FavoriteViewModel @Inject constructor(
    private val favoriteRepo: FavoriteRepository
) : ViewModel() {

    private val _favoriteList: MutableLiveData<List<Product>> = MutableLiveData()
    val favoriteList: LiveData<List<Product>>
        get() = _favoriteList

    private val _navigateToSelectFavorite: MutableLiveData<Int> = MutableLiveData()
    val navigateToSelectFavorite: LiveData<Int>
        get() = _navigateToSelectFavorite

    init {
        getFavoriteProduct()
    }

    private fun getFavoriteProduct() {
        viewModelScope.launch {
            _favoriteList.value = favoriteRepo.getFavoriteProduct()
        }
    }

    fun addFavoriteProductInBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = favoriteRepo.getProductFromDB(product.id)
            if (dbProduct != null) {
                dbProduct.count += 1
                favoriteRepo.updateProduct(dbProduct)
            } else {
                product.count += 1
                favoriteRepo.saveProduct(product)
            }
        }
    }

    fun saveFavoriteProduct(favoriteProduct: Product) {
        viewModelScope.launch {
            favoriteRepo.saveFavoriteProduct(
                FavoriteProduct(
                    favoriteProduct.about,
                    favoriteProduct.app,
                    favoriteProduct.app_name,
                    favoriteProduct.category,
                    favoriteProduct.company_id,
                    favoriteProduct.gallery,
                    favoriteProduct.id,
                    favoriteProduct.name,
                    favoriteProduct.price
                )
            )
            _favoriteList.value = favoriteRepo.getFavoriteProduct()
        }
    }

    fun deleteFavoriteProduct(favoriteProduct: Product) {
        viewModelScope.launch {
            favoriteRepo.deleteFavoriteProduct(
                FavoriteProduct(
                    favoriteProduct.about,
                    favoriteProduct.app,
                    favoriteProduct.app_name,
                    favoriteProduct.category,
                    favoriteProduct.company_id,
                    favoriteProduct.gallery,
                    favoriteProduct.id,
                    favoriteProduct.name,
                    favoriteProduct.price
                )
            )
            _favoriteList.value = favoriteRepo.getFavoriteProduct()
        }
    }

    fun displayFavorite(productId: Int) {
        _navigateToSelectFavorite.value = productId
    }

    fun displayFavoriteComplete() {
        _navigateToSelectFavorite.value = null
    }
}