package ru.iwater.youwater.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import ru.iwater.youwater.repository.AuthorisationRepository
import timber.log.Timber
import javax.inject.Inject

enum class StatusSendData {
    SUCCESS,
    ERROR
}

class ClientProfileViewModel @Inject constructor(
    private val authRepository: AuthorisationRepository
): ViewModel() {

    private val _client: MutableLiveData<Client> = MutableLiveData()
    val client: LiveData<Client>
        get() = _client

    private val _navigateToDataUser: MutableLiveData<Int> = MutableLiveData()
    val navigateToDataUser: LiveData<Int>
        get() = _navigateToDataUser

    private val _statusSend: MutableLiveData<StatusSendData> = MutableLiveData()
    val statusSend: LiveData<StatusSendData>
        get() = _statusSend

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

    //отправить данные пользователя на модерацию
    fun createAutoTask(clientId: Int, dataCreated: String, clientData: JsonObject) {
        viewModelScope.launch {
            val clientUserData = ClientUserData(id = clientId, dateCreated = dataCreated, clientData = clientData)
            val answer = authRepository.sendUserData(clientUserData)
            if (answer == "user data sent for moderation") _statusSend.value = StatusSendData.SUCCESS
                else _statusSend.value = StatusSendData.ERROR
        }
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