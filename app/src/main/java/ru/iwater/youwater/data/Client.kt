package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Дате класс модель клиента
 */
@Keep
data class Client(
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: Int,
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("client_id")
    val client_id: Int,
    @SerializedName("tanks")
    val tanks: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("session")
    val session: String,
    @SerializedName("contact")
    val contact: String,
    @SerializedName("address")
    val address: String?,
    @SerializedName("mailing_consent")
    val mailing: Int
)
