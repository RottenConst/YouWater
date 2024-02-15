package ru.iwater.youwater.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import ru.iwater.youwater.data.payModule.yookassa.Payment
import ru.iwater.youwater.data.payModule.yookassa.PaymentInfo
import java.io.IOException

interface ApiYookassa {

    /**
     * [Информация о платеже](https://yookassa.ru/developers/api#get_payment)
     *
     * @param paymentId уникальный индификатор платежа получается при создании платежа
     * @return В ответ на запрос придет объект платежа в актуальном статусе.
     */
    @GET("payments/{payment_id}")
    suspend fun getPayment(
        @Path("payment_id") paymentId: String
    ): PaymentInfo?

    /**
     * [Создание платежа](https://yookassa.ru/developers/api#create_payment)
     *
     * @param key Ключ идемпотентности
     * @param payment платеж
     * @return В ответ на запрос придет объект платежа в актуальном статусе.
     */
    @POST("payments")
    suspend fun createPayment(
        @Header("Idempotence-Key") key: String,
        @Body payment: Payment
    ): PaymentInfo?

    companion object {
        private const val ShopId = "226108"
        private const val Key = "test_c00r5oGlzdaQ6edc0jcDWaQ8DDurKOU1NNSWhPJhKL0"

//        private const val ShopId = "248648"
//        private const val Key = "live_VPol5OKACLck1xIFichGArRTc4mJdso46woVL9AC2Jk"
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
}

class BasicAuthInterceptor(user: String, password: String) :
    Interceptor {
    private val credentials: String

    init {
        credentials = Credentials.basic(user, password)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}