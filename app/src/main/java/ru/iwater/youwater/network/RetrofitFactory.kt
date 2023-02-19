package ru.iwater.youwater.network

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import javax.security.cert.CertificateException


//const val ImageUrl = "https://crm.new.iwatercrm.ru/iwatercrm/images"//prod
//const val BASE_URL_SBER = "https://securepayments.sberbank.ru/payment/rest/" //prod
//const val BASE_URL_API = "https://app.iwatercrm.ru/iwater/" //prod

const val BASE_URL_API = "https://api.iwatercrm.ru/iwater/" //test
const val BASE_URL_SBER = "https://3dsec.sberbank.ru/payment/rest/" //test
const val ImageUrl = "https://dev.new.iwatercrm.ru/iwatercrm/images"//test

object RetrofitFactory {
    const val AUTH_KEY = "3OSkO8gl.puTQf56Hi8BuTRFTpEDZyNjkkOFkvlPX"


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

object RetrofitSberApi {


    fun makeRetrofit(): SberPaymentApi {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient.newBuilder()
//        val okHttpClient = OkHttpClient.Builder()
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
            .baseUrl(BASE_URL_SBER)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SberPaymentApi::class.java)
    }
}

object UnsafeOkHttpClient {
    // Create a trust manager that does not validate certificate chains
    val unsafeOkHttpClient: OkHttpClient

    // Install the all-trusting trust manager

        // Create an ssl socket factory with our all-trusting manager
        get() = try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}