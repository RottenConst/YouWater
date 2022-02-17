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
    @SerializedName("region")
    val region: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("fact_address")
    val factAddress: String,
    @SerializedName("address_full")
    val addressFull: String,
    @SerializedName("coords")
    val coords: String,
    @SerializedName("contact")
    val contact: String,
    @SerializedName("tanks")
    val tanks: Int,
    @SerializedName("for_delete")
    val forDelete: Int,
    @SerializedName("time_change")
    val timeChange: Int,
    @SerializedName("user_changing")
    val userChanging: Int,
    @SerializedName("avg_difference")
    val avgDifference: String?,
    @SerializedName("last_date")
    val lastDate: String,
    @SerializedName("email")
    val email: String
)
