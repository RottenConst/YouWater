package ru.iwater.youwater.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class Address (
    val region: String,
    val street: String,
    val house: Int,
    val building: String?,
    val entrance: Int?,
    val floor: Int?,
    val flat: Int?,
    val note: String?,
    @PrimaryKey
    val id: Int,
    )