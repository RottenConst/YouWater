package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.OrderRepository
import timber.log.Timber
import javax.inject.Inject

enum class Status{ SEND, ERROR }

class OrderViewModel @Inject constructor(
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val authClient = orderRepo.getAuthClient()

    private val _client: MutableLiveData<Client> = MutableLiveData()
    val client: LiveData<Client>
        get() = _client

    private val _address: MutableLiveData<List<Address>?> = MutableLiveData()
    val address: LiveData<List<Address>?>
        get() = _address

    private val _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: LiveData<List<Product>>
        get() = _products

    private val _myOrder: MutableLiveData<List<MyOrder>> = MutableLiveData()
    val myOrder: LiveData<List<MyOrder>>
        get() = _myOrder

    private val _statusOrder: MutableLiveData<Status> = MutableLiveData()
    val statusOrder: LiveData<Status>
        get() = _statusOrder

    private val _linkHTTP: MutableLiveData<String> = MutableLiveData()
    val linkHTTP: LiveData<String>
        get() = _linkHTTP

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

    fun getOrderFromCrm() {
        viewModelScope.launch {
            val listOrderCRM = orderRepo.getAllOrder(orderRepo.getAuthClient().clientId)
            val listMyOrder = mutableListOf<MyOrder>()
            if (listOrderCRM.isNotEmpty()){
                listOrderCRM.forEach { order ->
                    val address = when {
                        order.address_json.entrance == null -> "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house})"
                        order.address_json.floor == null -> "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance}"
                        order.address_json.flat == null -> "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} этаж${order.address_json.floor}"
                        else -> "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} этаж${order.address_json.floor} кв.${order.address_json.flat}"
                    }
                    val listProduct = mutableListOf<Product>()
                    order.water_equip.forEach {
                        val product = orderRepo.getProduct(it.id)
                        if (product != null) {
                            product.count = it.count
                            listProduct.add(product)
                        }
                    }
                    listMyOrder.add(
                        MyOrder(id = order.id, address = address, cash = order.order_cost, date = "${order.date};${order.period}", products = listProduct, typeCash = order.payment_type, status = 0)
                    )
                }
            }
            _myOrder.value = listMyOrder.reversed()
        }
    }

    fun sendAndSaveOrder(order: Order, products: List<Product>, address: String) {
        viewModelScope.launch {
            val answer = orderRepo.createOrder(order)
            Timber.d("ОТВЕТ $answer")
            if (answer != "error") {
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
                _statusOrder.value = Status.SEND
            } else {
                _statusOrder.value = Status.ERROR
            }
        }
    }

    //запрос на регистрацию заказа
    fun payToCard(orderId: String, amount: Int) {
        viewModelScope.launch {
            val paymentCard = PaymentCard(orderNumber = orderId, amount = amount)
            _linkHTTP.value = orderRepo.payCard(paymentCard)
//            _linkHTTP.value = "\"https://habr.com/ru/post/428736/\""
        }
    }

    fun setLinkHttp(link: String?) {
        _linkHTTP.value = link
    }

    fun getMyOrder() {
        viewModelScope.launch {
            _myOrder.value = orderRepo.getMyOrder()
        }
    }
}