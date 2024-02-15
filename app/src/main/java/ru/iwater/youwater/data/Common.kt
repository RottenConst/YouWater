package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

data class Common(
    val available: Boolean,
    val date: Any?,
    @SerializedName("day_num")
    val dayNum: Int,
    @SerializedName("part_types")
    val partTypes: List<Boolean>
)
