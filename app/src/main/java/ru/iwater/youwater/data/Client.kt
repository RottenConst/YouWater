package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Дате класс модель клиента
 */
@Keep
data class Client(
    val id: Int,
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("date_created")
    val dateCreated: String,
    @SerializedName("last_update_time")
    val lastUpdateTime: String,
    @SerializedName("last_update_by")
    val lastUpdateBy: Int,
    @SerializedName("avg_difference")
    val avgDifference: Int?,
    @SerializedName("last_date")
    val lastDate: String = "",
    @SerializedName("come_from")
    val comeFrom: String,
    @SerializedName("activity_status")
    val activityStatus: Boolean,
    @SerializedName("auto_task_status")
    val autoTaskStatus: Boolean,
    val sms: String,
    val session: String = "",
    @SerializedName("app_registration_date")
    val appRegistrationDate: String,
    val type: Boolean,
    val name: String,
    val phone: String,
    val email: String,
    @SerializedName("contract_company")
    val contractCompany: Int,
    @SerializedName("contact_person")
    val contactPerson: String = "",
    val verified: Boolean,
    val inn: Int = 0,
    @SerializedName("accounting_phone")
    val accountingPhone: String = "",
    @SerializedName("inactivity_date")
    val inactivityDate: String = "",
    @SerializedName("is_deleted")
    val isDeleted: Boolean,
    @SerializedName("manager_notice")
    val managerNotice: String = "",
    @SerializedName("personal_discount_set")
    val personalDiscountSet: Boolean,
    @SerializedName("mailing_consent")
    val mailingConsent: Boolean,
    @SerializedName("verified_client")
    val verifiedClient: Boolean
)
