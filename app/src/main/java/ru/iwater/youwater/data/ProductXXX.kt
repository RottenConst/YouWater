package ru.iwater.youwater.data


import com.google.gson.annotations.SerializedName

data class ProductXXX(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int
)