package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.FileInputStream
import java.util.Properties

@Keep
data class PaymentCard(
    @SerializedName("userName")
    val userName: String = UserNameSber,
    @SerializedName("password")
    val password: String = passwordSber,
    @SerializedName("orderNumber")
    val orderNumber: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("returnUrl")
    val returnUrl: String = "http://605d3ea8e59a.ngrok.io",
    @SerializedName("phone")
    val phone: String
)

//prod
//const val UserNameSber = "p602720481107-api"
//const val passwordSber = "r6tMp1y78"
//test
const val UserNameSber = "t602720481107-api"
const val passwordSber = "ZwUEyuso"