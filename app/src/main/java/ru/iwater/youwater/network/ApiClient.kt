package ru.iwater.youwater.network

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.iwater.youwater.data.AddressParameters
import ru.iwater.youwater.data.Client
import ru.iwater.youwater.data.ClientEditData
import ru.iwater.youwater.data.DeleteMessage
import ru.iwater.youwater.data.DeliverySchedule
import ru.iwater.youwater.data.Favorite
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.data.ResponseStatus
import ru.iwater.youwater.utils.Status
import java.util.concurrent.TimeUnit

interface ApiClient {

    /**
     * Получение клиента.
     */
    @GET("client/")
    suspend fun getClient(): Client?

    /**
     * Получение списка активных адресов клиента
     */
    @GET("address/list/")
    suspend fun getAddressList(): List<NewAddress>?

    /**
     * Создание адреса
     */
    @POST("address/create/")
    suspend fun createNewAddress(
        @Body newAddressParameter: AddressParameters
    ): NewAddress?

    /**
     * Удаление адреса.
     */
    @DELETE("address/delete/{address_id}/")
    suspend fun deleteAddress(
        @Path("address_id") addressId: Int
    ): Response<JsonObject>

    /**
     * Получение списка любимых товаров клиента.
     */
    @GET("client/favorites-list/")
    suspend fun getFavoriteList(): Favorite

    /**
     * Добавление товара в избранное
     */
    @PUT("client/favorites-list/update/")
    suspend fun addFavoriteProduct(
        @Body productId: JsonObject
    ): ResponseStatus?

    /**
     * Удаление из списка любимых товаров клиента
     */
    @HTTP(method = "DELETE", path = "client/favorites-list/delete/", hasBody = true)
    suspend fun deleteFavoriteProduct(
        @Body productId: JsonObject
    ):ResponseStatus?

    /**
     * Возвращает дни доставки по адресу.
     */
    @POST("address/delivery-day-parts/")
    suspend fun getDeliverySchedule(
        @Body addressDeliverySchedule: JsonObject
    ): DeliverySchedule

    /**
     * Обновление клиента.
     */
    @PUT("client/update/")
    suspend fun setDataClient(
        @Body clientEditData: ClientEditData
    ): Response<Client>

    /**
     * Согласие на рассылку рекламы.
     */
    @PUT("client/mailing-consent/")
    suspend fun mailing(
        @Body clientData: JsonObject
    ): Response<JsonObject>

    /**
     * Удаление аккаунта
     */
    @PUT("client/delete-account/")
    suspend fun deleteAccount(): DeleteMessage?

    @HTTP(method = "DELETE", path = "auth/logout/", hasBody = true)
    suspend fun logout(
        @Body body: JsonObject
    ): Status

    companion object {
        private const val BASE_URL_CLIENT_API = "https://client-api-clients.iwatercrm.ru/" //test
//        private const val BASE_URL_CLIENT_API = "https://clients-app.iwater-crm.online/" //prod

        fun makeClientApi(token: String): ApiClient {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor {chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    return@addInterceptor chain.proceed(request)
                }
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL_CLIENT_API)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiClient::class.java)
        }
    }
}