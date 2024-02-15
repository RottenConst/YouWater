package ru.iwater.youwater.vm

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.AuthClient
import ru.iwater.youwater.repository.AuthorisationRepository
import ru.iwater.youwater.screen.navigation.StartNavRoute
import ru.iwater.youwater.utils.StatusPinCode
import ru.iwater.youwater.utils.StatusSession
import timber.log.Timber
import javax.inject.Inject


class AuthViewModel @Inject constructor(
    private val authorisationRepository: AuthorisationRepository
) : ViewModel() {

    private val _statusPinCode: MutableLiveData<StatusPinCode> = MutableLiveData()
    val statusPinCode: LiveData<StatusPinCode>
        get() = _statusPinCode

    private val _statusSession: MutableLiveData<StatusSession> = MutableLiveData(StatusSession.CHECKED)
    val statusSession: LiveData<StatusSession>
        get() = _statusSession

    private val _clientId: MutableLiveData<Int> = MutableLiveData()
    val clientId: LiveData<Int>
        get() = _clientId

    private val clientAuth = authorisationRepository.getAuthClient()

    /* проверка телефона:
        существует в базе - переход на экран ввода пин кода
        не сужествует - переход на экран регистации
     */
    fun authPhone(
        phone: String,
        navController: NavHostController
    ) {
        val telNum =
            StringBuilder(phone).insert(0, "+7(").insert(6, ") ").insert(11, '-').toString()
        viewModelScope.launch {
            Timber.d("auth")
            val authPhone = authorisationRepository.authPhone(telNum)
            when {
                authPhone == null -> {
                    Toast.makeText(navController.context, "ошибка соединения", Toast.LENGTH_LONG)
                        .show()
                }
                authPhone.status -> {
                    Timber.d("auth ${authPhone.clientId}")
                    navController.navigate(StartNavRoute.EnterPinCodeScreen.withArgs(telNum, authPhone.clientId.toString()))
                }
                else -> {
                    navController.navigate(StartNavRoute.RegisterScreen.withArgs(telNum))
                }
            }
        }
    }

    fun isValidatePhone(phone: String): Boolean {
        return phone.contains(Regex("""\d{10}"""))
    }

    /*
        проверка пин кода
     */
    fun checkPin(clientId: Int, phone: String, pinCode: String, isCheck: Boolean) {
        viewModelScope.launch {
            if (isCheck) {
                val clientFullAuth = authorisationRepository.checkCode(clientId = clientId, phone = phone, pinCode = pinCode)
                when {
                    clientFullAuth == null -> {
                        _statusPinCode.value = StatusPinCode.NET_ERROR
                    }

                    clientFullAuth.accessToken.isNotEmpty() -> {
                        _statusPinCode.value = StatusPinCode.DONE
                        saveClient(clientFullAuth)
                    }

                    clientFullAuth.accessToken.isEmpty() -> {
                        _statusPinCode.value = StatusPinCode.ERROR
                    }
                }
            } else {
                _statusPinCode.value = StatusPinCode.EDIT_PIN
            }
        }
    }

    /*
        проверка сессии пльзователя
     */
    fun checkSession() {
        viewModelScope.launch {
            val client = authorisationRepository.checkSession(clientAuth)
            when (client) {
                true -> _statusSession.value = StatusSession.TRY
                false -> _statusSession.value = StatusSession.FALSE
                else -> _statusSession.value = StatusSession.ERROR
            }
        }
    }

    fun isValidName(name: String): Boolean {
        return !name.contains(Regex("""[^A-zА-я\s]"""))
    }

    fun isValidEmail(email: String): Boolean {
        return email.contains(Regex("""(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})"""))
    }

    /*
        Регистрация клинта
     */
    suspend fun registerClient(
        phone: String,
        name: String,
        email: String,
        isMailing: Boolean,
        navController: NavHostController
    ) {
        val registerClient = authorisationRepository.registerClient(
            phone, name, email, isMailing
        )
        if (registerClient != null && registerClient["status"].asBoolean) {
            isRegisteredClient(
                isMailing,
                phone,
                registerClient["client_id"].asInt,
                navController
            )
        } else {
            Toast.makeText(navController.context, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun isRegisteredClient(
        isMailing: Boolean,
        phone: String,
        clientId: Int,
        navController: NavHostController
    ) {
        if (authorisationRepository.authPhone(phone)?.status == true) {
//            authorisationRepository.setMailing(clientId, isMailing)
            navController.navigate(
                StartNavRoute.EnterPinCodeScreen.withArgs(phone, clientId.toString())
            )
        } else {
            Toast.makeText(navController.context, "ошибка соединения", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveClient(clientAuth: AuthClient) {
        viewModelScope.launch {
            authorisationRepository.saveAuthClient(clientAuth)
        }
    }
}