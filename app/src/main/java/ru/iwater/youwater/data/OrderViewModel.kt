package ru.iwater.youwater.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.OrderRepository
import timber.log.Timber
import javax.inject.Inject

enum class Status{ SEND, ERROR }
enum class PaymentStatus {SUCCESSFULLY, ERROR}

class OrderViewModel @Inject constructor(
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val authClient = orderRepo.getAuthClient()

    //данные клиета
    private val _client: MutableLiveData<Client> = MutableLiveData()
    val client: LiveData<Client>
        get() = _client

    //адрес доставки
    private val _address: MutableLiveData<List<Address>?> = MutableLiveData()
    val address: LiveData<List<Address>?>
        get() = _address

    //список продуктов заявки
    private val _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: LiveData<List<Product>>
        get() = _products

    //список заказов клиента
    private val _listMyOrder: MutableLiveData<List<MyOrder>> = MutableLiveData()
    val listMyOrder: LiveData<List<MyOrder>>
        get() = _listMyOrder

    //последний заказ клиента
    private val _myOrder: MutableLiveData<List<MyOrder?>> = MutableLiveData()
    val myOrder: LiveData<List<MyOrder?>>
        get() = _myOrder

    //статус отправки заявки
    private val _statusOrder: MutableLiveData<Status> = MutableLiveData()
    val statusOrder: LiveData<Status>
        get() = _statusOrder

    //данные по оплате(ссылка, id)
    private val _dataPayment: MutableLiveData<List<String>> = MutableLiveData()
    val dataPayment: LiveData<List<String>>
        get() = _dataPayment

    private val _numberOrder: MutableLiveData<String> = MutableLiveData()
    val numberOrder: LiveData<String>
        get() = _numberOrder

    //ссылка на оплату
    private val _linkPayment: MutableLiveData<String> = MutableLiveData()
    val linkPayment: LiveData<String>
        get() = _linkPayment

    //статус оплаты
    private val _paymentStatus: MutableLiveData<PaymentStatus> = MutableLiveData()
    val paymentStatus: LiveData<PaymentStatus>
        get() = _paymentStatus

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

    fun getAllFactAddress(context: Context?) {
        viewModelScope.launch {
            val listAddress = orderRepo.getAllFactAddress()
            if (!listAddress.isNullOrEmpty()) {
                val region = listAddress[0].split(",")[0].removePrefix("\"")
                val street = listAddress[1].split(" ")[0].removePrefix("\"").removeSuffix(",")
                val house = listAddress[1].split(" ")[2].removeSuffix(",").toInt()
                val building = listAddress[1].split(" ")[4].removeSuffix(",")
                val entrance = listAddress[1].split(" ")[6].removeSuffix(",").toInt()
                val floor = listAddress[1].split(" ")[8].removeSuffix(",").toInt()
                val flat =
                    listAddress[1].split(" ")[10].removeSuffix(",").removeSuffix("\"").toInt()
                val address = Address(region, street, house, building, entrance, floor, flat, "")
                val savedAddress = orderRepo.getAllAddress()
                if (savedAddress.isNullOrEmpty()) {
                    saveAddress(address)
                } else {
                    savedAddress.forEach {
                        if (it.street != address.street && it.flat != address.flat) {
                            saveAddress(address)
                        }
                    }
                }
                _address.value = listOf(address)
            } else {
                Toast.makeText(context, "Ошибка, неудается добавить адрес", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveAddress(address: Address) {
        viewModelScope.launch {
            orderRepo.saveAddress(address)
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
            _listMyOrder.value = listMyOrder.reversed()
        }
    }


    private fun getOrderCrm(id: Int) {
        viewModelScope.launch {
            val orderFromCrm = orderRepo.getOrder(orderRepo.getAuthClient().clientId, id)
            Timber.d("ORDER SIZE ${orderFromCrm.size}")
            val myOrder = mutableListOf<MyOrder>()
            if (orderFromCrm.isNotEmpty()) {
                orderFromCrm.forEach { order ->
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
                    myOrder.add(
                        MyOrder(id = order.id, address = address, cash = order.order_cost, date = "${order.date};${order.period}", products = listProduct, typeCash = order.payment_type, status = 0)
                    )
                }
            }
            _listMyOrder.value = myOrder
        }
    }

    fun sendAndSaveOrder(order: Order, products: List<Product>, address: String) {
        viewModelScope.launch {
            val answer = orderRepo.createOrder(order)
            Timber.d("ОТВЕТ $answer")
            if (answer != "error") {
                val id = answer.split(":")[1].replace("}", "")
                _numberOrder.value = id
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
    fun payToCard(orderId: String, amount: Int, phone: String) {
        viewModelScope.launch {
            val paymentCard = PaymentCard(orderNumber = orderId, amount = amount, phone = phone)
            _dataPayment.value = orderRepo.payCard(paymentCard)
        }
    }

    fun getPaymentStatus(orderId: String) {
        viewModelScope.launch {
            val paymentStatus = orderRepo.getPaymentStatus(orderId)
            getOrderCrm(paymentStatus.first)
            Timber.d("STATUS ${paymentStatus.first} ${paymentStatus.second}")
            if (paymentStatus.second == 2) {
                val parameters = JsonObject()
                parameters.addProperty("updated_payment_state", 1)
                parameters.addProperty("updated_acq", orderId)
                if (orderRepo.setStatusPayment(paymentStatus.first, parameters)) {
                    _paymentStatus.value = PaymentStatus.SUCCESSFULLY
                } else _paymentStatus.value = PaymentStatus.ERROR
            } else _paymentStatus.value = PaymentStatus.ERROR
        }
    }

    fun setLinkHttp(link: String?) {
        _linkPayment.value = link
    }

    fun getMyAllOrder() {
        viewModelScope.launch {
            _listMyOrder.value = orderRepo.getMyAllOrder()
        }
    }

    fun getMyOrder(id: Int) {
        viewModelScope.launch {
            _myOrder.value = orderRepo.getMyOrder(id)
        }
    }
}