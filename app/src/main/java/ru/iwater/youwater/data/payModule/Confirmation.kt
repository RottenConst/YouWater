package ru.iwater.youwater.data.payModule

import com.google.gson.annotations.SerializedName

data class Confirmation(
    @SerializedName("confirmation_url")
    val confirmationUrl: String,
    val type: String
)