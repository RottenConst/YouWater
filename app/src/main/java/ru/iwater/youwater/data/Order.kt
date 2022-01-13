package ru.iwater.youwater.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("client_id")
    var clientId: Int,
    @SerializedName("acq_order_id")
    var acqOrderId: Int?,
    @SerializedName("notice")
    var notice: String?,
    @SerializedName("water_equip")
    var waterEquip: MutableList<JsonObject>,
    @SerializedName("period")
    var period: String,
    @SerializedName("order_cost")
    var orderCost: Int,
    @SerializedName("address")
    var address: String,
    @SerializedName("payment_type")
    var paymentType: String?,
    @SerializedName("status")
    var status: Int,
    @SerializedName("email")
    var email: String?,
    @SerializedName("contact")
    var contact: String,
    @SerializedName("date")
    var date: String,
    @SerializedName("address_json")
    var addressJson: JsonObject,
    @SerializedName("name")
    var name: String
)