package ru.iwater.youwater.vm

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import com.pusher.pushnotifications.PushNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.iwater.youwater.base.App
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.AddressParameters
import ru.iwater.youwater.data.Client
import ru.iwater.youwater.data.ClientEditData
import ru.iwater.youwater.data.DeliverySchedule
import ru.iwater.youwater.data.Favorite
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.network.ApiClient
import ru.iwater.youwater.repository.ClientRepository
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.screen.navigation.MainNavRoute
import timber.log.Timber

class ClientViewModel(
    private val repository: ClientRepository
): ViewModel() {

    private val _client: MutableLiveData<Client?> = MutableLiveData()
    val client: LiveData<Client?> get() = _client

    private val _favorite: MutableLiveData<Favorite> = MutableLiveData()
    val favorite: LiveData<Favorite> get() = _favorite

    private val _addressesList: MutableLiveData<List<NewAddress>> = MutableLiveData()
    val addressList: LiveData<List<NewAddress>> get() = _addressesList

    private val _productsInBasket: MutableLiveData<List<NewProduct>> = MutableLiveData()
    val productsInBasket: LiveData<List<NewProduct>> get() = _productsInBasket

    private val _deliverySchedule: MutableLiveData<DeliverySchedule> = MutableLiveData()
    val deliverySchedule: LiveData<DeliverySchedule> get() = _deliverySchedule

    private var editClientName: String = ""
    private var editClientPhone: String = ""
    private var editClientEmail: String = ""

    init {
        getClientInfo()
        getFavorite()
        getAddressesList()
    }

    private fun getFavorite() {
        viewModelScope.launch {
            _favorite.value = repository.getFavorite()
            _productsInBasket.value = repository.getProductsInBasket()
        }
    }

    fun onChangeFavorite(product: NewProduct, onFavorite: Boolean) {
        viewModelScope.launch {
            if (onFavorite) repository.deleteFavorite(product.id)
            else repository.addToFavoriteProduct(product.id)
            getFavorite()
        }

    }

    private fun getClientInfo() {
        viewModelScope.launch {
            _client.value = repository.getClientInfo()
        }
    }

    fun editUserData(navHostController: NavHostController) {
        viewModelScope.launch {
            Timber.d("CLIENT DATA = phone $editClientPhone")
            val clientEditData = ClientEditData(name = editClientName, phone = editClientPhone, email = editClientEmail)
            val clientUserData = repository.editUserData(clientEditData)
            if (clientUserData) {
                getClientInfo()
                navHostController.navigate(MainNavRoute.UserDataScreen.withArgs(true.toString())) {
                    popUpTo(MainNavRoute.UserDataScreen.path) { inclusive = true }
                }
            } else {
                Toast.makeText(
                    navHostController.context,
                    "Ошибка, данные не были отправлены",
                    Toast.LENGTH_SHORT
                ).show()
                navHostController.navigate(MainNavRoute.UserDataScreen.withArgs(false.toString())) {
                    popUpTo(MainNavRoute.UserDataScreen.path) { inclusive = true }
                }
            }
        }
    }

    fun getNumberFromPhone(clientPhone: String): String {
        val listNumber = mutableListOf<Char>()
        val integerChars = '0'..'9'
        clientPhone.forEach { char ->
            if (char in integerChars) listNumber.add(char)
        }
        return listNumber.joinToString(
            ""
        )
    }

    fun setEditClientName(clientName: String, clientPhone: String, clientEmail: String): Boolean {
        return if (!clientName.contains(Regex("""[^A-zА-я\s]"""))) {
            editClientName = clientName
            editClientPhone = clientPhone
            editClientEmail = clientEmail
            true
        } else {
            false
        }
    }

    fun setEditClientPhone(clientPhone: String, clientName: String, clientEmail: String): Boolean {
        return if (clientPhone.contains(Regex("""7\d{10}"""))) {
            editClientPhone = "+${clientPhone[0]}(${clientPhone[1]}${clientPhone[2]}${clientPhone[3]}) ${clientPhone[4]}${clientPhone[5]}${clientPhone[6]}-${clientPhone[7]}${clientPhone[8]}${clientPhone[9]}${clientPhone[10]}"
            editClientName = clientName
            editClientEmail = clientEmail
            true
        } else {
            false
        }
    }

    fun setEditClientEmail(clientEmail: String, clientName: String, clientPhone: String): Boolean {
        return if (clientEmail.contains(Regex("""^[A-z]+@[a-zA-Z_]+?\.[a-zA-Z]{2,6}$"""))) {
            editClientEmail = clientEmail
            editClientName = clientName
            editClientPhone = clientPhone
            true
        } else false
    }

    fun deleteAccount(clientId: Int, context: Context) {
        viewModelScope.launch {
            val deleteMessage = repository.deleteAccount()
            if (deleteMessage != null && deleteMessage.status) {
                PushNotifications.clearAllState()
                val intent = Intent(context.applicationContext, StartActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                CoroutineScope(Dispatchers.Default).launch {
                    YouWaterDB.getYouWaterDB(context.applicationContext)?.clearAllTables()
                }
                exitClient(clientId = clientId, context)
                Toast.makeText(
                    context.applicationContext,
                    "Акаунт удален",
                    Toast.LENGTH_SHORT
                ).show()
                context.startActivity(intent)
            } else {
                Toast.makeText(
                    context.applicationContext,
                    "Ошибка соединения",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun exitClient(clientId: Int, context: Context) {
        viewModelScope.launch {
            val authClient = ClientStorage(context).get()
            val status = repository.logoutClient(clientId, authClient.refreshToken)
            if (status) {
                ClientStorage(context).remove()
                PushNotifications.clearAllState()
                PushNotifications.clearDeviceInterests()
                val intent = Intent(context.applicationContext, StartActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                CoroutineScope(Dispatchers.Default).launch {
                    YouWaterDB.getYouWaterDB(context = context.applicationContext)?.clearAllTables()
                }
                context.startActivity(intent)
            }
        }
    }

    fun setMailing(isMailing: Boolean) {
        viewModelScope.launch {
            repository.setMailing(isMailing)
            getClientInfo()
        }
    }

    private fun getAddressesList() {
        viewModelScope.launch {
            _addressesList.value = emptyList()
            val addresses = repository.getAddress()
            if (addresses.isNotEmpty()) {
                _addressesList.value = addresses
            }
        }
    }

    fun getAddressString(newAddress: NewAddress):String {
        val block = if (!newAddress.block.isNullOrEmpty())"корп. ${newAddress.block}," else ""
        val entrance = if (!newAddress.entrance.isNullOrEmpty())"подъезд ${newAddress.entrance}," else ""
        val floor = if (!newAddress.floor.isNullOrEmpty()) "эт. ${newAddress.floor}," else ""
        val flat = if (!newAddress.flat.isNullOrEmpty()) "кв. ${newAddress.flat}" else ""
        return "г. ${newAddress.city} ул. ${newAddress.street} $block $entrance $floor $flat"
    }

    fun updateDeliveryOnAddress(address: NewAddress) {
        viewModelScope.launch {
            _deliverySchedule.value = repository.getDelivery(address)
        }
    }

    fun addProductToBasket(product: NewProduct, count: Int, isStartPocket: Boolean) {
        viewModelScope.launch {
            val dbProduct = repository.getProductFromDB(product.id)
            try {
                if (dbProduct != null && dbProduct.category != 20) {
                    dbProduct.count += count
                    repository.updateNewProductInBasket(dbProduct)
                    _productsInBasket.value = repository.getProductsInBasket()
                } else {
                    if (product.category == 20 && isStartPocket) {
                        repository.addProductInBasket(product.copy(count = 1))
                        _productsInBasket.value = repository.getProductsInBasket()
                    } else {
                        product.count += count
                        repository.addProductInBasket(product)
                        _productsInBasket.value = repository.getProductsInBasket()
                    }
                }
            } catch (e: Exception) {
                Timber.e("Error add in basket: $e")
            }
        }
    }

    fun deleteProductFromBasket(productId: Int) {
        viewModelScope.launch {
            val product = repository.getProductFromDB(productId)
            if (product != null) {
                Timber.d("product = $product")
                repository.deleteProductFromBasket(product)
                _productsInBasket.value = repository.getProductsInBasket()
            }
        }
    }

    fun updateProductCount(newProduct: NewProduct, count: Int) {
        viewModelScope.launch {
            newProduct.count = count
            Timber.d("product = ${newProduct.count}")
            repository.updateNewProductInBasket(newProduct)
            _productsInBasket.value = repository.getProductsInBasket()
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
        block: String,
        entrance: String,
        floor: String,
        flat: String,
        contact: String,
        notice: String,
        isFromOrder: Boolean,
        navController: NavHostController
        //-----

    ) {
        viewModelScope.launch {
            val client = repository.getClientInfo()
            if (client != null) {
                val addressParameters = AddressParameters(
                    clientId = client.id,
                    region = region,
                    city = city,
                    street = street,
                    house = house,
                    block = block.ifEmpty { null },
                    entrance = entrance.ifEmpty { null },
                    floor = floor.ifEmpty { null },
                    flat = flat.ifEmpty { null },
                    courierNotice = notice,
                    phoneContact = client.phone,
                    nameContact = client.name,
                    notice = notice
                )
                val newAddress =
                    if (contact.isEmpty()) {
                        repository.createAddress(
                            addressParameters
                        )
                    } else {
                        repository.createAddress(
                            addressParameters
                        )
                    }
                Timber.d("NEW ADDRESS = $newAddress")
                if (newAddress != null) {
                    if (isFromOrder) navController.navigate(
                        MainNavRoute.CreateOrderScreen.withArgs(false.toString(), "0")
                    ) else {
                        navController.popBackStack(
                            MainNavRoute.AddressesScreen.path,
                            inclusive = false,
                            saveState = false
                        )

                    }
                } else {
                    Toast.makeText(
                        navController.context,
                        "Ошибка, данные не были отправлены, возможно проблемы с интернетом",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    navController.context,
                    "Не был указан корректный адрес",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as App
                val token = ClientStorage(application.applicationContext).get().accessToken
                val repository = ClientRepository(
//                    ApiOrder.makeOrderApi(token),
                    ApiClient.makeClientApi(token),
                    YouWaterDB.getYouWaterDB(application.baseContext)?.newProductDao()!!
                )
                return ClientViewModel(
                    repository
                ) as T
            }
        }
    }
}