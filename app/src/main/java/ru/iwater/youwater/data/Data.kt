package ru.iwater.youwater.data

data class Data(
    val acq_order_id: String,
    val address: String,
    val address_id: Int,
    val address_json: AddressJsonX,
    val client_id: Int,
    val contact: String,
    val date: String,
    val date_closed: Any,
    val date_created: String,
    val email: String,
    val id: Int,
    val name: String,
    val notice: String,
    val `operator`: Any,
    val order_cost: String,
    val payment_type: String,
    val period: String,
    val system: String,
    val water_equip: List<WaterEquip>
)