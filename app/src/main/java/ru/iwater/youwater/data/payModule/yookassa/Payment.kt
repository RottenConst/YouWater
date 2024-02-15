package ru.iwater.youwater.data.payModule.yookassa

import com.google.gson.annotations.SerializedName

data class Payment(
    val amount: Amount,
    val description: String,
    @SerializedName("payment_token")
    val paymentToken: String,
    val capture: Boolean
)