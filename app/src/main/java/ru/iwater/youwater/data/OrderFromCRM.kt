package ru.iwater.youwater.data

import androidx.annotation.Keep

@Keep
data class OrderFromCRM(
    val acq_order_id: String,
    val address: String,
    val address_json: AddressJson,
    val address_id: Int,
    val checked: Int,
    val client_id: Int,
    val company_id: String,
    val contact: String,
    val date: String,
    val email: String,
    val id: Int,
    val name: String,
    val notice: String,
    val order_cost: String,
    val order_id: Int?,
    val payment_type: String,
    val period: String,
    val status: Int,
    val system: String,
    val water_equip: List<WaterEquip>
)