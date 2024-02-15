package ru.iwater.youwater.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ru.iwater.youwater.base.App
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.InfoProduct
import ru.iwater.youwater.data.Measure
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.network.ApiOrder
import ru.iwater.youwater.repository.InfoProductRepository
import ru.iwater.youwater.screen.navigation.MainNavRoute.AboutProductScreen.productId
import timber.log.Timber

class AboutProductViewModel (
    private val productId: Int,
    private val repository: InfoProductRepository
): ViewModel() {

    private val _product: MutableLiveData<InfoProduct?> = MutableLiveData()
    val product: LiveData<InfoProduct?> get() = _product

    private val _measureList: MutableLiveData<List<Measure>> = MutableLiveData(emptyList())
    val measureList: LiveData<List<Measure>> get() = _measureList



    init {
        initProduct(productId)
    }
    //инициалезация товара
    private fun initProduct(productId: Int) {
        viewModelScope.launch {
            val product = repository.getProduct(productId)
            val measures = repository.getMeasureList()
            if (product != null) {
                product.count = 1
                _product.value = product
                _measureList.value = measures
            }
        }
    }

    /**
     *  добавление товара в корзину, определённое количество
     */
    fun addProductCountToBasket(product: NewProduct) {
        viewModelScope.launch {
            val dbProduct = repository.getProductFromDB(productId)
            try {
                if (dbProduct == null) {
                    repository.addProductInBasket(product = product)
                } else {
                    repository.updateNewProductInBasket(product = product)
                }
            } catch (e: Exception) {
                Timber.d("Error add in basket: $e")
            }
        }
    }
}

class AboutProductViewModelFactory(private val productId: Int): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as App
        val token = ClientStorage(application.applicationContext).get().accessToken
        val repository = InfoProductRepository(
            ApiOrder.makeOrderApi(token),
            YouWaterDB.getYouWaterDB(application.baseContext)?.newProductDao()!!
        )
        return AboutProductViewModel(
            productId = productId,
            repository = repository
        ) as T
    }
}