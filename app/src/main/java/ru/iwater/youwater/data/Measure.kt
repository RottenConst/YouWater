package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

data class Measure(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("shortname")
    val shortName: String
)
