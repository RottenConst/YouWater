package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class AutoTaskData(
    val client_data: ClientData,
    val client_id: Int,
    val id: Int
)

@Keep
data class ClientData(
    val email: String,
    val lastname: String,
    val name: String,
    val phone: String
)