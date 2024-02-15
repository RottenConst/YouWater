package ru.iwater.youwater.iteractor

import android.content.Context
import ru.iwater.youwater.data.AuthClient

const val STATE_ACCOUNT = "ACCOUNT_STATE"

class ClientStorage(context: Context) : StorageStateAuthClient {

    private val preferencesStateAccount =
        context.getSharedPreferences(STATE_ACCOUNT, Context.MODE_PRIVATE)

    override fun save(data: AuthClient) {
        val editor = preferencesStateAccount.edit()
        editor.putInt(STATE_ACCOUNT, data.clientId)
        editor.putString(STATE_ACCOUNT + "company", data.company)
        editor.putString(STATE_ACCOUNT + "access_token", data.accessToken)
        editor.putString(STATE_ACCOUNT + "refresh_token", data.refreshToken)
        editor.apply()
    }

    override fun get(): AuthClient {
        val accessToken = preferencesStateAccount.getString(STATE_ACCOUNT + "access_token", "").toString()
        val refreshToken = preferencesStateAccount.getString(STATE_ACCOUNT + "refresh_token", "").toString()
        val id = preferencesStateAccount.getInt(STATE_ACCOUNT, 0)
        val company = preferencesStateAccount.getString(STATE_ACCOUNT + "company", "").toString()

        return AuthClient(
            clientId = id,
            company = company,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    override fun remove() {
        val editor = preferencesStateAccount.edit()
        editor.clear()
        editor.apply()
    }

}