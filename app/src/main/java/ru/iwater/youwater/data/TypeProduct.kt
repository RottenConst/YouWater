package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TypeProduct (
    @SerializedName("id")
    val id: Int,
    @SerializedName("company_id")
    val companyId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("visible_app")
    val visibleApp: Boolean,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("marketplace")
    val marketplace: Any? = null,
    @SerializedName("main_category_id")
    val mainCategoryId: Any? = null,
    @SerializedName("characteristics_list")
    val characteristicsList: List<Any>? = emptyList(),
    @SerializedName("nesting_level")
    val nestingLevel: Any? = null,
)