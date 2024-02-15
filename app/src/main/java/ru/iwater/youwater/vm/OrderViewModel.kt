package ru.iwater.youwater.vm

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import ru.iwater.youwater.base.App
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.Common
import ru.iwater.youwater.data.MyOrder
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Order
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.network.ApiClient
import ru.iwater.youwater.network.ApiOrder
import ru.iwater.youwater.repository.OrderRepository
import ru.iwater.youwater.screen.PaymentActivity
import ru.iwater.youwater.screen.navigation.MainNavRoute
import ru.iwater.youwater.utils.StatusData
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class OrderViewModel(
    clientId: Int = 0,
    addressList: List<NewAddress>,
    repeatOrder: Int = 0,
    private val repository: OrderRepository
): ViewModel() {

    private val _statusData: MutableLiveData<StatusData> = MutableLiveData()
    val statusData: LiveData<StatusData> get() = _statusData

    private val _order: MutableLiveData<Order> = MutableLiveData(
        Order(
            clientId = clientId,
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

    private val disabledDays = mutableListOf<Common>()
    private val exceptions = mutableListOf<ru.iwater.youwater.data.Exception>()

    private val deliveryTime = mutableListOf<Common>()
    private val exceptionTime = mutableListOf<ru.iwater.youwater.data.Exception>()

    private val _timesListOrder = listOf<String>().toMutableStateList()
    val timesListOrder: List<String> get() = _timesListOrder

    init {
        if (clientId > 0) {
            getBasket()
        } else {
            getOrderCrm()
        }

        if (repeatOrder != 0) {
            getInfoLastOrder(repeatOrder, addressList)
        }
    }

    private fun getBasket() {
        viewModelScope.launch {
            _productsInBasket.clear()
            _productsInBasket.addAll(repository.getProductListOfCategory())
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    private fun getOrderCrm() {
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

    private fun getInfoLastOrder(orderId: Int, addressList: List<NewAddress>) {
        viewModelScope.launch {
            if (orderId != 0) {
                val repeatOrder = repository.getOrder(orderId)
                if (repeatOrder != null) {
                    _productsInBasket.clear()
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

    fun disableDays(utcTimeMills: Long): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.timeInMillis = utcTimeMills
        var monday = isAvailableDay(disabledDays.find { day -> day.dayNum == 1 }, calendar)
        var tuesday = isAvailableDay(disabledDays.find { day -> day.dayNum == 2 }, calendar)
        var wednesday = isAvailableDay(disabledDays.find { day -> day.dayNum == 3 }, calendar)
        var thursday = isAvailableDay(disabledDays.find { day -> day.dayNum == 4 }, calendar)
        var friday = isAvailableDay(disabledDays.find { day -> day.dayNum == 5 }, calendar)
        var saturday = isAvailableDay(disabledDays.find { day -> day.dayNum == 6 }, calendar)
        var sunday = isAvailableDay(disabledDays.find { day -> day.dayNum == 7 }, calendar)
        Timber.d("MONDAY = $monday TUESDAY = $tuesday WEDNESDAY = $wednesday THURSDAY = $thursday FRIDAY = $friday SATURDAY = $saturday SUNDAY = $sunday")
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

    private fun isAvailableDay(common: Common?, calendar: Calendar): Boolean {
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
                    tomorrowDay == selectDay -> { // если заказ на завтра и время больше 16
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

    fun sendAndSaveOrder(order: Order?, orderCost: Int, clientPhone: String, navController: NavHostController) {
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
//                    getTelNumber()
//                    Timber.d("Tel num $telNumber, client phone $clientPhone")
                    refreshOrder()
//                    navController.clearBackStack(MainNavRoute.CreateOrderScreen.path)
                    PaymentActivity.startGenerateToken(
                        navController.context,
                        orderId,
                        orderCost,
                        getTelNumber(clientPhone)
//                        telNumber
                    )
                }
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

    private fun getTelNumber(clientPhone: String): String {
//        viewModelScope.launch {
//            val tel = repository.getClientInfo()?.phone
            Timber.d("tel $clientPhone")
            return clientPhone.removeRange(2, 3).removeRange(5, 7).removeRange(8, 9)
    }

//    }

}

class OrderViewModelFactory(private val clientId: Int = 0, private val repeatOrder: Int = 0, private val addressList: List<NewAddress> = emptyList()): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as App
        val token = ClientStorage(application.applicationContext).get().accessToken
        val repository = OrderRepository(
            ApiOrder.makeOrderApi(token),
            ApiClient.makeClientApi(token),
            YouWaterDB.getYouWaterDB(application.baseContext)?.newProductDao()!!
        )
        return OrderViewModel(
            clientId = clientId,
            addressList = addressList,
            repeatOrder = repeatOrder,
            repository = repository
        ) as T
    }
}