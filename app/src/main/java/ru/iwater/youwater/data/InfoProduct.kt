package ru.iwater.youwater.data

import com.google.gson.annotations.SerializedName

data class InfoProduct(
    @SerializedName("app_display")
    val appDisplay: Boolean,
    @SerializedName("app_name")
    val appName: String?,
    @SerializedName("category")
    val category: Int,
    @SerializedName("company_id")
    val companyId: Int,
    @SerializedName("created_by")
    val createdBy: Int,
    @SerializedName("date_created")
    val dateCreated: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    @SerializedName("measure")
    val measure: Int,
    @SerializedName("min_price")
    val minPrice: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: List<Price>,
    @SerializedName("shname")
    val shname: String?,
    @SerializedName("site_display")
    val siteDisplay: Boolean,
    @SerializedName("updated_by")
    val updatedBy: Int,
    var count: Int = 0,
) {

    fun getPriceOnCount(count: Int): Int {
        var price = 0
        return when (this.id) {
            81 -> {
                this.price.forEach {
                    if(it.border <= count) {
                        price = if (count < 10) {
                            (it.price - 30) * count
                        } else {
                            (it.price - 10) * count
                        }
                    }
                }
                price
            }
            84 -> {
                this.price.forEach {
                    if(it.border <= count) {
                        price = if (count < 10) {
                            (it.price - 30) * count
                        } else {
                            (it.price - 10) * count
                        }
                    }
                }
                price
            }
            else -> {
                this.price.forEach {
                    if(it.border <= count) {
                        price = it.price * count
                    }
                }
                price
            }
        }
    }
}