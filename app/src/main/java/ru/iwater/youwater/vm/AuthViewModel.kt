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
import timber.log.Timber
import javax.inject.Inject

enum class StatusPinCode { ERROR, DONE, NET_ERROR }
enum class StatusSession { TRY, FALSE, ERROR }

class AuthViewModel @Inject constructor(
    private val authorisationRepository: AuthorisationRepository
) : ViewModel() {

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

    fun authPhone(
        phone: String,
//        navController: NavController
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
//                    navController.navigate(
//                        LoginFragmentDirections.actionLoginFragmentToEnterPinCodeFragment(
//                            telNum,
//                            authPhone.clientId
//                        )
//                    )
                    Timber.d("auth ${authPhone.clientId}")
                    navController.navigate(StartNavRoute.EnterPinCodeScreen.withArgs(telNum, authPhone.clientId.toString()))
                }
                else -> {
//                    navController.navigate(
//                        LoginFragmentDirections.actionLoginFragmentToRegisterFragment(telNum)
//                    )
                    navController.navigate(StartNavRoute.RegisterScreen.withArgs(telNum))
                }
            }
        }
    }

    fun checkPin(pinCode: String, clientId: Int) {
        viewModelScope.launch {
            val clientFullAuth = authorisationRepository.checkCode(clientId, pinCode)
            when {
                clientFullAuth == null -> {
                    _statusPinCode.value = StatusPinCode.NET_ERROR
                }
                clientFullAuth.session.isNotEmpty() -> {
                    _statusPinCode.value = StatusPinCode.DONE
                    saveClient(clientFullAuth)
                }
                clientFullAuth.session.isEmpty() -> {
                    _statusPinCode.value = StatusPinCode.ERROR
                }
            }
        }
    }

    fun checkSession() {
        viewModelScope.launch {
            when (authorisationRepository.checkSession(clientAuth)) {
                true -> _statusSession.value = StatusSession.TRY
                false -> _statusSession.value = StatusSession.FALSE
                else -> _statusSession.value = StatusSession.ERROR
            }
        }
    }

    suspend fun registerClient(
        phone: String,
        name: String,
        email: String,
        isMailing: Boolean,
//        navController: NavController
        navController: NavHostController
    ) {
        val registerClient = authorisationRepository.registerClient(
            phone, name, email
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
//        navController: NavController
    ) {
        if (authorisationRepository.authPhone(phone)?.status == true) {
            authorisationRepository.setMailing(clientId, isMailing)
            navController.navigate(
                StartNavRoute.EnterPinCodeScreen.withArgs(phone, clientId.toString())
//                RegisterFragmentDirections.actionRegisterFragmentToEnterPinCodeFragment(
//                    phone,
//                    clientId
//                )
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