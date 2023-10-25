package ru.iwater.youwater.network

import retrofit2.http.GET
import retrofit2.http.Path
import ru.iwater.youwater.data.payModule.yookassa.PaymentInfo

interface ApiYookassa {

    @GET("payments/{payment_id}")
    suspend fun getPayment(
        @Path("payment_id") paymentId: String
    ): PaymentInfo?
}