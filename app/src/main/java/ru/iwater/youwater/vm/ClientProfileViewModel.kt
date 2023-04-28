package ru.iwater.youwater.data

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.ProductRepository
import javax.inject.Inject

enum class StatusSendData {
    SUCCESS,
    ERROR
}

enum class StatusData {
    LOAD,
    DONE
}

class ClientProfileViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {

    val client: LiveData<Client?> = liveData { emit(getClientInfo()) }

    private var editClientName: String = ""
    private var editClientPhone: String = ""
    private var editClientEmail: String = ""

    private val _ordersList = listOf<MyOrder>().toMutableStateList()
    val ordersList: List<MyOrder> get() = _ordersList

    private val _statusData: MutableLiveData<StatusData> = MutableLiveData()
    val statusData: LiveData<StatusData> get() = _statusData

    private val _statusSend: MutableLiveData<StatusSendData> = MutableLiveData()
    val statusSend: LiveData<StatusSendData>
        get() = _statusSend

    fun setEditClientData(clientName: String, clientPhone: String, clientEmail: String) {
        editClientName = clientName
        editClientPhone = clientPhone
        editClientEmail = clientEmail
    }

    fun editUserData() {
        viewModelScope.launch {
            val clientId = getClientInfo()?.client_id
            if (clientId != null) {
                val clientData = JsonObject()
                clientData.addProperty("name", editClientName)
                clientData.addProperty("contact", editClientPhone)
                clientData.addProperty("email", editClientEmail)
                val clientUserData = repository.editUserData(clientId, clientData)
                if (clientUserData) {
                    _statusSend.value = StatusSendData.SUCCESS
                } else _statusSend.value = StatusSendData.ERROR
            } else {
                _statusSend.value = StatusSendData.ERROR
            }
        }
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

    private suspend fun getClientInfo(): Client? {
        return repository.getClientInfo()
    }
}