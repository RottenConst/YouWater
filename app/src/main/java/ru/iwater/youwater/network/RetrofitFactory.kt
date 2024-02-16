package ru.iwater.youwater.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


const val ImageUrl = "https://crm.new.iwatercrm.ru/iwatercrm/images"//prod
const val BASE_URL_API = "https://app.iwatercrm.ru/iwater/" //prod
//
const val ShopId = "248648"
const val Key = "live_VPol5OKACLck1xIFichGArRTc4mJdso46woVL9AC2Jk"
//const val BASE_URL_API = "https://api.iwatercrm.ru/iwater/" //test
//const val ImageUrl = "https://dev.new.iwatercrm.ru/iwatercrm/images"//test

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

    fun makeYookassaApi(): ApiYookassa {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(ShopId, Key))
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.yookassa.ru/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiYookassa::class.java)
    }
}


class BasicAuthInterceptor(user: String, password: String) :
    Interceptor {
    private val credentials: String

    init {
        credentials = Credentials.basic(user, password)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}