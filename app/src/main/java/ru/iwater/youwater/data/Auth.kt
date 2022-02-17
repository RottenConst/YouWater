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
    var clientId: Int = 0,
    @SerializedName("company")
    var company: String = "",
    @SerializedName("session")
    var session: String = ""
)