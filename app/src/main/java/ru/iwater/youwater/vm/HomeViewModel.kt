package ru.iwater.youwater.vm

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ru.iwater.youwater.base.App
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.Banner
import ru.iwater.youwater.data.Category
import ru.iwater.youwater.data.Favorite
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.network.ApiClient
import ru.iwater.youwater.network.ApiOrder
import ru.iwater.youwater.repository.HomeRepository
import timber.log.Timber

class HomeViewModel(
    private val repository: HomeRepository
): ViewModel() {

    val promoBanners: LiveData<List<Banner>> = liveData {
        emit(repository.getPromoBanners())
    }

    val lastOrder: LiveData<Int?> = liveData {
        emit(repository.getLastOrder())
    }

    private val _startPocket: MutableLiveData<Boolean> = MutableLiveData()
    val startPocket: LiveData<Boolean> get() = _startPocket

    private val _catalogList = listOf<Category>().toMutableStateList()
    val catalogList: List<Category>
        get() = _catalogList

    private var _productsList: MutableLiveData<List<NewProduct>> = MutableLiveData(emptyList())
    val productList: LiveData<List<NewProduct>>
        get() = _productsList

    init {
        getCatalogList()
    }
    private fun getCatalogList() {
        viewModelScope.launch {
            val startPocket = repository.isStartPocket()
            _startPocket.value = startPocket
            _catalogList.addAll(repository.getCategoryListName(startPocket))
        }
    }

    fun getProductsList(favorite: Favorite) {
        viewModelScope.launch {
            val productsList = repository.getProductList()
            productsList.forEach{ product ->
                product.onFavoriteClick = favorite.favoritesList.contains(product.id) == true
            }
            _productsList.value = productsList
        }
    }

    fun getFavoriteProductList(favorite: Favorite) {
        viewModelScope.launch {
            val productsList = repository.getProductList()
            val favoriteList = productsList.filter {
                    product -> favorite.favoritesList.contains(product.id)
            }
            favoriteList.forEach { it.onFavoriteClick = true }
            _productsList.value = favoriteList
        }
    }

    fun getProductOfCategory(categoryId: Int, favorite: Favorite) {
        viewModelScope.launch {
            val productList = repository.getProductByCategory(categoryId)
            productList.forEach { product ->
                product.onFavoriteClick = favorite.favoritesList.contains(product.id) == true
            }
            _productsList.value = productList
        }
    }

    fun addProductToBasket(product: NewProduct) {
        viewModelScope.launch {
            val dbProduct = repository.getProductFromDB(product.id)
            try {
                if (dbProduct != null && dbProduct.category != 20) {
                    dbProduct.count += 1
                    repository.updateNewProductInBasket(dbProduct)
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

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as App
                val token = ClientStorage(application.applicationContext).get().accessToken
                val repository =    HomeRepository(
                    ApiOrder.makeOrderApi(token),
                    YouWaterDB.getYouWaterDB(application.baseContext)?.newProductDao()!!
                )
                return HomeViewModel(
                    repository
                ) as T
            }
        }
    }
}