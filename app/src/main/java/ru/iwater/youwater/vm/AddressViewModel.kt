package ru.iwater.youwater.vm

import androidx.lifecycle.*
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.*
import ru.iwater.youwater.repository.AddressRepository
import javax.inject.Inject

class AddressViewModel @Inject constructor(
    private val addressRepo: AddressRepository
) : ViewModel() {

    /**
     * список адресов
     */
    private val _rawAddressList: MutableLiveData<List<RawAddress>> = MutableLiveData()
    val rawAddress: LiveData<List<RawAddress>>
        get() = _rawAddressList

    /**
     * статус отправки адреса
     */
    private val _statusSend: MutableLiveData<StatusSendData> = MutableLiveData()
    val statusSend: LiveData<StatusSendData>
        get() = _statusSend

    /**
     * статус загрузки адресов
     */
    private val _statusLoad: MutableLiveData<StatusLoading> = MutableLiveData()
    val statusLoad: LiveData<StatusLoading>
        get() = _statusLoad

    init {
        getRawAddress()
    }

    /**
     * получить и сохранить все адреса
     */
    fun getRawAddress() {
        viewModelScope.launch {
            _statusLoad.value = StatusLoading.LOADING
            val listNetAddress = addressRepo.getAllFactAddress()
            val listAddress = mutableListOf<RawAddress>()
            for (rawAddress in listNetAddress) {
                val address = addressRepo.getFactAddress(rawAddress.id)
                when (address?.active) {
                    true -> {
                        listAddress.add(address)
                    }
                    else -> {
                    }
                }
            }
            if (listAddress.isEmpty()) {
                _statusLoad.value = StatusLoading.EMPTY
            } else {
                _rawAddressList.value = listAddress
                _statusLoad.value = StatusLoading.DONE
            }
        }
    }

    /**
     * получить информацию о пользователе
     */
    private suspend fun getClientInfo(): Client? {
        return addressRepo.getClientInfo()
    }

    /**
     * создать новый адрес
     */
    fun createNewAddress(
        region: String,
        factAddress: String,
        address: String,
        coords: String,
        fullAddress: String,
        returnTare: Int,
        addressJson: JsonObject,
        contact: String,
        notice: String
    ) {
        viewModelScope.launch {
            val client = getClientInfo()
            val newAddressParameters = JsonObject()
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
                    this.addProperty("return_tare", returnTare)
                    this.addProperty("coords", coords)
                    this.addProperty("active", 1)
                    this.add("address_json", addressJson)
                }
                val newAddress =
                if (contact.isEmpty()){
                    newAddressParameters.addProperty("contact", client.contact)
                    addressRepo.createAddress(
                        newAddressParameters
                    )
                } else {
                    newAddressParameters.addProperty("contact", contact)
                    addressRepo.createAddress(
                        newAddressParameters
                    )
                }
                if (newAddress == "Адрес успешно добавлен.") {
                    _statusSend.value = StatusSendData.SUCCESS
                } else {
                    _statusSend.value = StatusSendData.ERROR
                }
            } else {
                _statusSend.value = StatusSendData.ERROR
            }
        }
    }

    /**
     * удалить адрес из бд и сделать адрес неактивным в црм
     */
    fun deleteAddress(address: RawAddress) {
        viewModelScope.launch {
            addressRepo.deleteAddress(address)
            addressRepo.inactiveAddress(address.id)
            getRawAddress()
        }
    }

}