package ru.iwater.youwater.repository

import com.google.gson.JsonObject
import ru.iwater.youwater.data.AuthClient
import ru.iwater.youwater.data.Client
import ru.iwater.youwater.data.PhoneStatusClient
import ru.iwater.youwater.iteractor.ClientStorage
import ru.iwater.youwater.iteractor.StorageStateAuthClient
import ru.iwater.youwater.network.ApiWater
import ru.iwater.youwater.network.RetrofitFactory
import timber.log.Timber
import javax.inject.Inject

class AuthorisationRepository @Inject constructor(
    private val authStorage: StorageStateAuthClient
) {

    private val apiAuth: ApiWater = RetrofitFactory.makeRetrofit()
    private val authClient = AuthClient()

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
        if (clientAuth.clientId == 0) clientAuth.clientId += 1119
        client.addProperty("client_id", clientAuth.clientId)
        client.addProperty("session", clientAuth.session)
        try {
            return apiAuth.checkSession(client)?.get("check")?.asBoolean
        } catch (e: Exception) {
            Timber.e("error check session")
        }
        return null
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