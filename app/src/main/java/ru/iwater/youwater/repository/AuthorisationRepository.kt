package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.data.AuthClient
import ru.iwater.youwater.data.Client
import ru.iwater.youwater.data.ClientUserData
import ru.iwater.youwater.data.PhoneStatusClient
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject

class AuthorisationRepository @Inject constructor(
    private val authStorage: StorageStateAuthClient
) {

    private val apiAuth: ApiWater = RetrofitFactory.makeRetrofit()

    suspend fun authPhone(phone: String): PhoneStatusClient? {
        val phoneAnswer = JsonObject()
        phoneAnswer.addProperty("phone", phone)
        try {
            val phoneStatus = apiAuth.authPhone(phoneAnswer)
            if (phoneStatus != null) {
                return phoneStatus
            }
        }catch (e: Exception) {
            Timber.e("error auth phone: $e")
        }
        return null
    }

    suspend fun checkCode(clientId: Int, pinCode: String): AuthClient? {
        val code = JsonObject()
        code.addProperty("client_id", clientId)
        code.addProperty("code", pinCode)
        try {
            val authClient = apiAuth.checkCode(code)
            if (authClient != null) {
                Timber.i("Client = ${authClient.clientId} ${authClient.company} ${authClient.session}")
                authClient.clientId = clientId
                return authClient
            }
        }catch (e: Exception) {
            Timber.e("error check code: $e")
        }
        return null
    }

    suspend fun checkSession(clientAuth: AuthClient): Boolean? {
        val client = JsonObject()
        if (clientAuth.clientId > 0) {
            clientAuth.clientId
            client.addProperty("client_id", clientAuth.clientId)
            client.addProperty("session", clientAuth.session)
            try {
                return apiAuth.checkSession(client)?.get("check")?.asBoolean
            } catch (e: Exception) {
                Timber.e("error check session")
            }
        }
        return false
    }

    suspend fun getClientInfo(clientId: Int): Client? {
        try {
            val client = apiAuth.getClientDetail(clientId)
            if (client != null) {
                return client
            }
        }catch (e: Exception) {
            Timber.e("error get client: $e")
        }
        return null
    }

    suspend fun registerClient(phone: String, name: String, email: String): JsonObject? {
        return try {
            val client = apiAuth.register(phone, name, email)
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

    suspend fun sendUserData(clientUserData: ClientUserData): String {
        try {
            val answer = apiAuth.sendUserData(clientUserData)
            if (answer?.id != null) {
                return "user data sent for moderation"
            }
        } catch (e: java.lang.Exception) {
            Timber.e("error send user data: $e")
        }
        return "user data not send"
    }

    suspend fun editUserData(clientId: Int, clientUserData: JsonObject): Boolean {
        try {
            val answer = apiAuth.editUserData(clientId, clientUserData)
            if (answer?.isSuccessful == true) {
                return answer.body()?.get("status")?.asBoolean == true
            }
        } catch (e: java.lang.Exception) {
            Timber.e("error edit user data: $e")
        }
        return false
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