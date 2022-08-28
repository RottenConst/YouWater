package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PaymentCard(
    //prod sber
    @SerializedName("userName")
    val userName: String = "p602720481107-api",
    @SerializedName("password")
    val password: String = "r6tMp1y78",
//    test sber
//    @SerializedName("userName")
//    val userName: String = "t602720481107-api",
//    @SerializedName("password")
//    val password: String = "ZwUEyuso",
    @SerializedName("orderNumber")
    val orderNumber: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("returnUrl")
    val returnUrl: String = "http://605d3ea8e59a.ngrok.io",
    @SerializedName("phone")
    val phone: String
)
