package ru.iwater.youwater.network

import com.google.gson.JsonObject
import com.pusher.pushnotifications.api.RefreshToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.iwater.youwater.data.PhoneStatusClient
import ru.iwater.youwater.data.Token
import java.util.concurrent.TimeUnit

interface ApiAuthClient {

    /**
     * Авторизация пользователя.
     */
    @POST( "auth/authorization/")
    suspend fun authPhone(
        @Body phone: JsonObject
    ): PhoneStatusClient?

    /**
     * Проверка смс.
     */
    @POST("auth/check-code/")
    suspend fun checkCode(
        @Body jsonObject: JsonObject
    ): Token?

    /**
     * Обновление токенов.
     */
    @POST("auth/refresh-tokens/")
    suspend fun refreshTokens(
        @Body jsonObject: JsonObject
    ): Token?

    /**
     * Регистрация пользователя.
     */
    @POST("auth/registration/")
    suspend fun register(
        @Body registerClientBody: JsonObject
    ): Response<JsonObject>

    /**
     * Выход из системы.
     */
    @FormUrlEncoded
    @DELETE("auth/logout/")
    suspend fun logout(
        @Field("client_id") clientId: Int,
        @Field("refresh_token") refreshToken: String
    ): Boolean

    @PUT("mailing-consent/{client_id}/")
    suspend fun mailing(
        @Path("client_id") clientId: Int,
        @Body clientData: JsonObject
    ): Response<JsonObject>?

    companion object {
        private const val BASE_URL_CLIENT_API = "https://client-api-clients.iwatercrm.ru/" //fast api client test
//        private const val BASE_URL_CLIENT_API = "https://clients-app.iwater-crm.online/" //fast api client prod
        fun makeClientApi(): ApiAuthClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Client-Type", "Android")
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
            .build().create(ApiAuthClient::class.java)
        }
    }

}