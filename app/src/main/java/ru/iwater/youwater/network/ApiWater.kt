package ru.iwater.youwater.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import ru.iwater.youwater.data.*

interface ApiWater {

    @GET("iwaterProducts_list/")
    suspend fun getProductList():List<Product>

    @GET("categoryProducts_list/")
    suspend fun getCategoryList():List<TypeProduct>

    @POST("auth-phone/")
    suspend fun authPhone(
        @Body phone: JsonObject
    ): PhoneStatusClient?

    @POST("check-code/")
    suspend fun checkCode(
        @Body jsonObject: JsonObject
    ): AuthClient?

    @POST("check-session/")
    suspend fun checkSession(
        @Body jsonObject: JsonObject
    ): JsonObject?

    @GET("iwaterClients_detail/{client_id}/")
    suspend fun getClientDetail(
        @Path("client_id") clientId: Int
    ): Client?


    @POST("OrderCreate/")
    suspend fun createOrder(
        @Body order: Order
    ): Response<JsonObject>

    @GET("return_of_applications_client_id/{client_id}/")
    suspend fun getOrderClient(
        @Path("client_id") clientId: Int
    ): List<OrderFromCRM>?

    @POST("creating_auto_task")
    suspend fun sendUserData(
        @Body editClientData: ClientUserData
    ): AutoTaskData?

    @GET("all_adresses/{client_id}/")
    suspend fun getAllAddresses(
        @Path("client_id") clientId: Int
    ):Response<List<JsonObject>>

    @POST("OrdersInfo/{orderNumber}")
    suspend fun setStatusPayment(
        @Path("orderNumber") orderNumber: Int,
        @Body parameters: JsonObject,
    ): Response<JsonObject>
}