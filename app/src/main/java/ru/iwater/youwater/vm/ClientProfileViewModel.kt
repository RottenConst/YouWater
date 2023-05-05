package ru.iwater.youwater.data

import android.widget.Toast
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.ProductRepository
import ru.iwater.youwater.screen.profile.AddAddressFragmentDirections
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

    private val _addressesList = listOf<RawAddress>().toMutableStateList()
    val addressesList: List<RawAddress> get() = _addressesList

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
        navController: NavController
        //-----

    ) {
        viewModelScope.launch {
            val client = getClientInfo()
            val newAddressParameters = JsonObject()
            val factAddress = getFactAddress(city = city, street = street, house = house, building = building, entrance = entrance, floor = floor, flat = flat)
            val addressJson = getJsonAddress(region = region, city = city, street = street, house = house, building = building, entrance = entrance, floor = floor, flat = flat)
            val fullAddress = getFullAddress(region =  region, street = street, house = house, building = building)
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
                    if (contact.isEmpty()){
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
                        AddAddressFragmentDirections.actionAddAddressFragmentToCreateOrderFragment(false, 0)
                    ) else navController.navigate(AddAddressFragmentDirections.actionAddAddressFragmentToAddresessFragment())
                } else {
                    Toast.makeText(navController.context, "Ошибка, данные не были отправлены, возможно проблемы с интернетом", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(navController.context, "Не был указан корректный адрес", Toast.LENGTH_SHORT).show()
            }
        }
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

    private suspend fun getClientInfo(): Client? {
        return repository.getClientInfo()
    }
}