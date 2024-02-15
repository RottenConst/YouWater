package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

data class BannerList(
    @SerializedName("data")
    val banners: List<Banner>,
    @SerializedName("total_page")
    val totalPage: Int
)