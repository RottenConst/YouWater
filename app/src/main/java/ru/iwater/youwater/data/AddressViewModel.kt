package ru.iwater.youwater.data

import android.Manifest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.BuildConfig
import ru.iwater.youwater.repository.AddressRepository
import timber.log.Timber
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
        getAddAddress()
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
            Timber.d("Addresses === $listAddress")
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