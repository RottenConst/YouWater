package ru.iwater.youwater.data

data class Exception(
    val available: Boolean,
    val date: String,
    val day_num: Int,
    val part_types: List<Int>
)