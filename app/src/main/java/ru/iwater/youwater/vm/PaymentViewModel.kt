package ru.iwater.youwater.vm

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
import ru.iwater.youwater.data.MyOrder
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.network.ApiOrder
import ru.iwater.youwater.network.ApiYookassa
import ru.iwater.youwater.repository.PaymentRepository
import ru.iwater.youwater.screen.navigation.PaymentNavRoute
import ru.iwater.youwater.utils.StatusPayment
import timber.log.Timber

class PaymentViewModel(
    private var completeOrderId: Int = 0,
    private val repository: PaymentRepository
): ViewModel() {

    private val _createdOrder: MutableLiveData<MyOrder> = MutableLiveData(
        MyOrder("", "", "", emptyList(), "", -1, 0)
    )
    val completedOrder: LiveData<MyOrder> get() = _createdOrder

    private var _paymentStatus: MutableLiveData<StatusPayment> = MutableLiveData()
    val paymentStatus: LiveData<StatusPayment>
        get() = _paymentStatus

    private var checkUrl = ""
    private var idOrderPay = ""

    init {
        if (completeOrderId != 0) {
            clearBasket()
            getOrderCrm(orderId = completeOrderId)
        }
    }

    fun setCompleteOrderId(id: Int) {
        completeOrderId = id
        if (completeOrderId != 0) {
            clearBasket()
            getOrderCrm(id)
        }
    }

    private fun clearBasket() {
        viewModelScope.launch {
            val products = repository.getProductListOfCategory()
            products.forEach {
                repository.deleteProductFromBasket(it)
            }
        }
    }

    private fun getOrderCrm(orderId: Int) {
        viewModelScope.launch {
            Timber.d("GET ORDER CRM")
            val orderFromCrm = repository.getOrder(orderId)
            Timber.d("OrderCRM ${orderFromCrm?.id}")
            if (orderFromCrm != null) {
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

    fun createPay(
        orderId: Int,
        amount: String,
        description: String,
        paymentToken: String,
        capture: Boolean
    ) {
        viewModelScope.launch {
            Timber.d("payment")
            val dataPayment = repository.createPay(
                amount, description, paymentToken, capture
            )
            when {
                dataPayment != null && dataPayment.status == "succeeded" -> {
                    _paymentStatus.value = StatusPayment.DONE
                }
                dataPayment != null && dataPayment.status == "pending" -> {
                    _paymentStatus.value = StatusPayment.PANDING
                    checkUrl = dataPayment.confirmation.confirmationUrl
                    idOrderPay = dataPayment.payment_method.id
                    val parameters = JsonObject()
                    parameters.addProperty("acq_id", dataPayment.id)
                    repository.setStatusPayment(orderId = orderId, parameters)
                }
                else -> {
                    errorPay()
                }
            }
        }
    }

    fun setPaymentStatus(navController: NavHostController) {
        viewModelScope.launch {
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

    fun errorPay() {
        _paymentStatus.value = StatusPayment.ERROR
    }
}

class PaymentViewModelFactory(private val completeOrderId: Int = 0): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as App
        val token = ClientStorage(application.applicationContext).get().accessToken
        val repository = PaymentRepository(
            ApiYookassa.makeYookassaApi(),
            ApiOrder.makeOrderApi(token),
            YouWaterDB.getYouWaterDB(application.baseContext)?.newProductDao()!!
        )
        return PaymentViewModel(
            completeOrderId,
            repository
        ) as T
    }
}