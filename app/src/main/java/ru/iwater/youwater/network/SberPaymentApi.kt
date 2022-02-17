package ru.iwater.youwater.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.iwater.youwater.data.PaymentCard

interface SberPaymentApi {

    @FormUrlEncoded
    @POST ("register.do")
    suspend fun registerOrder(
        @Field("userName") userName: String,
        @Field("password") password: String,
        @Field("orderNumber") orderNumber: String,
        @Field("amount") amount: Int,
        @Field("returnUrl") returnUrl: String,
        @Field("pageView") pageView: String
    ): JsonObject
}