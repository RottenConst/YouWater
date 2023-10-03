package ru.iwater.youwater.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


//const val ImageUrl = "https://crm.new.iwatercrm.ru/iwatercrm/images"//prod
//const val BASE_URL_API = "https://app.iwatercrm.ru/iwater/" //prod
//
const val BASE_URL_API = "https://api.iwatercrm.ru/iwater/" //test
const val ImageUrl = "https://dev.new.iwatercrm.ru/iwatercrm/images"//test

object RetrofitFactory {
    private const val AUTH_KEY = "3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX"


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
            .baseUrl(BASE_URL_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiWater::class.java)
    }
}