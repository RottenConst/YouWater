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

    private val authClient = authRepository.getAuthClient()

    init {
        getClientInfo()
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