package ru.iwater.youwater.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Headers
import java.util.concurrent.TimeUnit

object RetrofitFactory {
    const val AUTH_KEY = "3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX"
    const val BASE_URL = "http://api.iwatercrm.ru/iwater/" //test
//    const val BASE_URL = "http://app.iwatercrm.ru/iwater/" //prod

    fun makeRetrofit(): ApiWater {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Authorization", AUTH_KEY)
                    .addHeader("X-Client-Type", "Android")
                    .build()
                return@addInterceptor chain.proceed(request)
            }
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiWater::class.java)
    }
}

object RetrofitGoogleService {
    const val BASE_URL = "https://maps.googleapis.com/"


    fun makeRetrofit(): GoogleMapApi {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GoogleMapApi::class.java)

    }
}

object RetrofitSberApi {
    const val BASE_URL = "https://3dsec.sberbank.ru/payment/rest/"

    fun makeRetrofit(): SberPaymentApi {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor{chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Connection", "keep-alive")
                    .build()
                return@addInterceptor chain.proceed(request)
            }
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SberPaymentApi::class.java)
    }
}