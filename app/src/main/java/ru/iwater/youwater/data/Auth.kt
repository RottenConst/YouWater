package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

/**
 *

 */
data class PhoneStatusClient (
    @SerializedName("client_id")
    val clientId: Int,
    @SerializedName("status")
    val status: Boolean
    )

data class AuthClient(
    @SerializedName("client_id")
    var clientId: Int = 0,
    @SerializedName("company")
    var company: String = "",
    @SerializedName("session")
    var session: String = ""
)