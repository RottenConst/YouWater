package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.iwater.youwater.repository.AddressRepository
import javax.inject.Inject

class AddressViewModel @Inject constructor(
    private val addressRepo: AddressRepository
) : ViewModel() {

    private val _addressList: MutableLiveData<List<Address>> = MutableLiveData()
    val addressList: LiveData<List<Address>>
        get() = _addressList

}