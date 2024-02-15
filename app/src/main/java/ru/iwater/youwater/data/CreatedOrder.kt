package ru.iwater.youwater.data


import com.google.gson.annotations.SerializedName

data class CreatedOrder(
    @SerializedName("id")
    val id: Int,
    @SerializedName("client_id")
    val clientId: Int,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("company_id")
    val companyId: Int,
    @SerializedName("date_created")
    val dateCreated: String,
    @SerializedName("updated_by")
    val updatedBy: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("contact")
    val contact: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("time_from")
    val timeFrom: String,
    @SerializedName("time_to")
    val timeTo: String,
    @SerializedName("notice")
    val notice: String,
    @SerializedName("total_cost")
    val totalCost: Int,
    @SerializedName("payment_type")
    val paymentType: Int,
    @SerializedName("checked")
    val checked: Boolean,
    @SerializedName("status")
    val status: Int,
    @SerializedName("app_source")
    val appSource: String,
    @SerializedName("acq_id")
    val acqId: String,
    @SerializedName("payment_status")
    val paymentStatus: Int,
    @SerializedName("address_id")
    val addressId: Int,
    @SerializedName("date_closed")
    val dateClosed: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("product_list")
    val productList: List<ProductXXX>,
)