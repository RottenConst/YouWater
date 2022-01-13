package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.OrderRepository
import timber.log.Timber
import javax.inject.Inject

class OrderViewModel @Inject constructor(
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val authClient = orderRepo.getAuthClient()

    private val _client: MutableLiveData<Client> = MutableLiveData()
    val client: LiveData<Client>
        get() = _client

    private val _address: MutableLiveData<Address?> = MutableLiveData()
    val address: LiveData<Address?>
        get() = _address

    private val _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: LiveData<List<Product>>
        get() = _products

    private val _myOrder: MutableLiveData<List<MyOrder>> = MutableLiveData()
    val myOrder: LiveData<List<MyOrder>>
        get() = _myOrder

    init {
        getClient()
        getAddress()
        getProducts()
    }

    private fun getClient() {
        viewModelScope.launch {
            val client = orderRepo.getClientInfo(authClient.clientId)
            if (client != null) _client.value = client
        }
    }


    private fun getAddress() {
        viewModelScope.launch {
            _address.value = orderRepo.getAllAddress()
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            _products.value = orderRepo.getAllProduct()
        }
    }

    fun clearProduct(products: List<Product>) {
        viewModelScope.launch {
            products.forEach {
                orderRepo.deleteProduct(it)
            }
        }
    }

    fun orderCreate(order: Order) {
        viewModelScope.launch {

        }
    }

    fun sendAndSaveOrder(order: Order, products: List<Product>, address: String) {
        viewModelScope.launch {
            val answer = orderRepo.createOrder(order)
            Timber.d("ОТВЕТ $answer")
            val id = answer.split(":")[1].replace("}", "")
            val myOrderId = id.toInt()
            if (myOrderId != 0) {
                val myOrder = MyOrder(
                    address,
                    order.orderCost.toString(),
                    "${order.date};${order.period}",
                    products,
                    order.paymentType,
                    0,
                    myOrderId
                );
                orderRepo.saveMyOrder(myOrder)
            }
        }
    }

    fun getMyOrder() {
        viewModelScope.launch {
            _myOrder.value = orderRepo.getMyOrder()
        }
    }
}