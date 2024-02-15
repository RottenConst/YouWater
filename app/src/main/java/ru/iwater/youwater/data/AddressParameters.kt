package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

data class AddressParameters(
    @SerializedName("client_id")
    val clientId: Int,
    val region: String,
    val city: String,
    val street: String,
    val house: String,
    val block: String? = null,
    val entrance: String? = null,
    val floor: String? = null,
    val flat: String? = null,
    val contact: String? = null,
    @SerializedName("courier_notice")
    val courierNotice: String,
    @SerializedName("phone_contact")
    val phoneContact: String,
    @SerializedName("name_contact")
    val nameContact: String,
    @SerializedName("notice")
    val notice: String
)
