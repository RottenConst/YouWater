package ru.iwater.youwater.data

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

@Keep
data class ClientUserData(
    @SerializedName("client_id")
    val id: Int,
    @SerializedName("date_created")
    val dateCreated: String,
    @SerializedName("type")
    val type: Int = 1,
    @SerializedName("client_data")
    val clientData: JsonObject,
)
