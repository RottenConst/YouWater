package ru.iwater.youwater.vm

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.yandex.metrica.impl.ob.Ne
import kotlinx.coroutines.launch
import ru.iwater.youwater.base.App
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.network.ApiClient
import ru.iwater.youwater.network.ApiOrder
import ru.iwater.youwater.repository.BasketRepository
import ru.iwater.youwater.repository.OrderRepository
import timber.log.Timber

class BasketViewModel(
    val productList: List<NewProduct>,
    private val repository: BasketRepository
): ViewModel() {

    private val _products = listOf<NewProduct>().toMutableStateList()
    val products: List<NewProduct> = _products
    private val _priceProduct = MutableLiveData<Int>(0)
    val priceProduct: LiveData<Int> = _priceProduct
    private val _generalPrice = MutableLiveData<Int>(0)
    val generalPrice: LiveData<Int> = _generalPrice

    init {
        _products.addAll(productList)
        refreshPrice(productList)
    }

    fun refreshPrice(productList: List<NewProduct>) {
        var priceProduct = 0
        var generalPrice = 0
        productList.forEach { product ->
            priceProduct += product.getPriceNoDiscount(product.count)
            generalPrice += product.getPriceOnCount(product.count)
        }
        _priceProduct.value = priceProduct
        _generalPrice.value = generalPrice
    }

    fun plusCountProduct(index: Int, updateProduct: (NewProduct, Int) -> Unit) {
        viewModelScope.launch {
            val product = _products[index]
            product.count += 1
            _products[index] = product
            updateProduct(_products[index], product.count)
            refreshPrice(_products)
            //            product.count += 1
//            _products.value?.get(productIt)?.count
//            val product = productList.find {
//                it.id == productIt
//            }
//            updateProduct(productList[index], productlist?.get(index)?.count1)
//            repository.updateNewProductInBasket(product!!.copy(count = product.count + 1))
        }
    }

    fun deleteProduct(productIndex: Int, delete: (Int) -> Unit) {
        viewModelScope.launch {
            val productslist = _products.filter { product -> product.id != _products[productIndex].id }
            Timber.d("is init")
            delete(_products[productIndex].id)
            _products.clear()
            _products.addAll(productslist)

            refreshPrice(_products)

//            _products.value?.map {  }
//            repository.deleteProductFromBasket(product)
//            _products.value = repository.getProductListOfCategory()
        }
    }

    fun minusCountProduct(index: Int, updateProduct: (NewProduct, Int) -> Unit) {
        viewModelScope.launch {
//            val productslist = _products.value
//            if (productslist != null) {
//                productList[index].count -= 1
//                _products.value = productslist ?: emptyList()
//                updateProduct(productList[index], productslist[index].count)
//                refreshPrice(productList)
//            }
            val product = _products[index]
            product.count -= 1
            _products[index] = product
            updateProduct(_products[index], product.count)
            refreshPrice(_products)
        }
    }

//    fun getPriceNoDiscount(): Int {
//        var generalCostProducts = 0
//        productList.forEach { product ->
//            generalCostProducts += product.getPriceNoDiscount(product.count)
//        }
//        return generalCostProducts
//    }

//    fun getCostProduct(): Int {
//        var generalCostProducts = 0
//        productList.forEach { product ->
//            generalCostProducts += product.getPriceOnCount(product.count)
//        }
//        return generalCostProducts
//    }
}

class BasketViewModelFactory(private val productList: List<NewProduct>): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as App
        val repository = BasketRepository(
            YouWaterDB.getYouWaterDB(application.baseContext)?.newProductDao()!!
        )
        return BasketViewModel(
            productList = productList,
            repository = repository
        ) as T
    }
}