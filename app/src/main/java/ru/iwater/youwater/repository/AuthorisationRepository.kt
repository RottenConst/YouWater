package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.data.AuthClient
import ru.iwater.youwater.data.PhoneStatusClient
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiAuthClient
import timber.log.Timber
import javax.inject.Inject

class AuthorisationRepository @Inject constructor(
    private val authStorage: StorageStateAuthClient
) {

    private val apiAuth: ApiAuthClient = ApiAuthClient.makeClientApi()

    //проверка номера телефона в базе,
    suspend fun authPhone(phone: String): PhoneStatusClient? {
        val phoneAnswer = JsonObject()
        phoneAnswer.addProperty("phone", phone)
        phoneAnswer.addProperty("company_id", 7)
        return try {
            val phoneStatus = apiAuth.authPhone(phoneAnswer)
            phoneStatus
        }catch (e: Exception) {
            Timber.e("error auth phone: $e")
            null
        }
    }

    suspend fun checkCode(clientId: Int, phone: String, pinCode: String): AuthClient? {
        val code = JsonObject()
        code.addProperty("phone", phone)
        code.addProperty("company_id", 7)
        code.addProperty("sms", pinCode)
        try {
            val token = apiAuth.checkCode(code)
            if (token != null) {
                Timber.i("Client = ${token.accessToken} ${token.refreshToken}")
                return AuthClient(
                    clientId = clientId,
                    company = "7",
                    accessToken = token.accessToken,
                    refreshToken = token.refreshToken
                )
            }
        }catch (e: Exception) {
            Timber.e("error check code: $e")
        }
        return null
    }

    suspend fun checkSession(clientAuth: AuthClient): Boolean {
        val refreshToken = JsonObject()
        if (clientAuth.refreshToken.isNotEmpty()) {
            Timber.d("TOKEN = ${clientAuth.refreshToken}")
            refreshToken.addProperty("refresh_token", clientAuth.refreshToken)
            try {
                val token = apiAuth.refreshTokens(refreshToken)
                val authClient = AuthClient(
                    clientId = clientAuth.clientId,
                    company = clientAuth.company,
                    accessToken = token?.accessToken ?: "",
                    refreshToken = token?.refreshToken ?: ""
                )
                Timber.d("auth client = ${authClient.accessToken}")
                saveAuthClient(authClient)
                return authClient.refreshToken.isNotEmpty() && authClient.accessToken.isNotEmpty()
            } catch (e: Exception) {
                Timber.e("error check session $e")
            }
        }
        return false
    }

    suspend fun registerClient(phone: String, name: String, email: String, mailingConsent: Boolean, companyId: Int = 7): JsonObject? {
        val registerClient = JsonObject()
        registerClient.addProperty("phone", phone)
        registerClient.addProperty("name", name)
        registerClient.addProperty("email", email)
        registerClient.addProperty("mailing_consent", mailingConsent)
        registerClient.addProperty("company_id", companyId)
        return try {
            val client = apiAuth.register(registerClient)
            if (client.isSuccessful) {
               client.body()
            } else {
                null
            }
        }catch (e: Exception) {
            Timber.e("Register error: $e")
            null
        }
    }

    suspend fun setMailing(clientId: Int, isMailing: Boolean) {
        try {
            val mailing = JsonObject()
            if (isMailing) {
                mailing.addProperty("mailing_consent", 1)
                apiAuth.mailing(clientId, mailing)
            } else {
                mailing.addProperty("mailing_consent", 0)
                apiAuth.mailing(clientId, mailing)
            }
        } catch (e: Exception) {
            Timber.e("error set mailing: $e")
        }

    }

    fun saveAuthClient(authClient: AuthClient?) {
        if (authClient != null) {
            authStorage.save(authClient)
        }
    }

    fun deleteClient() {
        authStorage.remove()
    }

    fun getAuthClient(): AuthClient = authStorage.get()
}