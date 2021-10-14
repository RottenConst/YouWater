package ru.iwater.youwater.iteractor

import android.content.Context
import ru.iwater.youwater.data.AuthClient

const val STATE_ACCOUNT = "ACCOUNT_STATE"

class ClientStorage(context: Context) : StorageStateAuthClient {

    private val preferencesStateAccount =
        context.getSharedPreferences(STATE_ACCOUNT, Context.MODE_PRIVATE)

    override fun save(data: AuthClient) {
        val editor = preferencesStateAccount.edit()
        editor.putString(STATE_ACCOUNT + "session", data.session)
        editor.putInt(STATE_ACCOUNT, data.clientId)
        editor.putString(STATE_ACCOUNT + "company", data.company)

        editor.apply()
    }

    override fun get(): AuthClient {
        val session = preferencesStateAccount.getString(STATE_ACCOUNT + "session", "").toString()
        val id = preferencesStateAccount.getInt(STATE_ACCOUNT, 0)
        val company = preferencesStateAccount.getString(STATE_ACCOUNT + "company", "").toString()

        return AuthClient(id, company, session)
    }

    override fun remove() {
        val editor = preferencesStateAccount.edit()
        editor.clear()
        editor.apply()
    }

}