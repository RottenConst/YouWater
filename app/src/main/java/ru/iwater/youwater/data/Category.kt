package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("company_id")
    val companyId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("visible_app")
    val visibleApp: Boolean
)
