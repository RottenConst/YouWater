package ru.iwater.youwater.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.iwater.youwater.data.AddressResult

interface GoogleMapApi {

    @GET("maps/api/geocode/json")
    suspend fun getAddressOnPlaceId(
        @Query("place_id") place_id: String,
        @Query("language") language: String,
        @Query("key") key: String
    ): AddressResult?

    @GET("maps/api/geocode/json")
    suspend fun getCoordinateOnAddress(
        @Query("address") address: String,
        @Query("language") language: String,
        @Query("key") key: String
    ): AddressResult?
}