package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

data class Exception(
    val available: Boolean,
    val date: String,
    @SerializedName("day_num")
    val dayNum: Int,
    @SerializedName("part_types")
    val partTypes: List<Boolean>
)