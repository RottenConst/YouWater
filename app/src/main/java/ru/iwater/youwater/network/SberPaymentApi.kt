package ru.iwater.youwater.network

import com.google.gson.JsonObject
import retrofit2.http.*

interface SberPaymentApi {

    @FormUrlEncoded
    @POST ("register.do")
    suspend fun registerOrder(
        @Field("userName") userName: String,
        @Field("password") password: String,
        @Field("orderNumber") orderNumber: String,
        @Field("amount") amount: Int,
        @Field("returnUrl") returnUrl: String,
        @Field("pageView") pageView: String,
        @Field("phone") phone: String
    ): JsonObject

    @FormUrlEncoded
    @POST("getOrderStatusExtended.do")
    suspend fun getOrderStatus(
        @Field("userName") userName: String,
        @Field("password") password: String,
        @Field("orderId") orderId: String
    ): JsonObject
}