package ru.iwater.youwater.data


import com.google.gson.annotations.SerializedName

data class ClientEditData(
    @SerializedName("accounting_phone")
    val accountingPhone: String? = null,
    @SerializedName("activity_status")
    val activityStatus: Boolean = true,
    @SerializedName("auto_task_status")
    val autoTaskStatus: Boolean = true, //true for test
    @SerializedName("avg_difference")
    val avgDifference: Int? = null,
    @SerializedName("company_id")
    val companyId: Int = 7,
    @SerializedName("contact_person")
    val contactPerson: String? = null,
    @SerializedName("contract_company")
    val contractCompany: Int = 1,
    @SerializedName("email")
    val email: String,
    @SerializedName("inn")
    val inn: Int? = null,
    @SerializedName("manager_notice")
    val managerNotice: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("personal_discount_set")
    val personalDiscountSet: Boolean = false,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("type")
    val type: Boolean = false,
    @SerializedName("verified_client")
    val verifiedClient: Boolean = true
)