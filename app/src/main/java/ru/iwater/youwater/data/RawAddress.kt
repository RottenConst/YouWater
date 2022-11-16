package ru.iwater.youwater.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Keep
@Entity
data class RawAddress (
    @SerializedName("id")
    @PrimaryKey
    val id: Int,
    @SerializedName("fact_address")
    val factAddress: String,
    @SerializedName("full_address")
    val fullAddress: String,
    @SerializedName("verified")
    var verified: Boolean,
    @SerializedName("notice")
    var notice: String?
)