package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

data class Banner(
    @SerializedName("description")
    val description: String,
    @SerializedName("discount_type")
    val discountType: Int,
    @SerializedName("discount_value")
    val discountValue: Double,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("picture")
    val picture: String,
    @SerializedName("product_list")
    val productList: List<ProductX>,
    @SerializedName("promocode")
    val promoCode: String,
    @SerializedName("start_date")
    val startDate: String
)