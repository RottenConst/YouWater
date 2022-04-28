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
enum class OrderLoadStatus { LOADING, DONE, ERROR }

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

    //статус загрузки заказов
    private val _statusLoad: MutableLiveData<OrderLoadStatus> = MutableLiveData()
    val statusLoad: LiveData<OrderLoadStatus>
        get() = _statusLoad

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
            Timber.d("ADDRESS SIZE = $listAddress")
            val addresses = mutableListOf<Address>()
            if (!listAddress.isNullOrEmpty()) {
                for (index in listAddress.indices) {
                    if (index % 2 != 0) {
                        val region = listAddress[index - 1].split(",")[0].removePrefix("\"")
                        val address = getAddressFromString(listAddress[index].split(","), region)
                        addresses.add(address)
                        val savedAddress = orderRepo.getAllAddress()
                        if (savedAddress.isNullOrEmpty()) {
                            saveAddress(address)
                        } else {
                            savedAddress.forEach {
                                if (it.street != address.street && it.house != address.house && it.flat != address.flat) {
                                    saveAddress(address)
                                }
                            }
                        }
                    }
                }
                _address.value = addresses
            } else {
                Toast.makeText(context, "Ошибка не удалось загрузить адреса", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAddressFromString(rawAddress: List<String>, region: String): Address {
        var street = ""
        var house = 0
        var building = ""
        var entrance: Int? = null
        var floor: Int? = null
        var flat: Int? = null
        rawAddress.forEachIndexed { index, s ->
            if (index == 0) street = s.removePrefix("\"").removeSuffix(",")
            if (index > 0) {
                house = parseHouse(s, house)
                building = parseBuilding(s, building)
                entrance = parseEntrance(s, entrance)
                floor = parseFloor(s, floor)
                flat = parseFlat(s, flat)
            }
        }
        Timber.d("$region $street $house $building $entrance $floor $flat")
        return Address(region, street, house, building, entrance, floor, flat, "")
    }

    private fun parseHouse(string: String, house: Int): Int{
        val houseList = string.split(" ")
        return if (houseList[1] == "д." && house == 0) {
            houseList[2].removeSuffix("\"").toInt()
        } else house
    }

    private fun parseEntrance(string: String, entrance: Int?): Int?{
        val entranceList = string.split(" ")
        return if (entranceList[1] == "пд." && entrance == null) {
            entranceList[2].removeSuffix("\"").toInt()
        } else entrance
    }

    private fun parseFloor(string: String, floor: Int?): Int?{
        val floorList = string.split(" ")
        return if (floorList[1] == "эт." && floor == null) {
            floorList[2].removeSuffix("\"").toInt()
        } else floor
    }

    private fun parseFlat(string: String, flat: Int?): Int?{
        val flatList = string.split(" ")
        return if (flatList[1] == "кв." && flat == null) {
            flatList[2].removeSuffix("\"").toInt()
        } else flat
    }

    private fun parseBuilding(string: String, building: String): String{
        val buildingList = string.split(" ")
        return if (buildingList[1] == "корп." || buildingList[1] == "ст." && building.isNotEmpty()) {
            buildingList[2].removeSuffix("\"")
        } else building
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
            _statusLoad.value = OrderLoadStatus.LOADING
            if (listOrderCRM.isNotEmpty()){
                listOrderCRM.forEach { order ->
                    var address = ""
                    when {
                        order.address_json.building == null -> {
                            if (order.address_json.entrance == 0 && order.address_json.floor == 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house}"
                            } else if (order.address_json.entrance == 0 && order.address_json.floor == 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} кв.${order.address_json.flat}"
                            } else if (order.address_json.entrance == 0 && order.address_json.floor != 0 && order.address_json.flat == 0){
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor}"
                            } else if (order.address_json.entrance != 0 && order.address_json.floor == 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance}"
                            } else if (order.address_json.entrance == 0 && order.address_json.floor != 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor} кв.${order.address_json.flat}"
                            } else if (order.address_json.entrance != 0 && order.address_json.floor == 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                            }
                        }
                        order.address_json.entrance == 0 -> {
                            if (order.address_json.building.isEmpty() && order.address_json.floor == 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house}"
                            } else if (order.address_json.building.isEmpty() && order.address_json.floor == 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} кв.${order.address_json.flat}"
                            } else if (order.address_json.building.isEmpty() && order.address_json.floor != 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor}"
                            } else if (order.address_json.building.isNotEmpty() && order.address_json.floor == 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building}"
                            } else if (order.address_json.building.isNotEmpty() && order.address_json.floor != 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} этаж${order.address_json.floor}"
                            } else if (order.address_json.building.isNotEmpty() && order.address_json.floor == 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} кв. ${order.address_json.flat}"
                            }
                        }
                        order.address_json.floor == 0 -> {
                            if (order.address_json.flat == 0 && order.address_json.building.isEmpty()) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance}"
                            } else if (order.address_json.flat == 0 && order.address_json.building.isNotEmpty()){
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} подьезд ${order.address_json.entrance}"
                            } else if (order.address_json.flat != 0 && order.address_json.building.isEmpty()) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                            } else if (order.address_json.flat != 0 && order.address_json.building.isNotEmpty()) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                            }
                        }
                        order.address_json.flat == 0 -> address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} этаж${order.address_json.floor}"
                        else -> address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} cт.${order.address_json.building} подьезд ${order.address_json.entrance} этаж${order.address_json.floor} кв.${order.address_json.flat}"
                    }
                    val listProduct = mutableListOf<Product>()
                    order.water_equip.forEach {
                        val product = orderRepo.getProduct(it.id)
                        if (product != null) {
                            product.count = it.count
                            listProduct.add(product)
                        }
                    }
                    Timber.d("ORDER_ID = ${order.order_id}")
                    val status = when (order.order_id) {
                        null -> 0
                        else -> {
                            val statusOrder = orderRepo.getStatusOrder(orderId = order.order_id)?.plus(1)
                            statusOrder ?: 1
                        }
                    }
                    listMyOrder.add(
                        MyOrder(address = address,
                            cash = order.order_cost,
                            date = "${order.date};${order.period}",
                            products = listProduct,
                            typeCash = order.payment_type,
                            status = status,
                            id = order.id)
                    )
                }
                _statusLoad.value = OrderLoadStatus.DONE
            } else {
                _statusLoad.value = OrderLoadStatus.ERROR
            }
            _listMyOrder.value = listMyOrder
        }
    }

    fun getOrderCrm(id: Int) {
        viewModelScope.launch {
            val orderFromCrm = orderRepo.getOrder(orderRepo.getAuthClient().clientId, id)
            Timber.d("ORDER SIZE ${orderFromCrm.size}")
            val myOrder = mutableListOf<MyOrder>()
            if (orderFromCrm.isNotEmpty()) {
                orderFromCrm.forEach { order ->
                    var address = ""
                    when {
                        order.address_json.building == null -> {
                            if (order.address_json.entrance == 0 && order.address_json.floor == 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house}"
                            } else if (order.address_json.entrance == 0 && order.address_json.floor == 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} кв.${order.address_json.flat}"
                            } else if (order.address_json.entrance == 0 && order.address_json.floor != 0 && order.address_json.flat == 0){
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor}"
                            } else if (order.address_json.entrance != 0 && order.address_json.floor == 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance}"
                            } else if (order.address_json.entrance == 0 && order.address_json.floor != 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor} кв.${order.address_json.flat}"
                            } else if (order.address_json.entrance != 0 && order.address_json.floor == 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                            }
                        }
                        order.address_json.entrance == 0 -> {
                            if (order.address_json.building.isEmpty() && order.address_json.floor == 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house}"
                            } else if (order.address_json.building.isEmpty() && order.address_json.floor == 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} кв.${order.address_json.flat}"
                            } else if (order.address_json.building.isEmpty() && order.address_json.floor != 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor}"
                            } else if (order.address_json.building.isNotEmpty() && order.address_json.floor == 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building}"
                            } else if (order.address_json.building.isNotEmpty() && order.address_json.floor != 0 && order.address_json.flat == 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} этаж${order.address_json.floor}"
                            } else if (order.address_json.building.isNotEmpty() && order.address_json.floor == 0 && order.address_json.flat != 0) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} кв. ${order.address_json.flat}"
                            }
                        }
                        order.address_json.floor == 0 -> {
                            if (order.address_json.flat == 0 && order.address_json.building.isEmpty()) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance}"
                            } else if (order.address_json.flat == 0 && order.address_json.building.isNotEmpty()){
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} подьезд ${order.address_json.entrance}"
                            } else if (order.address_json.flat != 0 && order.address_json.building.isEmpty()) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                            } else if (order.address_json.flat != 0 && order.address_json.building.isNotEmpty()) {
                                address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                            }
                        }
                        order.address_json.flat == 0 -> address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} этаж${order.address_json.floor}"
                        else -> address = "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} cт.${order.address_json.building} подьезд ${order.address_json.entrance} этаж${order.address_json.floor} кв.${order.address_json.flat}"
                    }
                    val listProduct = mutableListOf<Product>()
                    order.water_equip.forEach {
                        val product = orderRepo.getProduct(it.id)
                        if (product != null) {
                            product.count = it.count
                            listProduct.add(product)
                        }
                    }
                    val status = when (order.order_id) {
                        null -> 0
                        else -> {
                            val statusOrder = orderRepo.getStatusOrder(orderId = order.order_id)?.plus(1)
                            statusOrder ?: 1
                        }
                    }
                    myOrder.add(
                        MyOrder(address = address,
                            cash = order.order_cost,
                            date = "${order.date};${order.period}",
                            products = listProduct,
                            typeCash = order.payment_type,
                            status = status,
                            id = order.id)
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