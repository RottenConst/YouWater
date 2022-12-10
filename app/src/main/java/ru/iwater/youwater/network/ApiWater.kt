package ru.iwater.youwater.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import ru.iwater.youwater.data.*

interface ApiWater {

    @GET("products/")
    suspend fun getProductList():List<Product>

    @GET("product-detail/{product_id}")
    suspend fun getProduct(
        @Path("product_id") productId: Int
    ): Product?

    @GET("category/")
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

    @GET("client-detail/{client_id}/")
    suspend fun getClientDetail(
        @Path("client_id") clientId: Int
    ): Client?

    @FormUrlEncoded
    @POST("sign-up/")
    suspend fun singUp(
        @Field("id") id: Int,
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("email") email: String
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST("sign-up/")
    suspend fun register(
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("email") email: String
    ): Response<JsonObject>

    @POST("order-app/")
    suspend fun createOrder(
        @Body order: Order
    ): Response<JsonObject>

    @POST("order-app/")
    suspend fun createOrderApp(
        @Body order: Order
    ): Response<JsonObject>

    @GET("client-orders-app/{client_id}/")
    suspend fun getOrderClient(
        @Path("client_id") clientId: Int
    ): List<OrderFromCRM>?

    @POST("creating_auto_task")
    suspend fun sendUserData(
        @Body editClientData: ClientUserData
    ): AutoTaskData?

    @PUT("client-detail/{client_id}/")
    suspend fun editUserData(
        @Path("client_id") clientId: Int,
        @Body clientData: JsonObject
    ): Response<JsonObject>?

    @GET("address/{client_id}/")
    suspend fun getAllAddresses(
        @Path("client_id") clientId: Int
    ):List<RawAddress>

    @GET("address-detail/{address_id}")
    suspend fun getAddress(
        @Path("address_id") addressId: Int
    ): RawAddress?

    @FormUrlEncoded
    @POST("address/")
    suspend fun createNewAddress(
        @Field("client_id") clientId: Int,
        @Field("contact") contact: String,
        @Field("region") region: String,
        @Field("fact_address") factAddress: String,
        @Field("address") address: String,
        @Field("coords") coords: String,
        @Field("active") active: Int,
        @Field("full_address") fullAddress: String,
        @Field("return_tare") returnTare: Int,
        @Field("phone_contact") phoneContact: String,
        @Field("name_contact") nameContact: String,
        @Field("address_json") addressJson: JsonObject,
        @Field("notice") notice: String
    ): Response<JsonObject>

    @PUT("address-delete/{id}/")
    suspend fun deleteAddress(
        @Path("id") idAddress: Int
    ): Response<JsonObject>

    @PUT("order-app-detail/{orderNumber}/")
    suspend fun setStatusPayment(
        @Path("orderNumber") orderNumber: Int,
        @Body parameters: JsonObject,
    ): Response<JsonObject>

    @GET("status_order/{order_id}/")
    suspend fun getStatusOrder(
        @Path("order_id") orderId: Int
    ): Response<List<JsonObject>>

    @GET("starter-eligible/{client_id}")
    suspend fun isStartPocket(
        @Path("client_id") client_id: Int
    ): Response<JsonObject>

    @GET("promo/")
    suspend fun getPromo(): List<PromoBanner>?

    @GET("last-client-order/{client_id}/")
    suspend fun isLastOrder(
        @Path("client_id") clientId: Int
    ): Response<JsonObject>

    @GET("order-app-detail/{lastOrderId}/")
    suspend fun getLastOrderInfo(
        @Path("lastOrderId") lastOrderId: Int
    ): OrderFromCRM?
}