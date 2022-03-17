package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.BuildConfig
import ru.iwater.youwater.repository.AddressRepository
import javax.inject.Inject

class AddressViewModel @Inject constructor(
    private val addressRepo: AddressRepository
) : ViewModel() {

    private val _addressList: MutableLiveData<List<Address>> = MutableLiveData()
    val addressList: LiveData<List<Address>>
        get() = _addressList

    private val _addressResult: MutableLiveData<AddressResult> = MutableLiveData()
    val addressResult: LiveData<AddressResult>
        get() = _addressResult

    init {
//        getAddAddress()
    }

    fun getPlace(place: String) {
        viewModelScope.launch {
            val addressResult = addressRepo.getStreetOnCoordinate(place, BuildConfig.GOOGLE_MAPS_API_KEY)
            if (addressResult?.status == "OK") {
                _addressResult.value = addressResult
            }
        }
    }

    fun getAllFactAddress() {
        viewModelScope.launch {
            val listAddress = addressRepo.getAllFactAddress()
            if (!listAddress.isNullOrEmpty()) {
                val region = listAddress[0].split(",")[0].removePrefix("\"")
                val street = listAddress[1].split(" ")[0].removePrefix("\"").removeSuffix(",")
                val house = listAddress[1].split(" ")[2].removeSuffix(",").toInt()
                val building = listAddress[1].split(" ")[4].removeSuffix(",")
                val entrance = listAddress[1].split(" ")[6].removeSuffix(",").toInt()
                val floor = listAddress[1].split(" ")[8].removeSuffix(",").toInt()
                val flat =
                    listAddress[1].split(" ")[10].removeSuffix(",").removeSuffix("\"").toInt()
                val address = Address(region, street, house, building, entrance, floor, flat, "")
                val savedAddress = addressRepo.getAddressList()
                if (savedAddress.isNullOrEmpty()) {
                    saveAddress(address)
                } else {
                    savedAddress.forEach {
                        if (it.street != address.street && it.flat != address.flat) {
                            saveAddress(address)
                        }
                    }
                }
                _addressList.value = listOf(address)
            } else {
                getAddAddress()
            }
        }
    }

    fun getInfoOnAddress(addressString: String) {
        viewModelScope.launch {
            val addressResult = addressRepo.getInfoOnAddress(addressString, BuildConfig.GOOGLE_MAPS_API_KEY)
            if (addressResult?.status == "OK") {
                _addressResult.value = addressResult
            }
        }
    }

    private fun getAddAddress() {
        viewModelScope.launch {
           _addressList.value = addressRepo.getAddressList()
        }
    }

    fun saveAddress(address: Address) {
        viewModelScope.launch {
            addressRepo.saveAddress(address)
        }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch {
            addressRepo.deleteAddress(address)
            getAddAddress()
        }
    }

}