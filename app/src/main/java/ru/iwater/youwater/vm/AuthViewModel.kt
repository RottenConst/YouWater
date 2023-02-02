package ru.iwater.youwater.vm

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.AuthClient
import ru.iwater.youwater.repository.AuthorisationRepository
import ru.iwater.youwater.screen.MainActivity
import javax.inject.Inject

enum class StatusPhone {LOAD, ERROR, NET_ERROR, DONE}
enum class StatusPinCode{LOAD, ERROR, DONE}
enum class StatusSession{TRY, FALSE, ERROR}

class AuthViewModel @Inject constructor(
    private val authorisationRepository: AuthorisationRepository
): ViewModel() {

    private val _statusPhone: MutableLiveData<StatusPhone> = MutableLiveData()
    val statusPhone: LiveData<StatusPhone>
        get() = _statusPhone

    private val _statusPinCode: MutableLiveData<StatusPinCode> = MutableLiveData()
    val statusPinCode: LiveData<StatusPinCode>
        get() = _statusPinCode

    private val _statusSession: MutableLiveData<StatusSession> = MutableLiveData()
    val statusSession: LiveData<StatusSession>
        get() = _statusSession

    private val _clientId: MutableLiveData<Int> = MutableLiveData()
    val clientId: LiveData<Int>
        get() = _clientId

    private val clientAuth = authorisationRepository.getAuthClient()



    fun authPhone(phone: String) {
        viewModelScope.launch {
            _statusPhone.value = StatusPhone.LOAD
            val authPhone = authorisationRepository.authPhone(phone)
            when {
                authPhone == null -> {
                    _statusPhone.value = StatusPhone.NET_ERROR
                }
                authPhone.status -> {
                    _statusPhone.value = StatusPhone.DONE
                    _clientId.value = authPhone.clientId
                    clientAuth.clientId = authPhone.clientId
                }
                !authPhone.status -> {
                    _statusPhone.value = StatusPhone.ERROR
                }
            }
        }
    }

    fun checkPin(context: Context?, pinCode: String, clientId: Int) {
        viewModelScope.launch {
            _statusPinCode.value = StatusPinCode.LOAD
            val clientFullAuth = authorisationRepository.checkCode(clientId, pinCode)
            when {
                clientFullAuth == null -> {
                    _statusPinCode.value = StatusPinCode.ERROR
                    Toast.makeText(context, "Ошибка соединения", Toast.LENGTH_LONG).show()
                }
                clientFullAuth.session.isNotEmpty() -> {
                    _statusPinCode.value = StatusPinCode.DONE
                    saveClient(clientFullAuth)
                    MainActivity.start(context)
                }
                clientFullAuth.session.isEmpty() -> {
                    _statusPinCode.value = StatusPinCode.ERROR
                    Toast.makeText(context, "Неверный пин код", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun checkSession() {
        viewModelScope.launch {
            when(authorisationRepository.checkSession(clientAuth)) {
                true -> _statusSession.value = StatusSession.TRY
                false -> _statusSession.value = StatusSession.FALSE
                else -> _statusSession.value = StatusSession.ERROR
            }
        }
    }

    fun registerClient(phone: String, name: String, email: String, isMailing: Boolean) {
        viewModelScope.launch {
            val registerClient = authorisationRepository.registerClient(
                phone, name, email
            )
            if (registerClient != null) {
                val status = registerClient.get("status").asBoolean
                val clientId = registerClient["client_id"].asInt
                if (status) {
                    authorisationRepository.setMailing(clientId, isMailing)
                    authPhone(phone)
                }
            }
        }
    }

    private fun saveClient(clientAuth: AuthClient) {
        viewModelScope.launch {
            authorisationRepository.saveAuthClient(clientAuth)
        }
    }
}