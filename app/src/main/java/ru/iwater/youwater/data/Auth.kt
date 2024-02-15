package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 *

 */
@Keep
data class PhoneStatusClient (
    @SerializedName("client_id")
    val clientId: Int,
    @SerializedName("status")
    val status: Boolean
    )

@Keep
data class AuthClient(
    @SerializedName("client_id")
    var clientId: Int = 7,
    @SerializedName("company")
    var company: String = "",
    @SerializedName("access_token")
    var accessToken: String = "",
    @SerializedName("refresh_token")
    var refreshToken: String = ""
)

data class Token(
    @SerializedName("access_token")
    val accessToken: String = "",
    @SerializedName("refresh_token")
    val refreshToken: String = ""
)