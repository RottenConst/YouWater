package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
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

    private val _statusSend: MutableLiveData<StatusSendData> = MutableLiveData()
    val statusSend: LiveData<StatusSendData>
        get() = _statusSend

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
            Timber.d("ADDRESS SIZE = $listAddress")
            val addresses = mutableListOf<Address>()
            for (index in listAddress.indices) {
                if (index % 2 != 0) {
                    val region = listAddress[index - 1].split(",")[0].removePrefix("\"")
                    val address = getAddressFromString(listAddress[index].split(","), region)
                    addresses.add(address)
                    val savedAddress = addressRepo.getAddressList()
                    if (savedAddress.isNullOrEmpty()) {
                        saveAddress(address)
                    } else {
                        savedAddress.forEach {
                            if (it.street != address.street && it.house != address.house && it.flat != address.flat) {
                                saveAddress(address)
                            }
                        }
                    }
                }
            }
            _addressList.value = addresses
        }
    }

    private fun getAddressFromString(rawAddress: List<String>, region: String): Address {
        var street = ""
        var house = 0
        var building = ""
        var entrance: Int? = null
        var floor: Int? = null
        var flat: Int? = null
        rawAddress.forEachIndexed { index, s ->
            if (index == 0) street = s.removePrefix("\"").removeSuffix(",")
            if (index > 0) {
                house = parseHouse(s, house)
                building = parseBuilding(s, building)
                entrance = parseEntrance(s, entrance)
                floor = parseFloor(s, floor)
                flat = parseFlat(s, flat)
            }
        }
        Timber.d("$region $street $house $building $entrance $floor $flat")
        return Address(region, street, house, building, entrance, floor, flat, "")
    }

    private fun parseHouse(string: String, house: Int): Int{
        val houseList = string.split(" ")
        return if (houseList[1] == "д." && house == 0) {
            houseList[2].removeSuffix("\"").toInt()
        } else house
    }

    private fun parseEntrance(string: String, entrance: Int?): Int?{
        val entranceList = string.split(" ")
        return if (entranceList[1] == "пд." && entrance == null) {
            entranceList[2].removeSuffix("\"").toInt()
        } else entrance
    }

    private fun parseFloor(string: String, floor: Int?): Int?{
        val floorList = string.split(" ")
        return if (floorList[1] == "эт." && floor == null) {
            floorList[2].removeSuffix("\"").toInt()
        } else floor
    }

    private fun parseFlat(string: String, flat: Int?): Int?{
        val flatList = string.split(" ")
        return if (flatList[1] == "кв." && flat == null) {
            flatList[2].removeSuffix("\"").toInt()
        } else flat
    }

    private fun parseBuilding(string: String, building: String): String{
        val buildingList = string.split(" ")
        return if (buildingList[1] == "корп." || buildingList[1] == "ст." && building.isNotEmpty()) {
            buildingList[2].removeSuffix("\"")
        } else building
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

    fun createAutoTask(clientId: Int, dateCreate: String, addressData: JsonObject) {
        viewModelScope.launch {
            val clientAddAddress =
                ClientUserData(id = clientId, dateCreated = dateCreate, clientData = addressData)
            val answer = addressRepo.createAutoTask(clientAddAddress)
            if (answer == "address sent for moderation") _statusSend.value = StatusSendData.SUCCESS
                else _statusSend.value = StatusSendData.ERROR
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