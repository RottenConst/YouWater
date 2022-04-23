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
    @SerializedName("name")
    val name: String,
    @SerializedName("client_id")
    val client_id: Int,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("session")
    val session: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("address")
    val address: String?
)
