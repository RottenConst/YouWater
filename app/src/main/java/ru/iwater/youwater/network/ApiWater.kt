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

    @FormUrlEncoded
    @PUT("favorites-list/{client_id}/")
    suspend fun addToFavoriteProduct(
        @Path("client_id") clientId: Int,
        @Field("product_id") productId: Int
    ): JsonObject?

    @GET("favorites-list/{client_id}/")
    suspend fun getFavoriteProduct(
        @Path("client_id") clientId: Int
    ): JsonObject?

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "favorites-list/{client_id}/", hasBody = true)
    suspend fun deleteFavoriteProduct(
        @Path("client_id") client_id: Int,
        @Field("product_id") productId: Int
    ): JsonObject?

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

    @POST("address/")
    suspend fun createNewAddress(
        @Body parameters: JsonObject
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

    @GET("order-app-detail/{lastOrderId}/")
    suspend fun getLastOrderInfo(
        @Path("lastOrderId") lastOrderId: Int
    ): OrderFromCRM?
}