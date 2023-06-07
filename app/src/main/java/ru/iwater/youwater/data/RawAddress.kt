package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RawAddress (
    @SerializedName("id")
    val id: Int,
    @SerializedName("region")
    val region: String?,
    @SerializedName("fact_address")
    val factAddress: String,
    @SerializedName("full_address")
    val fullAddress: String,
    @SerializedName("verified")
    var verified: Boolean,
    @SerializedName("notice")
    var notice: String?
)