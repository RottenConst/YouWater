package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.AuthorisationRepository
import javax.inject.Inject

class ClientProfileViewModel @Inject constructor(
    private val authRepository: AuthorisationRepository
): ViewModel() {

    private val _client: MutableLiveData<Client> = MutableLiveData()
    val client: LiveData<Client>
        get() = _client

    private val _navigateToDataUser: MutableLiveData<Int> = MutableLiveData()
    val navigateToDataUser: LiveData<Int>
        get() = _navigateToDataUser

    private val authClient = authRepository.getAuthClient()

    init {
        getClientInfo()
    }

    fun displayDataUser(client: Client) {
        _navigateToDataUser.value = client.client_id
    }

    fun displayDataUserComplete() {
        _navigateToDataUser.value = null
    }

    private fun getClientInfo() {
        viewModelScope.launch {
            val client = authRepository.getClientInfo(authClient.clientId)
            if (client != null) {
                _client.value = client
            }
        }
    }
}