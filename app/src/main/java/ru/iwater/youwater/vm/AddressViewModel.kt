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
            for (rawAddress in listNetAddress) {
                val address = addressRepo.getAddress(rawAddress.id)
                if (address == null) {
                    addressRepo.saveAddress(rawAddress)
                }
            }
            if (addressRepo.getAddressList().isEmpty()) {
                _statusLoad.value = StatusLoading.EMPTY
            } else {
                _rawAddressList.value = addressRepo.getAddressList()
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
        addressJson: JsonObject
    ) {
        viewModelScope.launch {
            val client = getClientInfo()
            if (client != null) {
                val newAddress = addressRepo.createAddress(
                    client.client_id,
                    client.email,
                    region,
                    factAddress,
                    address,
                    coords,
                    fullAddress,
                    returnTare,
                    client.contact,
                    client.name,
                    addressJson
                )
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