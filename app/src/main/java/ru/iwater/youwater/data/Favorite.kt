package ru.iwater.youwater.data


import com.google.gson.annotations.SerializedName

data class Favorite(
    @SerializedName("favorites_list")
    val favoritesList: List<Int>
)