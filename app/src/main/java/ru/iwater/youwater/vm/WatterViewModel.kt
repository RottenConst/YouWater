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

    val promoBanners: LiveData<List<Banner>> = liveData {
        emit(repository.getPromoBanners())
    }

    val lastOrder: LiveData<Int?> = liveData {
        emit(getLastOrder())
    }

    private val _statusData: MutableLiveData<StatusData> = MutableLiveData()
    val statusData: LiveData<StatusData> get() = _statusData

    private var _productsList: MutableLiveData<List<NewProduct>> = MutableLiveData(emptyList())
    val productList: LiveData<List<NewProduct>>
        get() = _productsList

    private val _catalogList = listOf<TypeProduct>().toMutableStateList()
    val catalogList: List<TypeProduct>
        get() = _catalogList

    private val _favoriteProductList: MutableLiveData<List<NewProduct>> = MutableLiveData(emptyList())
    val favoriteProductList: LiveData<List<NewProduct>> get() = _favoriteProductList

    //продукт
    private val _product: MutableLiveData<InfoProduct?> = MutableLiveData()
    val product: LiveData<InfoProduct?> get() = _product

    private val _measureList: MutableLiveData<List<Measure>> = MutableLiveData(emptyList())
    val measureList: LiveData<List<Measure>> get() = _measureList

    //myOrder
    private val _ordersList = listOf<MyOrder>().toMutableStateList()
    val ordersList: List<MyOrder> get() = _ordersList


    //basket
    private val _productsInBasket = listOf<NewProduct>().toMutableStateList()
    val productsInBasket: List<NewProduct>
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
            date = "",
            notice = "",
            timeFrom = "",
            timeTo = "",
            totalCost = 0,
            paymentType = "",
            productList = mutableListOf(),
            addressId = 0
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

    private val _addressList: MutableLiveData<List<NewAddress>> = MutableLiveData()
    val addressList: LiveData<List<NewAddress>> get() = _addressList

    private val _timesListOrder = listOf<String>().toMutableStateList()
    val timesListOrder: List<String> get() = _timesListOrder

    private val _addressesList = listOf<NewAddress>().toMutableStateList()
    val addressesList: List<NewAddress> get() = _addressesList

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
            val startPocket = repository.isStartPocket()
            _catalogList.addAll(repository.getCategoryList(startPocket))
        }
    }

    private fun getTelNumber() {
        viewModelScope.launch {
            val tel = repository.getClientInfo()?.phone
            Timber.d("tel $tel")
            telNumber = tel?.removeRange(2, 3)?.removeRange(5, 7)?.removeRange(8, 9).toString()
        }

    }

    fun getProductsList() {
        viewModelScope.launch {
            val favoriteList = repository.getFavorite()?.favoritesList
            val productsList = repository.getProductList()
            productsList.forEach{ product ->
                product.onFavoriteClick = favoriteList?.contains(product.id) == true
            }
            _productsList.value = productsList
        }
    }

    fun getProductByCategory(categoryId: Int) {
        viewModelScope.launch {
            val productList = repository.getProductByCategory(categoryId)
            _productsList.value = productList
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
        _productsInBasket.forEach { product ->
            generalCostProducts += product.getPriceNoDiscount(product.count)
        }
        Timber.d("General cost no discount = $generalCostProducts")
        _priceNoDiscount.value = generalCostProducts
    }

    private fun getCostProduct() {
        var generalCostProducts = 0

        _productsInBasket.forEach { product ->
            generalCostProducts += product.getPriceOnCount(product.count)
        }
        Timber.d("General cost = $generalCostProducts")
        _generalCost.value = generalCostProducts
    }

    fun deleteProductFromBasket(productId: Int) {
        viewModelScope.launch {
            val product = _productsInBasket.find { it.id == productId }
            if (product != null) {
                Timber.d("product = $product")

                repository.deleteProductFromBasket(product)
            }
        }
        getBasket()
    }

    fun plusCountProduct(productId: Int) {
        viewModelScope.launch {
            val product = _productsInBasket.find { it.id == productId }
            if (product != null) {
                product.count += 1
                repository.updateNewProductInBasket(product)
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
                        repository.updateNewProductInBasket(product)
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
                order.value?.clientId = client.id
                _client.value = client
            }
        }
    }

    fun getClientInfo() {
        viewModelScope.launch {
            _client.value = repository.getClientInfo()
//            _client.value = repository.getAuthClient()
        }
    }

    fun deleteAccount(mainActivity: MainActivity) {
        viewModelScope.launch {
            val deleteMessage = repository.deleteAccount()
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
                    "Акаунт удален",
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
        return if (clientEmail.contains(Regex("""^[A-z]+@[a-zA-Z_]+?\.[a-zA-Z]{2,6}$"""))) {
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
            Timber.d("CLIENT DATA = phone $editClientPhone")
            val clientEditData = ClientEditData(name = editClientName, phone = editClientPhone, email = editClientEmail)
            val clientUserData = repository.editUserData(clientEditData)
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
        val commons = deliveryTime.filter { it.dayNum == orderDate }
        Timber.d("COMMONS is $commons")
        val times = if (commons.isNotEmpty() || exception.isNotEmpty()) {
            if (exception.isNotEmpty()) {
                addTime(exception[0].partTypes)
            } else {
                Timber.d("Common ${commons[0].partTypes.size}")
                commons[0].partTypes.forEach {
                    Timber.d("part_types = $it")
                }
                when {
                    todayDate == date -> { //выбрана сегодняшняя дата
                        addTime(listOf(false)) //только вечер
                    }
                    tomorrowDay == selectDay && thisHour < 16 -> { // если заказ на завтра и время меньше 16
                        addTime(listOf(true, true)) // любое время
                    }
                    tomorrowDay == selectDay && thisHour > 15 -> { // если заказ на завтра и время больше 16
                        addTime(listOf(false, true)) // вечер
                    }
                    else -> {
                        addTime(commons[0].partTypes) // иначе как указано в графике
                    }
                }
            }
        } else emptyList()
        _timesListOrder.addAll(times)
    }

    fun disableDays(utcTimeMills: Long): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.timeInMillis = utcTimeMills
        var monday = isAvailableDay1(disabledDays.find { day -> day.dayNum == 1 }, calendar)
        var tuesday = isAvailableDay1(disabledDays.find { day -> day.dayNum == 2 }, calendar)
        var wednesday = isAvailableDay1(disabledDays.find { day -> day.dayNum == 3 }, calendar)
        var thursday = isAvailableDay1(disabledDays.find { day -> day.dayNum == 4 }, calendar)
        var friday = isAvailableDay1(disabledDays.find { day -> day.dayNum == 5 }, calendar)
        var saturday = isAvailableDay1(disabledDays.find { day -> day.dayNum == 6 }, calendar)
        var sunday = isAvailableDay1(disabledDays.find { day -> day.dayNum == 7 }, calendar)
        Timber.d("MONDAY = ${monday} TUESDAY = ${tuesday} WEDNESDAY = ${wednesday} THURSDAY = ${thursday} FRIDAY = ${friday} SATURDAY = ${saturday} SUNDAY = ${sunday}")
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
                    when (exceptionDay.dayNum) {
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
        return monday || tuesday || wednesday || thursday || friday || saturday || sunday
    }

    private fun isAvailableDay1(common: Common?, calendar: Calendar): Boolean {
        return if (common != null) {
            when (common.dayNum) {
                1 -> calendar[Calendar.DAY_OF_WEEK] == 2 && common.available
                2 -> calendar[Calendar.DAY_OF_WEEK] == 3 && common.available
                3 -> calendar[Calendar.DAY_OF_WEEK] == 4 && common.available
                4 -> calendar[Calendar.DAY_OF_WEEK] == 5 && common.available
                5 -> calendar[Calendar.DAY_OF_WEEK] == 6 && common.available
                6 -> calendar[Calendar.DAY_OF_WEEK] == 7 && common.available
                7 -> calendar[Calendar.DAY_OF_WEEK] == 1 && common.available
                else -> {
                    false
                }
            }
        } else false
    }

    private fun addTime(partTypes: List<Boolean>): MutableList<String> {
        return when {
            partTypes.size == 2 -> {
                arrayListOf("09:00-16:00", "17:00-22:00", "19:00-22:00")
            }

            partTypes[0] -> {
                arrayListOf("17:00-22:00", "19:00-22:00")
            }

            !partTypes[0] -> {
                arrayListOf("09:00-16:00")
            }

            else -> {
                arrayListOf("")
            }
        }
    }

    fun getDeliveryOnAddress(address: NewAddress) {
        viewModelScope.launch {
            disabledDays.clear()
            exceptions.clear()
            order.value?.date = ""
            order.value?.timeFrom = ""
            order.value?.timeTo = ""
            order.value?.addressId = address.id
            val delivery = repository.getDelivery(address)
            Timber.d("DELIVERY ${delivery?.common}")
            if (delivery != null) {
                disabledDays.addAll(delivery.common.sortedBy { common -> common.dayNum })
                exceptions.addAll(delivery.exceptions)
                Timber.d("COMMON ${delivery.common.filter { it.available }}")
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
        when (timeOrder) {
            "09:00-16:00" -> {
                _order.value?.timeFrom = "09:00"
                _order.value?.timeTo = "16:00"
            }
            "17:00-22:00" -> {
                _order.value?.timeFrom = "17:00"
                _order.value?.timeTo = "22:00"
            }
            "19:00-22:00" -> {
                _order.value?.timeFrom = "19:00"
                _order.value?.timeTo = "22:00"
            }
        }

        Timber.d("Order period = ${order.value?.timeFrom} - ${order.value?.timeTo}")
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
        return order?.clientId!! > 0 &&
                order.timeFrom.isNotEmpty() &&
                order.timeTo.isNotEmpty() &&
                !order.paymentType.isNullOrEmpty() &&
                order.paymentType != "Выберите способ оплаты" &&
                order.date.isNotEmpty() &&
                order.totalCost == 0
    }

    private fun refreshOrder() {
        _order.value = Order(
            clientId = 0,
            notice = "",
            date = "",
            productList = mutableListOf(),
            timeFrom = "",
            timeTo = "",
            totalCost = 0,
            paymentType = "",
            addressId = 0
        )
    }

    fun sendAndSaveOrder(order: Order?, orderCost: Int, navController: NavHostController) {
        viewModelScope.launch {
            if (order != null) {
                _order.value?.totalCost = orderCost
                order.totalCost = orderCost
                _productsInBasket.forEach { product ->
                    val productJson =
                        getJsonProduct(product, product.getPriceOnCount(product.count)/product.count)
                    order.productList.add(productJson)
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
//                    navController.clearBackStack(MainNavRoute.CreateOrderScreen.path)
                    refreshOrder()
                } else {
                    Timber.d("Order - $orderId, $orderCost")
                    getTelNumber()
                    refreshOrder()
//                    navController.clearBackStack(MainNavRoute.CreateOrderScreen.path)
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
                dataPayment != null && dataPayment.status == "succeeded" -> {
                    Timber.d("succeeded")
                    _paymentStatus.value = StatusPayment.DONE
                }

                dataPayment != null && dataPayment.status == "pending" -> {
                    Timber.d("Check pay")
                    _paymentStatus.value = StatusPayment.PANDING
                    val parameters = JsonObject()
//                    parameters.addProperty("updated_payment_state", 1)
                    parameters.addProperty("acq_id", dataPayment.id)
                    repository.setStatusPayment(orderId = orderId, parameters)
                    checkUrl = dataPayment.confirmation.confirmationUrl
                    idOrderPay = dataPayment.payment_method.id
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
                    val address = addressList.find { it.id == repeatOrder.addressId }
                    if (address != null) {
                        getDeliveryOnAddress(address)
                    }
                    Timber.d("Product Size = ${repeatOrder.productList.size}")
                    val products = repeatOrder.productList.map {
                        val product = repository.getNewProduct(it.id)
                        product?.count = it.amount
                        product
                    }
                    _productsInBasket.addAll(products.filterNotNull().filter { it.category != 20 })
                    getCostProduct()
                    getPriceNoDiscount()
                    _order.value?.paymentType = repeatOrder.paymentType.toString()
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
                val listProduct = mutableListOf<NewProduct>()
                orderFromCrm.productList.forEach {
                    val product = repository.getNewProduct(it.id)
                    if (product != null) {
                        if (product.category != 20) {
                            product.count = it.amount
                            listProduct.add(product)
                        }
                    }
                }

                _createdOrder.value = MyOrder(
                    address = orderFromCrm.address,
                    cash = orderFromCrm.totalCost.toString(),
                    date = "${orderFromCrm.date};${orderFromCrm.timeFrom}-${orderFromCrm.timeTo}",
                    products = listProduct,
                    typeCash = orderFromCrm.paymentType.toString(),
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

    private fun getJsonProduct(product: NewProduct, priceOne: Int): JsonObject {
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
                    val listProduct = mutableListOf<NewProduct>()
                    order.productList.forEach {
                        val product = repository.getNewProduct(it.id)
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
                            cash = order.totalCost.toString(),
                            date = "${order.date};${order.timeFrom}-${order.timeTo}",
                            products = listProduct,
                            typeCash = order.paymentType.toString(),
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

    fun getAddressString(newAddress: NewAddress):String {
        val block = if (!newAddress.block.isNullOrEmpty())"корп. ${newAddress.block}," else ""
        val entrance = if (!newAddress.entrance.isNullOrEmpty())"подъезд ${newAddress.entrance}," else ""
        val floor = if (!newAddress.floor.isNullOrEmpty()) "эт. ${newAddress.floor}," else ""
        val flat = if (!newAddress.flat.isNullOrEmpty()) "кв. ${newAddress.flat}" else ""
        return "г. ${newAddress.city} ул. ${newAddress.street} $block $entrance $floor $flat"
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
        block: String,
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
            if (client != null) {
                val addressParameters = AddressParameters(
                    clientId = client.id,
                    region = region,
                    city = city,
                    street = street,
                    house = house,
                    block = block.ifEmpty { null },
                    entrance = entrance.ifEmpty { null },
                    floor = floor.ifEmpty { null },
                    flat = flat.ifEmpty { null },
                    courierNotice = notice,
                    phoneContact = client.phone,
                    nameContact = client.name,
                    notice = notice
                )
                val newAddress =
                    if (contact.isEmpty()) {
                        repository.createAddress(
                            addressParameters
                        )
                    } else {
                        repository.createAddress(
                            addressParameters
                        )
                    }
                Timber.d("NEW ADDRESS = $newAddress")
                if (newAddress != null) {
                    if (isFromOrder) navController.navigate(
                        MainNavRoute.CreateOrderScreen.withArgs(false.toString(), "0")
                    ) else {
                        navController.popBackStack(MainNavRoute.AddressesScreen.path,
                            inclusive = false,
                            saveState = false
                        )

                    }
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

    fun setMailing(isMailing: Boolean) {
        viewModelScope.launch {
            repository.setMailing(isMailing)
            getClientInfo()
        }
    }


    fun getFavoriteProductList() {
        viewModelScope.launch {
            val favoriteProductList = mutableListOf<NewProduct>()
            _statusData.value = StatusData.LOAD
            val favoriteList = repository.getFavorite()?.favoritesList
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
            val dbProduct = repository.getProductFromDB(product.id)
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