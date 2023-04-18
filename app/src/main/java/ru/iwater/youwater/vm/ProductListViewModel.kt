package ru.iwater.youwater.vm

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.google.gson.JsonObject
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.Client
import ru.iwater.youwater.data.Common
import ru.iwater.youwater.data.Exception
import ru.iwater.youwater.data.Order
import ru.iwater.youwater.data.PaymentCard
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.RawAddress
import ru.iwater.youwater.di.components.OnScreen
import ru.iwater.youwater.repository.ProductRepository
import ru.iwater.youwater.screen.basket.CardPaymentFragmentDirections
import ru.iwater.youwater.screen.basket.CreateOrderFragmentDirections
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OnScreen
class ProductListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
) : ViewModel() {

    private val _client: MutableLiveData<Client?> = MutableLiveData()
    val client: LiveData<Client?> = _client

    private val _productsList = listOf<Product>().toMutableStateList()
    val productsList: List<Product> get() = _productsList

    private val _priceNoDiscount: MutableLiveData<Int> = MutableLiveData()
    val priceNoDiscount: LiveData<Int> get() = _priceNoDiscount

    private val _generalCost: MutableLiveData<Int> = MutableLiveData()
    val generalCost: LiveData<Int>
        get() = _generalCost

    private val _addressList: MutableLiveData<List<RawAddress>> = MutableLiveData()
    val addressList: LiveData<List<RawAddress>> get() = _addressList

    private val _timesListOrder = listOf<String>().toMutableStateList()
    val timesListOrder: List<String> get() = _timesListOrder

    private val _order: MutableLiveData<Order> = MutableLiveData(Order(
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
        dateCreate =  java.sql.Date(Calendar.getInstance().timeInMillis)
    ))
    val order: LiveData<Order> get() = _order

    private val disabledDays = mutableListOf<Common>()
    private val exceptions = mutableListOf<Exception>()

    private val deliveryTime = mutableListOf<Common>()
    private val exceptionTime = mutableListOf<Exception>()


    fun isTrueOrder(order: Order?): Boolean {
        return order?.clientId != 0 &&
                order?.period?.isNotEmpty() == true && order.period != "**:**-**:**" &&
                !order.paymentType.isNullOrEmpty() && order.paymentType != "Выберите способ оплаты"  &&
                !order.email.isNullOrEmpty() && order.name.isNotEmpty() && order.date.isNotEmpty() && order.orderCost == 0
    }
    fun getClient() {
        viewModelScope.launch {
            val client = productRepo.getClientInfo()
            if (client != null) {
                order.value?.clientId = client.client_id
                order.value?.name = client.name
                order.value?.contact = client.contact
                if (client.email.isNotEmpty()) order.value?.email = client.email
                _client.value = client
            }
        }
    }

    fun getBasket() {
        viewModelScope.launch {
            _productsList.clear()
            _productsList.addAll(productRepo.getProductListOfCategory())
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    fun deleteProductFromBasket(productId: Int) {
        viewModelScope.launch {
            val product = _productsList.find { it.id == productId }
            if (product != null) {
                productRepo.deleteProductFromBasket(product)
            }
            _productsList.remove(product)
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    private fun getCostProduct() {
        var generalCostProducts = 0
        productsList.forEach { product ->
            generalCostProducts += product.getPriceOnCount(product.count)
        }
        _generalCost.value = generalCostProducts
    }

    private fun getPriceNoDiscount() {
        var generalCostProducts = 0
        productsList.forEach { product ->
            generalCostProducts += product.getPriceNoDiscount(product.count)
        }
        _priceNoDiscount.value = generalCostProducts
    }

    fun plusCountProduct(productId: Int) {
        viewModelScope.launch {
            val product = _productsList.find { it.id == productId }
            if (product != null) {
                product.count += 1
                productRepo.updateProductInBasket(product)
            }
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    fun minusCountProduct(productId: Int) {
        viewModelScope.launch {
            val product = _productsList.find { it.id == productId }
            if (product != null) {
                when {
                    product.count > 1 -> {
                        product.count -= 1
                        productRepo.updateProductInBasket(product)
                    }
                }
            }
            getPriceNoDiscount()
            getCostProduct()
        }
    }

    fun getAddressList() {
        viewModelScope.launch {
            _addressList.value = productRepo.getAddress()
        }
    }

    fun getDeliveryOnAddress(address: RawAddress) {
        viewModelScope.launch {
            disabledDays.clear()
            exceptions.clear()
            order.value?.date = ""
            order.value?.period = ""
            order.value?.addressId = address.id
            val delivery = productRepo.getDelivery(address)
            if (delivery != null) {
                disabledDays.addAll(delivery.common.filter { !it.available })
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

    fun getCalendar(calendar: Calendar, setDateOrder: (String) -> Unit): DatePickerDialog {
        val thisYear = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        _timesListOrder.clear()

        val datePickerDialog = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
            if (monthOfYear + 1 >= 10) "$dayOfMonth.${monthOfYear + 1}.$year".also {
                setDateOrder(it)
                _timesListOrder.addAll(getTimeList(year, monthOfYear, dayOfMonth))
            } else "$dayOfMonth.0${monthOfYear + 1}.$year".also {
                setDateOrder(it)
                _timesListOrder.addAll(getTimeList(year, monthOfYear, dayOfMonth))
            }
         }, thisYear,month,day)

        datePickerDialog.setTitle("Укажите время заказа")
        datePickerDialog.firstDayOfWeek = Calendar.MONDAY
        val minDate = Calendar.getInstance()
        datePickerDialog.minDate = minDate

        if (hour >= 14) {
            val yearMin = minDate.get(Calendar.YEAR)
            val monthMin = minDate.get(Calendar.MONTH)
            val dayMin = minDate.get(Calendar.DAY_OF_MONTH)
            minDate.set(yearMin, monthMin, dayMin + 1)
            datePickerDialog.minDate = minDate
        }

        val maxDate = Calendar.getInstance()
        maxDate.set(Calendar.DAY_OF_MONTH, day + 320)
        datePickerDialog.maxDate = maxDate

        disableOnDelivery(minDate, maxDate, disabledDays, exceptions, datePickerDialog)
        return datePickerDialog
    }

    private fun disableOnDelivery(minDate: Calendar, maxDate: Calendar, disabledDays: List<Common>, exceptions: List<Exception>, datePickerDialog: DatePickerDialog) {
        var loopDate = minDate
        while (minDate.before(maxDate)) {
            val dayOfWeek = loopDate[Calendar.DAY_OF_WEEK]
            disabledDays.forEach { common ->
                disableDay(common.day_num, dayOfWeek, loopDate, datePickerDialog)
            }
            minDate.add(Calendar.DATE, 1)
            loopDate = minDate
        }
        datePickerDialog.disabledDays.forEach { calendar ->
            val date = SimpleDateFormat("yyyy-MM-dd", Locale("ru")).format(calendar.time)
            exceptions.forEach {
                if (it.date == date && it.available) {
                    calendar.clear()
                }
            }
        }
    }

    private fun disableDay(day: Int, dayOfWeek: Int, loopDate: Calendar, datePickerDialog: DatePickerDialog) {
        when(day) {
            1 -> if (dayOfWeek == Calendar.MONDAY) {
                val disables = arrayOfNulls<Calendar>(1)
                disables[0] = loopDate
                datePickerDialog.disabledDays = disables
            }
            2 -> if (dayOfWeek == Calendar.TUESDAY) {
                val disables = arrayOfNulls<Calendar>(1)
                disables[0] = loopDate
                datePickerDialog.disabledDays = disables
            }
            3 -> if (dayOfWeek == Calendar.WEDNESDAY) {
                val disables = arrayOfNulls<Calendar>(1)
                disables[0] = loopDate
                datePickerDialog.disabledDays = disables
            }
            4 -> if (dayOfWeek == Calendar.THURSDAY) {
                val disables = arrayOfNulls<Calendar>(1)
                disables[0] = loopDate
                datePickerDialog.disabledDays = disables
            }
            5 -> if (dayOfWeek == Calendar.FRIDAY) {
                val disables = arrayOfNulls<Calendar>(1)
                disables[0] = loopDate
                datePickerDialog.disabledDays = disables
            }
            6 -> if (dayOfWeek == Calendar.SATURDAY) {
                val disables = arrayOfNulls<Calendar>(1)
                disables[0] = loopDate
                datePickerDialog.disabledDays = disables
            }
            7 -> if (dayOfWeek == Calendar.SUNDAY) {
                val disables = arrayOfNulls<Calendar>(1)
                disables[0] = loopDate
                datePickerDialog.disabledDays = disables
            }
        }
    }

    private fun getTimeList(year: Int, monthOfYear: Int, dayOfMonth: Int): List<String> {
        val calendar = Calendar.getInstance()
        //дата заказа
        calendar.set(year, monthOfYear, dayOfMonth)
        val orderDate = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val date = SimpleDateFormat("yyyy-MM-dd", Locale("ru")).format(Date(calendar.timeInMillis))
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
                    addTime(commons[0].part_types)
                }
        } else emptyList()
        return times
    }

    private fun addTime(part_types: List<Int>): MutableList<String> {
        return when {
            part_types.size == 2 -> {
                arrayListOf("09:00-16:00", "17:00-22:00", "19:00-22:00")
            }
            part_types[0] == 0 -> {
                arrayListOf("09:00-16:00")
            }
            else -> {arrayListOf("17:00-22:00", "19:00-22:00")}
        }
    }

    fun setTypePeyOrder(typeOrder: String){
        when (typeOrder) {
            "Оплата по карте курьеру" -> _order.value?.paymentType = "4"
            "Оплата наличными" -> _order.value?.paymentType = "0"
            "Оплата онлайн" -> _order.value?.paymentType = "2"
        }
    }

    fun setTimeOrder(timeOrder: String) {
        _order.value?.period = timeOrder
        Timber.d("Order period = $order")
    }

    fun setNoticeOrder(notice: String) {
        _order.value?.notice = notice
    }

    private fun getJsonProduct(product: Product, priceOne: Int): JsonObject {
        val productJs = JsonObject()
        productJs.addProperty("id", product.id)
        productJs.addProperty("name", product.name)
        productJs.addProperty("price", priceOne)
        productJs.addProperty("amount", product.count)
        return productJs
    }

    fun sendAndSaveOrder(order: Order?, orderCost: Int, navController: NavController) {
        viewModelScope.launch {
            if (order != null) {
                _order.value?.orderCost = orderCost
                order.orderCost = orderCost
                _productsList.forEach { product ->
                    val productJson =
                        getJsonProduct(product, product.getPriceOnCount(product.count))
                    order.waterEquip.add(productJson)
                }
                val orderId = productRepo.createOrderApp(order)
                if (order.paymentType != "2") {

                    navController.navigate(
                        CreateOrderFragmentDirections.actionCreateOrderFragmentToCompleteOrderFragment(
                            orderId.toString(),
                            false
                        )
                    )
                } else {
                    val paymentCard = PaymentCard(orderNumber = orderId.toString(), amount = orderCost * 100, phone = order.contact)
                    val dataPayment = productRepo.payCard(paymentCard)
                    val id = dataPayment[0].removePrefix("\"").removeSuffix("\"")
                    val url = dataPayment[1].removePrefix("\"").removeSuffix("\"")
                    navController.navigate(
                        CreateOrderFragmentDirections.actionCreateOrderFragmentToCardPaymentFragment(url, id)
                    )
                }
            }
        }
    }

    fun getPaymentStatus(orderId: String, navController: NavController) {
        viewModelScope.launch {
            val paymentStatus = productRepo.getPaymentStatus(orderId)
            if (paymentStatus.second == 2) {
                val parameters = JsonObject()
                parameters.addProperty("updated_payment_state", 1)
                parameters.addProperty("updated_acq", orderId)
                if (productRepo.setStatusPayment(paymentStatus.first, parameters)) {
                    navController.navigate(
                        CardPaymentFragmentDirections.actionCardPaymentFragmentToCompleteOrderFragment(
                            orderId,
                            true
                        )
                    )
                } else {
                    navController.navigate(
                        CardPaymentFragmentDirections.actionCardPaymentFragmentToCreateOrderFragment(
                            true,
                            0
                        )
                    )
                }
            } else {
                navController.navigate(
                    CardPaymentFragmentDirections.actionCardPaymentFragmentToCreateOrderFragment(
                        true,
                        0
                    )
                )
            }
        }
    }
}