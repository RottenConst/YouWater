package ru.iwater.youwater.data

import androidx.lifecycle.*
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.OrderRepository
import timber.log.Timber
import javax.inject.Inject

enum class Status { SEND, ERROR }
enum class PaymentStatus { SUCCESSFULLY, ERROR }
enum class StatusLoading { LOADING, DONE, EMPTY, ERROR }

class OrderViewModel @Inject constructor(
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val authClient = orderRepo.getAuthClient()

    //данные клиета
    private val _client: MutableLiveData<Client> = MutableLiveData()
    val client: LiveData<Client>
        get() = _client

    //адрес доставки
    val rawAddress: LiveData<List<RawAddress>> = liveData {
        emit(getRawAddress())
    }

    private val _address: MutableLiveData<String> = MutableLiveData()
    val address: LiveData<String> get() = _address

    //список продуктов заявки
    private val _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: LiveData<List<Product>>
        get() = _products

    //список заказов клиента
    private val _listMyOrder: MutableLiveData<List<MyOrder>> = MutableLiveData()
    val listMyOrder: LiveData<List<MyOrder>>
        get() = _listMyOrder

    //последний заказ клиента
    private val _myOrder: MutableLiveData<OrderFromCRM?> = MutableLiveData()
    val myOrder: LiveData<OrderFromCRM?>
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
    private val _statusLoad: MutableLiveData<StatusLoading> = MutableLiveData()
    val statusLoad: LiveData<StatusLoading>
        get() = _statusLoad

    init {
        getClient()
        getProducts()
    }

    private fun getClient() {
        viewModelScope.launch {
            val client = orderRepo.getClientInfo(authClient.clientId)
            if (client != null) _client.value = client
        }
    }

    private suspend fun getRawAddress(): List<RawAddress> {
        val listNetAddress = orderRepo.getAllFactAddress()
        for (rawAddress in listNetAddress) {
            val address = orderRepo.getAddress(rawAddress.id)
            if (address == null) {
                orderRepo.saveAddress(rawAddress)
            }
        }
        return orderRepo.getAllAddress()
    }

    fun getAddressFromString(rawAddress: List<String>, region: String, id: Int, notice: String?): Address {
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
//                flat = parseFlat(s, flat)
            }
        }
        Timber.d("$region $street $house $building $entrance $floor $flat, $notice")
        return Address(region, street, house, building, entrance, floor, flat, notice , id)
    }

    private fun parseHouse(string: String, house: Int): Int {
        val houseList = string.split(" ")
        return if (houseList[1] == "д." && house == 0) {
            houseList[2].removeSuffix("\"").toInt()
        } else house
    }

    private fun parseEntrance(string: String, entrance: Int?): Int? {
        val entranceList = string.split(" ")
        return if (entranceList[1] == "пд." && entrance == null) {
            entranceList[2].removeSuffix("\"").toInt()
        } else entrance
    }

    private fun parseFloor(string: String, floor: Int?): Int? {
        val floorList = string.split(" ")
        return if (floorList[1] == "эт." && floor == null) {
            floorList[2].removeSuffix("\"").toInt()
        } else floor
    }

    private fun parseFlat(string: String, flat: Int?): Int? {
        val flatList = string.split(" ")
        return if (flatList[1] == "кв." && flat == null) {
            flatList[2].removeSuffix("\"").toInt()
        } else flat
    }

    private fun parseBuilding(string: String, building: String): String {
        val buildingList = string.split(" ")
        return if (buildingList[1] == "корп." || buildingList[1] == "ст." && building.isNotEmpty()) {
            buildingList[2].removeSuffix("\"")
        } else building
    }

    private fun getProducts() {
        viewModelScope.launch {
            val isFirstOrder = orderRepo.isFirstOrder()
            if (isFirstOrder != null) {
                if (isFirstOrder) {
                    _products.value = orderRepo.getAllProduct()
                } else {
                    val products = orderRepo.getAllProduct().filter { product -> product.category != 20 }
                    _products.value = products
                }
            }
        }
    }

    fun clearProduct(products: List<Product>) {
        viewModelScope.launch {
            for (product in products) {
                orderRepo.deleteProduct(product)
            }
        }
    }

    fun getOrderFromCrm() {
        viewModelScope.launch {
            val listOrderCRM = orderRepo.getAllOrder(orderRepo.getAuthClient().clientId)
            Timber.d("LIST ORDER FROM CRM ${listOrderCRM.size}")
            val listMyOrder = mutableListOf<MyOrder>()
            _statusLoad.value = StatusLoading.LOADING
            if (listOrderCRM.isNotEmpty()) {
                listOrderCRM.forEach { order ->
                    Timber.d("ORDER ADDRESS ID = ${order.address_id}")
                    val address = getStringAddress(order)

                        val listProduct = mutableListOf<Product>()
                        order.water_equip.forEach {
                            val product = orderRepo.getProduct(it.id)
                            if (product != null) {
                                product.count = it.amount
                                listProduct.add(product)
                            }
                        }
                        Timber.d("ORDER_ID = ${order.order_id}")
                        val status = when (order.order_id) {
                            null -> 0
                            else -> {
                                val statusOrder =
                                    orderRepo.getStatusOrder(orderId = order.order_id)?.plus(1)
                                statusOrder ?: 1
                            }
                        }
                        listMyOrder.add(
                            MyOrder(
                                address = address,
                                cash = order.order_cost,
                                date = "${order.date};${order.period}",
                                products = listProduct,
                                typeCash = order.payment_type,
                                status = status,
                                id = order.id
                            )
                        )

                }
                _statusLoad.value = StatusLoading.DONE
            } else if (listOrderCRM.isEmpty()) {
                _statusLoad.value = StatusLoading.EMPTY
            } else {
                _statusLoad.value = StatusLoading.ERROR
            }
            _listMyOrder.value = listMyOrder
        }
    }

    private suspend fun getStringAddress(order: OrderFromCRM): String {
        return if (order.address_id != 0) {
            val rawAddress = orderRepo.getFactAddress(order.address_id)
            Timber.d("ADDRESS + ${rawAddress?.factAddress}")
            rawAddress?.factAddress ?: "error"
        } else {
            getStringAddressFromJson(order)
        }
    }

    private fun getStringAddressFromJson(order: OrderFromCRM): String {
        var address = ""
        when {
            order.address_json.building == null -> {
                if (order.address_json.entrance == 0 && order.address_json.floor == 0 && order.address_json.flat == 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house}"
                } else if (order.address_json.entrance == 0 && order.address_json.floor == 0 && order.address_json.flat != 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} кв.${order.address_json.flat}"
                } else if (order.address_json.entrance == 0 && order.address_json.floor != 0 && order.address_json.flat == 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor}"
                } else if (order.address_json.entrance != 0 && order.address_json.floor == 0 && order.address_json.flat == 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance}"
                } else if (order.address_json.entrance == 0 && order.address_json.floor != 0 && order.address_json.flat != 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor} кв.${order.address_json.flat}"
                } else if (order.address_json.entrance != 0 && order.address_json.floor == 0 && order.address_json.flat != 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                }
            }
            order.address_json.entrance == 0 -> {
                if (order.address_json.building.isEmpty() && order.address_json.floor == 0 && order.address_json.flat == 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house}"
                } else if (order.address_json.building.isEmpty() && order.address_json.floor == 0 && order.address_json.flat != 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} кв.${order.address_json.flat}"
                } else if (order.address_json.building.isEmpty() && order.address_json.floor != 0 && order.address_json.flat == 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} этаж${order.address_json.floor}"
                } else if (order.address_json.building.isNotEmpty() && order.address_json.floor == 0 && order.address_json.flat == 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building}"
                } else if (order.address_json.building.isNotEmpty() && order.address_json.floor != 0 && order.address_json.flat == 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} этаж${order.address_json.floor}"
                } else if (order.address_json.building.isNotEmpty() && order.address_json.floor == 0 && order.address_json.flat != 0) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} кв. ${order.address_json.flat}"
                }
            }
            order.address_json.floor == 0 -> {
                if (order.address_json.flat == 0 && order.address_json.building.isEmpty()) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance}"
                } else if (order.address_json.flat == 0 && order.address_json.building.isNotEmpty()) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} подьезд ${order.address_json.entrance}"
                } else if (order.address_json.flat != 0 && order.address_json.building.isEmpty()) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                } else if (order.address_json.flat != 0 && order.address_json.building.isNotEmpty()) {
                    address =
                        "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} ст. ${order.address_json.building} подьезд ${order.address_json.entrance} кв.${order.address_json.flat}"
                }
            }
            order.address_json.flat == 0 -> address =
                "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} подьезд ${order.address_json.entrance} этаж${order.address_json.floor}"
            else -> address =
                "${order.address_json.region} ул.${order.address_json.street} д.${order.address_json.house} cт.${order.address_json.building} подьезд ${order.address_json.entrance} этаж${order.address_json.floor} кв.${order.address_json.flat}"
        }
        return address
    }

    fun getOrderCrm(id: Int) {
        viewModelScope.launch {
            val orderFromCrm = orderRepo.getOrder(orderRepo.getAuthClient().clientId, id)
            Timber.d("ORDER SIZE ${orderFromCrm.size}")
            val myOrder = mutableListOf<MyOrder>()
            if (orderFromCrm.isNotEmpty()) {
                orderFromCrm.forEach { order ->
                    val address = getStringAddress(order)

                    val listProduct = mutableListOf<Product>()
                    order.water_equip.forEach {
                        val product = orderRepo.getProduct(it.id)
                        if (product != null) {
                            if (product.category != 20) {
                                product.count = it.amount
                                listProduct.add(product)
                            }
                        }
                    }
                    val status = when (order.order_id) {
                        null -> 0
                        else -> {
                            val statusOrder =
                                orderRepo.getStatusOrder(orderId = order.order_id)?.plus(1)
                            statusOrder ?: 1
                        }
                    }
                    myOrder.add(
                        MyOrder(
                            address = address,
                            cash = order.order_cost,
                            date = "${order.date};${order.period}",
                            products = listProduct,
                            typeCash = order.payment_type,
                            status = status,
                            id = order.id
                        )
                    )
                }
            }
            _listMyOrder.value = myOrder
        }
    }

    fun sendAndSaveOrder(order: Order) {
        viewModelScope.launch {
            val answer = orderRepo.createOrderApp(order)
            Timber.d("ОТВЕТ $answer")
            if (answer != "error") {
                _numberOrder.value = answer
                _statusOrder.value = Status.SEND
            } else {
                _statusOrder.value = Status.ERROR
            }
        }
    }

    fun getInfoLastOrder(orderId: Int) {
        viewModelScope.launch {
            if (orderId != 0) {
                val lastOrder = orderRepo.getLastOrderInfo(orderId)
                if (lastOrder != null) {
                    Timber.d("ADRESS ID = ${lastOrder.address_id}")
                    if (lastOrder.address_id != 0) {
                        _address.value = orderRepo.getFactAddress(lastOrder.address_id)?.factAddress
                    } else {
                        _address.value = "Адрес устарел, выберете другой"
                    }
                    _myOrder.value = lastOrder
                    val products = mutableListOf<Product>()
                    lastOrder.water_equip.forEach {
                        val product = orderRepo.getProduct(it.id)
                        if (product != null) {
                            product.count = it.amount
                            products.add(product)
                        }
                    }
                    _products.value = products
                }
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
}