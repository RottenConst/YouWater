package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.sql.Date

@Keep
data class Order(
    @SerializedName("client_id")
    var clientId: Int,
    @SerializedName("date")
    var date: String,
    @SerializedName("time_from")
    var timeFrom: String,
    @SerializedName("time_to")
    var timeTo: String,
    @SerializedName("notice")
    var notice: String?,
    @SerializedName("total_cost")
    var totalCost: Int,
    @SerializedName("payment_type")
    var paymentType: String?,
    @SerializedName("address_id")
    var addressId: Int,
    @SerializedName("product_list")
    var productList: MutableList<JsonObject>,
)