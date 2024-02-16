package ru.iwater.youwater.vm

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import com.google.gson.JsonObject
import com.pusher.pushnotifications.PushNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.*
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.PaymentActivity
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.screen.navigation.PaymentNavRoute
import ru.iwater.youwater.utils.StatusData
import ru.iwater.youwater.utils.StatusPayment
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.Exception

@OnScreen
class WatterViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {

    val promoBanners: LiveData<List<PromoBanner>> = liveData {
        emit(repository.getPromoBanners())
    }

    val lastOrder: LiveData<Int?> = liveData {
        emit(getLastOrder())
    }

    private val _statusData: MutableLiveData<StatusData> = MutableLiveData()
    val statusData: LiveData<StatusData> get() = _statusData

    private var _productsList: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val productList: LiveData<List<Product>>
        get() = _productsList

    private val _catalogList = listOf<TypeProduct>().toMutableStateList()
    val catalogList: List<TypeProduct>
        get() = _catalogList

    private val _favoriteProductList: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val favoriteProductList: LiveData<List<Product>> get() = _favoriteProductList

    //продукт
    private val _product: MutableLiveData<Product?> = MutableLiveData()
    val product: LiveData<Product?> get() = _product

    //myOrder
    private val _ordersList = listOf<MyOrder>().toMutableStateList()
    val ordersList: List<MyOrder> get() = _ordersList


    //basket
    private val _productsInBasket = listOf<Product>().toMutableStateList()
    val productsInBasket: List<Product>
        get() = _productsInBasket

    private var _priceNoDiscount: MutableLiveData<Int> = MutableLiveData()
    val priceNoDiscount: LiveData<Int>
        get() = _priceNoDiscount

    private val _generalCost: MutableLiveData<Int> = MutableLiveData()
    val generalCost: LiveData<Int>
        get() = _generalCost

    //order
    private val _client: MutableLiveData<Client?> = MutableLiveData()
    val client: LiveData<Client?> = _client

    private var editClientName: String = ""
    private var editClientPhone: String = ""
    private var editClientEmail: String = ""

    private val _order: MutableLiveData<Order> = MutableLiveData(
        Order(
            clientId = 0,
            acqOrderId = 0,
            notice = "",
            waterEquip = mutableListOf(),
            period = "",
            orderCost = 0,
            paymentType = "",
            status = 0,
            email = "",
            contact = "",
            date = "",
            addressId = 0,
            name = "",
            dateCreate = java.sql.Date(Calendar.getInstance().timeInMillis)
        )
    )
    val order: LiveData<Order> get() = _order

    private val _createdOrder: MutableLiveData<MyOrder> = MutableLiveData(
        MyOrder("", "", "", emptyList(), "", -1, 0)
    )
    val completedOrder: LiveData<MyOrder> get() = _createdOrder

    private val disabledDays = mutableListOf<Common>()
    private val exceptions = mutableListOf<ru.iwater.youwater.data.Exception>()

    private val deliveryTime = mutableListOf<Common>()
    private val exceptionTime = mutableListOf<ru.iwater.youwater.data.Exception>()

    private val _addressList: MutableLiveData<List<RawAddress>> = MutableLiveData()
    val addressList: LiveData<List<RawAddress>> get() = _addressList

    private val _timesListOrder = listOf<String>().toMutableStateList()
    val timesListOrder: List<String> get() = _timesListOrder

    private val _addressesList = listOf<RawAddress>().toMutableStateList()
    val addressesList: List<RawAddress> get() = _addressesList

    private var _paymentStatus: MutableLiveData<StatusPayment> = MutableLiveData()
    val paymentStatus: LiveData<StatusPayment>
        get() = _paymentStatus

    private var telNumber = ""
    private var checkUrl = ""
    private var idOrderPay = ""

    init {
        getCatalogList()
    }

    private fun getCatalogList() {
        viewModelScope.launch {
            _catalogList.addAll(repository.getCategoryList())
        }
    }

    private fun getTelNumber() {
        viewModelScope.launch {
            val tel = repository.getClientInfo()?.contact
            Timber.d("tel $tel")
            telNumber = tel?.removeRange(2, 3)?.removeRange(5, 7)?.removeRange(8, 9).toString()
        }

    }

    fun getProductsList() {
        viewModelScope.launch {
            val favoriteList = repository.getFavorite()?.favorites_list?.map { it.toInt() }
            val productsList = repository.getProductList()
            productsList.forEach{ product ->
                product.onFavoriteClick = favoriteList?.contains(product.id) == true
            }
            _productsList.value = productsList
        }
    }

    fun getBasket() {
        viewModelScope.launch {
            _productsInBasket.clear()
            _productsInBasket.addAll(repository.getProductListOfCategory())
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    private fun getPriceNoDiscount() {
        var generalCostProducts = 0
        productsInBasket.forEach { product ->
            generalCostProducts += product.getPriceNoDiscount(product.count)
        }
        Timber.d("General cost no discount = $generalCostProducts")
        _priceNoDiscount.value = generalCostProducts
    }

    private fun getCostProduct() {
        var generalCostProducts = 0

        productsInBasket.forEach { product ->
            generalCostProducts += product.getPriceNoDiscount(product.count)
        }
        Timber.d("General cost no discount = $generalCostProducts")
        _generalCost.value = generalCostProducts
    }

    fun deleteProductFromBasket(productId: Int) {
        viewModelScope.launch {
            val product = _productsInBasket.find { it.id == productId }
            if (product != null) {
                repository.deleteProductFromBasket(product)
            }
            _productsInBasket.remove(product)
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    fun plusCountProduct(productId: Int) {
        viewModelScope.launch {
            val product = _productsInBasket.find { it.id == productId }
            if (product != null) {
                product.count += 1
                repository.updateProductInBasket(product)
            }
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    fun minusCountProduct(productId: Int) {
        viewModelScope.launch {
            val product = _productsInBasket.find { it.id == productId }
            if (product != null) {
                when {
                    product.count > 0 -> {
                        product.count -= 1
                        repository.updateProductInBasket(product)
                    }
                }
            }
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    fun getClient() {
        viewModelScope.launch {
            val client = repository.getClientInfo()
            if (client != null) {
                order.value?.clientId = client.client_id
                order.value?.name = client.name
                order.value?.contact = client.contact
                if (client.email.isNotEmpty()) order.value?.email = client.email
                _client.value = client
            }
        }
    }

    fun getClientInfo() {
        viewModelScope.launch {
            _client.value = repository.getClientInfo()
        }
    }

    fun deleteAccount(clientId: Int, phone: String, mainActivity: MainActivity) {
        viewModelScope.launch {
            val parametersForDelete = JsonObject()
            parametersForDelete.addProperty("phone", phone)
            val deleteMessage = repository.deleteAccount(clientId, parametersForDelete)
            if (deleteMessage != null && deleteMessage.status) {
                PushNotifications.clearAllState()
                val intent = Intent(mainActivity.applicationContext, StartActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                CoroutineScope(Dispatchers.Default).launch {
                    YouWaterDB.getYouWaterDB(mainActivity.applicationContext)?.clearAllTables()
                }
                exitClient()
                Toast.makeText(
                    mainActivity.applicationContext,
                    deleteMessage.message,
                    Toast.LENGTH_SHORT
                ).show()
                mainActivity.startActivity(intent)
            } else {
                Toast.makeText(
                    mainActivity.applicationContext,
                    "Ошибка соединения",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getAuthClient(): AuthClient = repository.getAuthClient()

    fun setEditClientName(clientName: String, clientPhone: String, clientEmail: String): Boolean {
        return if (!clientName.contains(Regex("""[^A-zА-я\s]"""))) {
            editClientName = clientName
            editClientPhone = clientPhone
            editClientEmail = clientEmail
            true
        } else {
            false
        }
    }

    fun setEditClientPhone(clientPhone: String, clientName: String, clientEmail: String): Boolean {
        return if (clientPhone.contains(Regex("""7\d{10}"""))) {
            editClientPhone = "+${clientPhone[0]}(${clientPhone[1]}${clientPhone[2]}${clientPhone[3]}) ${clientPhone[4]}${clientPhone[5]}${clientPhone[6]}-${clientPhone[7]}${clientPhone[8]}${clientPhone[9]}${clientPhone[10]}"
            editClientName = clientName
            editClientEmail = clientEmail
            true
        } else {
            false
        }
    }

    fun setEditClientEmail(clientEmail: String, clientName: String, clientPhone: String): Boolean {
        return if (clientEmail.contains(Regex("""(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})"""))) {
            editClientEmail = clientEmail
            editClientName = clientName
            editClientPhone = clientPhone
            true
        } else false
    }

    fun getNumberFromPhone(clientPhone: String): String {
        val listNumber = mutableListOf<Char>()
        val integerChars = '0'..'9'
        clientPhone.forEach { char ->
            if (char in integerChars) listNumber.add(char)
        }
        return listNumber.joinToString(
            ""
        )
    }

    fun editUserData(navHostController: NavHostController) {
        viewModelScope.launch {
            val clientId = repository.getClientInfo()?.client_id
            if (clientId != null) {
                val clientData = JsonObject()
                clientData.addProperty("name", editClientName)
                clientData.addProperty("contact", editClientPhone)
                clientData.addProperty("email", editClientEmail)
                val clientUserData = repository.editUserData(clientId, clientData)
                if (clientUserData) {
                    navHostController.navigate(MainNavRoute.UserDataScreen.withArgs(true.toString())) {
                        popUpTo(MainNavRoute.UserDataScreen.path) { inclusive = true }
                    }
                } else {
                    Toast.makeText(
                        navHostController.context,
                        "Ошибка, данные не были отправлены",
                        Toast.LENGTH_SHORT
                    ).show()
                    navHostController.navigate(MainNavRoute.UserDataScreen.withArgs(false.toString())) {
                        popUpTo(MainNavRoute.UserDataScreen.path) { inclusive = true }
                    }
                }
            } else {
                Toast.makeText(
                    navHostController.context,
                    "Ошибка, данные не были отправлены",
                    Toast.LENGTH_SHORT
                ).show()
                navHostController.navigate(MainNavRoute.UserDataScreen.withArgs(false.toString())) {
                    popUpTo(MainNavRoute.UserDataScreen.path) { inclusive = true }
                }
            }
        }
    }

    fun getAddressList() {
        viewModelScope.launch {
            _addressList.value = repository.getAddress()
        }
    }

    fun getStartDate(calendar: Calendar): Calendar {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minDate = Calendar.getInstance()

        if (hour < 12) {
            val yearMin = minDate.get(Calendar.YEAR)
            val monthMin = minDate.get(Calendar.MONTH)
            val dayMin = minDate.get(Calendar.DAY_OF_MONTH)
            minDate.set(yearMin, monthMin, dayMin - 1)
        } else if (hour in 12..15) {
            val yearMin = minDate.get(Calendar.YEAR)
            val monthMin = minDate.get(Calendar.MONTH)
            val dayMin = minDate.get(Calendar.DAY_OF_MONTH)
            minDate.set(yearMin, monthMin, dayMin, hour, 0)
        } else {
            val yearMin = minDate.get(Calendar.YEAR)
            val monthMin = minDate.get(Calendar.MONTH)
            val dayMin = minDate.get(Calendar.DAY_OF_MONTH)
            minDate.set(yearMin, monthMin, dayMin)
        }
        return minDate
    }

    fun getTimeList(timeMillis: Long, minDate: Calendar) {
        _timesListOrder.clear()
        val calendar = Calendar.getInstance()
        //дата заказа
        calendar.timeInMillis = timeMillis
        val orderDate = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val tomorrowDay = minDate.get(Calendar.DAY_OF_WEEK) + 1 //завтрашняя дата
        val thisHour = minDate.get(Calendar.HOUR_OF_DAY) // который час
        val selectDay = calendar.get(Calendar.DAY_OF_WEEK) // выбранная дата
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale("ru")).format(Date(Calendar.getInstance().timeInMillis)) // сегодняшняя дата
        val date = SimpleDateFormat("yyyy-MM-dd", Locale("ru")).format(Date(timeMillis)) // выбранная дата
        _order.value?.date = date
        val exception = exceptionTime.filter { it.date == date }
        val commons = deliveryTime.filter { it.day_num == orderDate }
        val times = if (commons.isNotEmpty() || exception.isNotEmpty()) {
            if (exception.isNotEmpty()) {
                addTime(exception[0].part_types)
            } else {
                Timber.d("Common ${commons[0].part_types.size}")
                commons[0].part_types.forEach {
                    Timber.d("part_types = $it")
                }
                when {
                    todayDate == date -> { //выбрана сегодняшняя дата
                        addTime(listOf(1)) //только вечер
                    }
                    tomorrowDay == selectDay && thisHour < 16 -> { // если заказ на завтра и время меньше 16
                        addTime(listOf(0, 1)) // любое время
                    }
                    tomorrowDay == selectDay && thisHour > 15 -> { // если заказ на завтра и время больше 16
                        addTime(listOf(1)) // вечер
                    }
                    else -> {
                        addTime(commons[0].part_types) // иначе как указано в графике
                    }
                }
            }
        } else emptyList()
        _timesListOrder.addAll(times)
    }

    fun disableDays(utcTimeMills: Long): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = utcTimeMills
        var monday = isAvailableDay(disabledDays.find { day -> day.day_num == 1 }, calendar)
        var tuesday = isAvailableDay(disabledDays.find { day -> day.day_num == 2 }, calendar)
        var wednesday = isAvailableDay(disabledDays.find { day -> day.day_num == 3 }, calendar)
        var thursday = isAvailableDay(disabledDays.find { day -> day.day_num == 4 }, calendar)
        var friday = isAvailableDay(disabledDays.find { day -> day.day_num == 5 }, calendar)
        var saturday = isAvailableDay(disabledDays.find { day -> day.day_num == 6 }, calendar)
        var sunday = isAvailableDay(disabledDays.find { day -> day.day_num == 7 }, calendar)
        Timber.d("MONDAY = ${monday} TUESDAY = ${thursday} WEDNESDAY = ${wednesday} THURSDAY = ${thursday} FRIDAY = ${friday} SATURDAY = ${saturday} SUNDAY = ${sunday}")
        exceptions.forEach { exceptionDay ->
            if (exceptionDay.available) {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale("ru")).format(calendar.time)
                if (date == exceptionDay.date) {
                    val dates = date.split("-")
                    val year = dates[0].toInt()
                    val month = dates[1].toInt()
                    val day = dates[2].toInt()
                    val exceptionsDate = Calendar.getInstance()
                    exceptionsDate.set(year, month, day)
                    Timber.d("YEAR = $year, month = $month, day = $day")
                    Timber.d("Exception date = ${exceptionsDate.time}")
                    when (exceptionDay.day_num) {
                        1 -> monday = calendar[Calendar.DAY_OF_WEEK] == Calendar.MONDAY
                        2 -> tuesday = calendar[Calendar.DAY_OF_WEEK] == Calendar.TUESDAY
                        3 -> wednesday = calendar[Calendar.DAY_OF_WEEK] == Calendar.WEDNESDAY
                        4 -> thursday = calendar[Calendar.DAY_OF_WEEK] == Calendar.THURSDAY
                        5 -> friday = calendar[Calendar.DAY_OF_WEEK] == Calendar.FRIDAY
                        6 -> saturday = calendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY
                        7 -> sunday = calendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY
                    }
                }
            }
        }

        return monday && tuesday && wednesday && thursday && friday && saturday && sunday
    }

    private fun isAvailableDay(common: Common?, calendar: Calendar): Boolean {
        return if (common != null) {
            if (common.available) {
                true
            } else {
                when (common.day_num) {
                    1 -> calendar[Calendar.DAY_OF_WEEK] != Calendar.MONDAY
                    2 -> calendar[Calendar.DAY_OF_WEEK] != Calendar.TUESDAY
                    3 -> calendar[Calendar.DAY_OF_WEEK] != Calendar.WEDNESDAY
                    4 -> calendar[Calendar.DAY_OF_WEEK] != Calendar.THURSDAY
                    5 -> calendar[Calendar.DAY_OF_WEEK] != Calendar.FRIDAY
                    6 -> calendar[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY
                    7 -> calendar[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY
                    else -> {
                        true
                    }
                }
            }
        } else {
            false
        }
    }

    private fun addTime(partTypes: List<Int>): MutableList<String> {
        return when {
            partTypes.size == 2 -> {
                arrayListOf("09:00-16:00", "17:00-22:00", "19:00-22:00")
            }

            partTypes[0] == 0 -> {
                arrayListOf("09:00-16:00")
            }

            else -> {
                arrayListOf("17:00-22:00", "19:00-22:00")
            }
        }
    }

    fun getDeliveryOnAddress(address: RawAddress) {
        viewModelScope.launch {
            disabledDays.clear()
            exceptions.clear()
            order.value?.date = ""
            order.value?.period = ""
            order.value?.addressId = address.id
            val delivery = repository.getDelivery(address)
            if (delivery != null) {
                disabledDays.addAll(delivery.common)
                exceptions.addAll(delivery.exceptions)
                deliveryTime.addAll(
                    delivery.common.filter { it.available }
                )
                exceptionTime.addAll(
                    delivery.exceptions.filter { it.available }
                )
            }
        }
    }

    fun setTimeOrder(timeOrder: String) {
        _order.value?.period = timeOrder
        Timber.d("Order period = $order")
    }

    fun setTypePeyOrder(typeOrder: String) {
        when (typeOrder) {
            "Оплата по карте курьеру" -> _order.value?.paymentType = "4"
            "Оплата наличными" -> _order.value?.paymentType = "0"
            "Оплата онлайн" -> _order.value?.paymentType = "2"
        }
    }

    fun setNoticeOrder(notice: String) {
        _order.value?.notice = notice
    }

    fun isTrueOrder(order: Order?): Boolean {
        return order?.clientId != 0 &&
                order?.period?.isNotEmpty() == true && order.period != "**:**-**:**" &&
                !order.paymentType.isNullOrEmpty() && order.paymentType != "Выберите способ оплаты" &&
                !order.email.isNullOrEmpty() && order.name.isNotEmpty() && order.date.isNotEmpty() && order.orderCost == 0
    }

    private fun refreshOrder() {
        _order.value = Order(
            clientId = 0,
            acqOrderId = 0,
            notice = "",
            waterEquip = mutableListOf(),
            period = "",
            orderCost = 0,
            paymentType = "",
            status = 0,
            email = "",
            contact = "",
            date = "",
            addressId = 0,
            name = "",
            dateCreate = java.sql.Date(Calendar.getInstance().timeInMillis)
        )
    }

    fun sendAndSaveOrder(order: Order?, orderCost: Int, navController: NavHostController) {
        viewModelScope.launch {
            if (order != null) {
                _order.value?.orderCost = orderCost
                order.orderCost = orderCost
                _productsInBasket.forEach { product ->
                    val productJson =
                        getJsonProduct(product, product.getPriceNoDiscount(product.count)/product.count)
                    order.waterEquip.add(productJson)
                }
                val orderId = repository.createOrderApp(order)
                if (order.paymentType != "2") {
                    navController.navigate(
                        MainNavRoute.CompleteOrderScreen.withArgs(
                            orderId.toString(),
                            false.toString()
                        )
                    ) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                    }
                    navController.clearBackStack(MainNavRoute.CreateOrderScreen.path)
                    refreshOrder()
                } else {
                    Timber.d("Order - $orderId, $orderCost")
                    getTelNumber()
                    refreshOrder()
                    navController.clearBackStack(MainNavRoute.CreateOrderScreen.path)
                    PaymentActivity.startGenerateToken(
                        navController.context,
                        orderId,
                        orderCost,
                        telNumber
                    )
                }
            }
        }
    }

    fun createPay(
        orderId: Int,
        amount: String,
        description: String,
        paymentToken: String,
        capture: Boolean
    ) {
        viewModelScope.launch {
            val dataPayment = repository.createPay(
                amount, description, paymentToken, capture
            )
            when {
                dataPayment != null && dataPayment.data.status == "succeeded" -> {
                    Timber.d("succeeded")
                    _paymentStatus.value = StatusPayment.DONE
                }

                dataPayment != null && dataPayment.data.status == "pending" -> {
                    Timber.d("Check pay")
                    _paymentStatus.value = StatusPayment.PANDING
                    val parameters = JsonObject()
                    parameters.addProperty("updated_payment_state", 1)
                    parameters.addProperty("updated_acq", dataPayment.data.id)
                    repository.setStatusPayment(orderId = orderId, parameters)
                    checkUrl = dataPayment.data.confirmation.confirmation_url
                    idOrderPay = dataPayment.data.payment_method.id
                }

                else -> {
                    errorPay()
                }
            }
        }
    }

    fun setPaymentStatus(navController: NavHostController) {
        viewModelScope.launch {
            Timber.d("start Get order pay $idOrderPay")
            val resultPay = repository.getOrderPayStatus(idOrderPay)
            if (resultPay) {
                _paymentStatus.value = StatusPayment.DONE
                navController.navigate(
                    PaymentNavRoute.CompleteOrderScreen.path
                )
            }
            else {
                _paymentStatus.value = StatusPayment.ERROR
                navController.navigate(
                    PaymentNavRoute.CompleteOrderScreen.path
                )
            }

        }
    }

    fun getCheckUrl() = checkUrl

    fun startPay() {
        _paymentStatus.value = StatusPayment.LOAD
    }

    fun errorPay() {
        _paymentStatus.value = StatusPayment.ERROR
    }

    fun getInfoLastOrder(orderId: Int) {
        viewModelScope.launch {
            if (orderId != 0) {
                val repeatOrder = repository.getOrder(orderId)
                if (repeatOrder != null) {
                    _productsInBasket.clear()
//                    clearBasket()
                    val addressList = repository.getAddress()
                    val address = addressList.find { it.id == repeatOrder.address_id }
                    if (address != null) {
                        getDeliveryOnAddress(address)
                    }
                    Timber.d("Product Size = ${repeatOrder.water_equip.size}")
                    val products = repeatOrder.water_equip.map {
                        val product = repository.getProduct(it.id)
                        product?.count = it.amount
                        product
                    }
                    _productsInBasket.addAll(products.filterNotNull().filter { it.category != 20 })
                    getCostProduct()
                    getPriceNoDiscount()
                    _order.value?.paymentType = repeatOrder.payment_type
                    _order.value?.notice = repeatOrder.notice
                }
            }
        }
    }

    fun getOrderCrm(orderId: Int) {
        viewModelScope.launch {
            Timber.d("GET ORDER CRM")
            val orderFromCrm = repository.getOrder(orderId)
            Timber.d("OrderCRM ${orderFromCrm?.id}")
            if (orderFromCrm != null) {
//                val address = getStringAddress(order)
                Timber.d("GET ORDER CRM 1")
                val listProduct = mutableListOf<Product>()
                orderFromCrm.water_equip.forEach {
                    val product = repository.getProduct(it.id)
                    if (product != null) {
                        if (product.category != 20) {
                            product.count = it.amount
                            listProduct.add(product)
                        }
                    }
                }

                _createdOrder.value = MyOrder(
                    address = orderFromCrm.address,
                    cash = orderFromCrm.order_cost,
                    date = "${orderFromCrm.date};${orderFromCrm.period}",
                    products = listProduct,
                    typeCash = orderFromCrm.payment_type,
                    status = orderFromCrm.status,
                    id = orderFromCrm.id
                )
            }
        }

    }

    fun clearBasket() {
        viewModelScope.launch {
            val products = repository.getProductListOfCategory()
            products.forEach {
                repository.deleteProductFromBasket(it)
            }
        }
    }

    private fun getJsonProduct(product: Product, priceOne: Int): JsonObject {
        val productJs = JsonObject()
        productJs.addProperty("id", product.id)
        productJs.addProperty("name", product.name)
        productJs.addProperty("price", priceOne)
        productJs.addProperty("amount", product.count)
        return productJs
    }

    fun getOrderCrm() {
        viewModelScope.launch {
            _ordersList.clear()
            _statusData.value = StatusData.LOAD
            val ordersListFromCrm = repository.getOrdersList()
            if (ordersListFromCrm.isNotEmpty()) {
                ordersListFromCrm.forEach { order ->
                    val listProduct = mutableListOf<Product>()
                    order.water_equip.forEach {
                        val product = repository.getProduct(it.id)
                        if (product != null) {
                            if (product.category != 20) {
                                product.count = it.amount
                                listProduct.add(product)
                            }
                        }
                    }
                    _ordersList.add(
                        MyOrder(
                            address = order.address,
                            cash = order.order_cost,
                            date = "${order.date};${order.period}",
                            products = listProduct,
                            typeCash = order.payment_type,
                            status = order.status,
                            id = order.id
                        )
                    )

                }
            }
            _statusData.value = StatusData.DONE
        }
    }

    fun getAddressesList() {
        viewModelScope.launch {
            _addressesList.clear()
            val addresses = repository.getAddress()
            if (addresses.isNotEmpty()) {
                _addressesList.addAll(addresses)
            }
        }
    }

    fun inActiveAddress(id: Int) {
        viewModelScope.launch {
            repository.inactiveAddress(id)
            getAddressesList()
        }
    }

    /**
     * создать новый адрес
     */
    fun createNewAddress(
        region: String,
        city: String,
        street: String,
        house: String,
        building: String,
        entrance: String,
        floor: String,
        flat: String,
        contact: String,
        notice: String,
        isFromOrder: Boolean,
        navController: NavHostController
        //-----

    ) {
        viewModelScope.launch {
            val client = repository.getClientInfo()
            val newAddressParameters = JsonObject()
            val factAddress = getFactAddress(
                city = city,
                street = street,
                house = house,
                building = building,
                entrance = entrance,
                floor = floor,
                flat = flat
            )
            val addressJson = getJsonAddress(
                region = region,
                city = city,
                street = street,
                house = house,
                building = building,
                entrance = entrance,
                floor = floor,
                flat = flat
            )
            val fullAddress =
                getFullAddress(region = region, street = street, house = house, building = building)
            val address = getAddress(street = street, house = house, building = building)
            if (client != null) {
                newAddressParameters.apply {
                    this.addProperty("client_id", client.client_id)
                    this.addProperty("name_contact", client.name)
                    this.addProperty("phone_contact", client.contact)
                    this.addProperty("notice", notice)
                    this.addProperty("region", region)
                    this.addProperty("address", address)
                    this.addProperty("fact_address", factAddress)
                    this.addProperty("full_address", fullAddress)
                    this.addProperty("return_tare", 0)
                    this.addProperty("coords", "")
                    this.addProperty("active", 1)
                    this.add("address_json", addressJson)
                }
                val newAddress =
                    if (contact.isEmpty()) {
                        newAddressParameters.addProperty("contact", client.contact)
                        repository.createAddress(
                            newAddressParameters
                        )
                    } else {
                        newAddressParameters.addProperty("contact", contact)
                        repository.createAddress(
                            newAddressParameters
                        )
                    }
                if (newAddress == "Адрес успешно добавлен.") {
                    if (isFromOrder) navController.navigate(
//                        AddAddressFragmentDirections.actionAddAddressFragmentToCreateOrderFragment(false, 0)
                        MainNavRoute.CreateOrderScreen.withArgs(false.toString(), "0")
                    ) else navController.navigate(MainNavRoute.AddressesScreen.path)
                } else {
                    Toast.makeText(
                        navController.context,
                        "Ошибка, данные не были отправлены, возможно проблемы с интернетом",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    navController.context,
                    "Не был указан корректный адрес",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getFactAddress(
        city: String,
        street: String,
        house: String,
        building: String,
        entrance: String,
        floor: String,
        flat: String,
    ) = when {
        //5
        flat.isEmpty() && floor.isEmpty() && entrance.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house"
        //4
        flat.isEmpty() && floor.isEmpty() && entrance.isEmpty() && building.isEmpty() -> "$city, $street, д. $house"
        flat.isEmpty() && floor.isEmpty() && entrance.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building"
        flat.isEmpty() && floor.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house,подъезд $entrance"
        flat.isEmpty() && entrance.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house, этаж $floor"
        floor.isEmpty() && entrance.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house, кв. $flat"
        //3
        flat.isEmpty() && floor.isEmpty() && entrance.isEmpty() -> "$city, $street, д. $house, корп. $building"
        flat.isEmpty() && floor.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, подъезд $entrance"
        flat.isEmpty() && floor.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, подъезд $entrance"
        flat.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house, подъезд $entrance, этаж $floor"
        flat.isEmpty() && entrance.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, этаж $floor"
        flat.isEmpty() && entrance.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, этаж $floor, кв. $flat"
        entrance.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house, этаж $floor, кв. $flat"
        entrance.isEmpty() && building.isEmpty() && floor.isEmpty() -> "$city, $street, д. $house, кв. $flat"
        entrance.isEmpty() && floor.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, кв. $flat"
        building.isEmpty() && floor.isEmpty() && city.isEmpty() -> "$street, д. $house,подъезд $entrance, кв. $flat"
        //2
        flat.isEmpty() && floor.isEmpty() -> "$city, $street, д. $house, корп. $building, подъезд $entrance"
        flat.isEmpty() && entrance.isEmpty() -> "$city, $street, д. $house, корп. $building, этаж $floor"
        flat.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, подъезд $entrance, этаж $floor"
        flat.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, подъезд $entrance, этаж $floor"
        floor.isEmpty() && entrance.isEmpty() -> "$city, $street, д. $house, корп. $building, кв. $flat"
        floor.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, подъезд $entrance, кв. $flat"
        floor.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, подъезд $entrance, кв. $flat"
        entrance.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, этаж $floor, кв. $flat"
        entrance.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, этаж $floor, кв. $flat"
        building.isEmpty() && city.isEmpty() -> "$street, д. $house, подъезд $entrance, этаж $floor, кв. $flat"
        //1
        flat.isEmpty() -> "$city, $street, д. $house, корп. $building, подъезд $entrance, этаж $floor"
        floor.isEmpty() -> "$city, $street, д. $house, корп. $building, подъезд $entrance, кв. $flat"
        entrance.isEmpty() -> "$city, $street, д. $house, корп. $building, этаж $floor, кв. $flat"
        building.isEmpty() -> "$city, $street, д. $house, подъезд $entrance, этаж $floor, кв. $flat"
        city.isEmpty() -> "$street, д. $house, корп. $building, подъезд $entrance, этаж $floor, кв. $flat"
        else -> "$city, $street, д. $house, корп. $building, подъезд $entrance, этаж $floor, кв. $flat"
    }

    private fun getFullAddress(region: String, street: String, house: String, building: String) =
        when {
            building.isEmpty() -> "$region, $street д. $house"
            else -> "$region, $street д. $house корп. $building"
        }

    private fun getAddress(street: String, house: String, building: String) =
        when {
            building.isEmpty() -> "$street д. $house"
            else -> "$street д. $house корп. $building"
        }

    private fun getJsonAddress(
        region: String,
        city: String,
        street: String,
        house: String,
        building: String,
        entrance: String,
        floor: String,
        flat: String,
    ): JsonObject? {
        val addressJson = JsonObject()
        if (region.isNotEmpty()) {
            addressJson.addProperty("region", region)
        } else {
            return null
        }
        if (street.isEmpty()) {
            return null
        } else {
            addressJson.addProperty("street", street)
        }
        if (house.isEmpty()) {
            return null
        } else {
            addressJson.addProperty("house", house)
        }
        addressJson.addProperty("city", city)
        addressJson.addProperty("building", building)
        addressJson.addProperty("entrance", entrance)
        addressJson.addProperty("floor", floor)
        addressJson.addProperty("flat", flat)
        return addressJson
    }

    fun setMailing(clientId: Int, isMailing: Boolean) {
        viewModelScope.launch {
            repository.setMailing(clientId, isMailing)
            getClientInfo()
        }
    }


    fun getFavoriteProductList() {
        viewModelScope.launch {
            val favoriteProductList = mutableListOf<Product>()
            _statusData.value = StatusData.LOAD
            val favoriteList = repository.getFavorite()?.favorites_list?.map { it.toInt() }
            val products = repository.getProductList()
            favoriteList?.forEach { favoriteId ->
                products.find { it.id == favoriteId }?.let { favoriteProductList.add(it) }
            }
            favoriteProductList.forEach { product ->
                product.onFavoriteClick = true
            }
            _favoriteProductList.value = favoriteProductList
            _statusData.value = StatusData.DONE
        }
    }

    //инициалезация товара
    fun initProduct(productId: Int) {
        viewModelScope.launch {
            val product = repository.getProduct(productId)
            if (product != null) {
                product.count = 1
                _product.value = product
            }
        }
    }

    fun addProductCountToBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = repository.getProductFromDB(product.id)
            try {
                if (dbProduct == null) {
                    repository.addProductInBasket(product = product)
                } else {
                    repository.updateProductInBasket(product = product)
                }
            } catch (e: Exception) {
                Timber.d("Error add in basket: $e")
            }
        }
    }

    fun addProductToBasket(product: Product) {
        viewModelScope.launch {
            val dbProduct = repository.getProductFromDB(product.id)
            try {
                if (dbProduct != null && dbProduct.category != 20) {
                    dbProduct.count += 1
                    repository.updateProductInBasket(dbProduct)
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

    fun onChangeFavorite(productId: Int, onFavorite: Boolean) {
        viewModelScope.launch {
            val productsList = _productsList.value
            if (onFavorite) repository.deleteFavorite(productId)
            else repository.addToFavoriteProduct(productId)
            productsList?.find { product -> product.id == productId }?.onFavoriteClick = !onFavorite
            _productsList.value = productsList ?: emptyList()
        }

    }

    private suspend fun getLastOrder(): Int? {
        return repository.getLastOrder()
    }

    fun exitClient() {
        PushNotifications.clearAllState()
        PushNotifications.clearDeviceInterests()
        repository.deleteClient()
    }

}