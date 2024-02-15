package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
@Keep
data class NewAddress(
    val id: Int,
    @SerializedName("client_id")
    val clientId: Int,
    @SerializedName("updated_by")
    val updatedBy: Int,
    val address: String,
    val coords: String,
    @SerializedName("delivery_area_id")
    val deliveryAreaId: Int,
    val verified: Boolean,
    @SerializedName("courierNotice")
    val courierNotice: String,
    val contact: String,
    @SerializedName("phone_contact")
    val phoneContact: String,
    @SerializedName("name_contact")
    val nameContact: String,
    val notice: String,
    val region: String,
    val city: String,
    val street: String,
    val house: String,
    val block: String?,
    val entrance: String?,
    val floor: String?,
    val flat: String?
) {
    fun getDeliveryProperty(): JsonObject {
        val deliveryProperty = JsonObject().apply {
            addProperty("region", this@NewAddress.region)
            addProperty("city", this@NewAddress.city)
            addProperty("street", this@NewAddress.street)
            addProperty("house", this@NewAddress.house)
            addProperty("block", this@NewAddress.block)
            addProperty("entrance", this@NewAddress.entrance)
            addProperty("floor", this@NewAddress.floor)
            addProperty("flat", this@NewAddress.flat)
        }
        return deliveryProperty
    }
}