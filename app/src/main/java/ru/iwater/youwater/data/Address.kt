package ru.iwater.youwater.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address (
    @PrimaryKey
    val id: Int,
    val region: String,
    val address: String,
    val fullAddress: String = "$region, $address",
    val latitude: Long,
    val longitude: Long,
    val coordinate: String = "$latitude, $longitude"
    )