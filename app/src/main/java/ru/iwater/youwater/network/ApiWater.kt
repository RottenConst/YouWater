package ru.iwater.youwater.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import ru.iwater.youwater.data.*
import ru.iwater.youwater.data.payModule.MessagePay

interface ApiWater {

    @GET("products/")
    suspend fun getProductList():List<Product>

    @GET("product-detail/{product_id}")
    suspend fun getProduct(
        @Path("product_id") productId: Int
    ): Product?

    @GET("favorites-list/{client_id}/")
    suspend fun getFavoriteProduct(
        @Path("client_id") clientId: Int
    ): Favorite?

    @FormUrlEncoded
    @PUT("favorites-list/{client_id}/")
    suspend fun addToFavoriteProduct(
        @Path("client_id") clientId: Int,
        @Field("product_id") productId: Int
    ): ResponseStatus?

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "favorites-list/{client_id}/", hasBody = true)
    suspend fun deleteFavoriteProduct(
        @Path("client_id") clientId: Int,
        @Field("product_id") productId: Int
    ): ResponseStatus?

    @GET("categoryProducts_list/")
    suspend fun getCategoryList():List<TypeProduct>?

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

    @GET("client_detail/{client_id}/")
    suspend fun getClientDetail(
        @Path("client_id") clientId: Int
    ): Client?

    @FormUrlEncoded
    @POST("sign-up/")
    suspend fun register(
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("email") email: String
    ): Response<JsonObject>

    @PUT("mailing-consent/{client_id}/")
    suspend fun mailing(
        @Path("client_id") clientId: Int,
        @Body clientData: JsonObject
    ): Response<JsonObject>?

    @POST("order-app/")
    suspend fun createOrder(
        @Body order: Order
    ): OrderApp?

    @GET("client-orders-app/{client_id}/")
    suspend fun getOrderClient(
        @Path("client_id") clientId: Int
    ): List<OrderFromCRM>?

    @PUT("client_detail/{client_id}/")
    suspend fun editUserData(
        @Path("client_id") clientId: Int,
        @Body clientData: JsonObject
    ): Response<JsonObject>?

    @GET("address/{client_id}/")
    suspend fun getAllAddresses(
        @Path("client_id") clientId: Int
    ):List<RawAddress>

    @POST("address/")
    suspend fun createNewAddress(
        @Body parameters: JsonObject
    ): Response<JsonObject>

    @PUT("address_delete/{id}/")
    suspend fun deleteAddress(
        @Path("id") idAddress: Int
    ): Response<JsonObject>

    @POST("OrdersInfo/{orderNumber}/")
    suspend fun setStatusPayment(
        @Path("orderNumber") orderNumber: Int,
        @Body parameters: JsonObject,
    ): Response<JsonObject>

    @GET("starter-eligible/{client_id}")
    suspend fun isStartPocket(
        @Path("client_id") clientId: Int
    ): StartPocket?

    @GET("promo/")
    suspend fun getPromo(): List<PromoBanner>?

    @FormUrlEncoded
    @POST("yookassa-order-mobile/")
    suspend fun createPay(
        @Field("amount") amount: String,
        @Field("description") description: String,
        @Field("payment_token") paymentToken: String,
        @Field("capture") capture: Boolean
    ): MessagePay?

    //Получить график доставки
    @POST("delivery-days/")
    suspend fun getDeliverySchedule(
        @Body addressJson: JsonObject
    ): DeliverySchedule?

    @POST("delete-account/{client_id}/")
    suspend fun deleteAccount(
        @Path("client_id") client_id: Int,
        @Body parameters: JsonObject
    ): DeleteMessage?
}