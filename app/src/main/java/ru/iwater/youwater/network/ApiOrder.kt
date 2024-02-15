package ru.iwater.youwater.network

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.iwater.youwater.data.BannerList
import ru.iwater.youwater.data.Category
import ru.iwater.youwater.data.CreatedOrder
import ru.iwater.youwater.data.InfoProduct
import ru.iwater.youwater.data.Measure
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.data.Order
import ru.iwater.youwater.data.StartPocket
import ru.iwater.youwater.data.TypeProduct
import ru.iwater.youwater.data.payModule.MessagePay
import java.util.concurrent.TimeUnit

interface ApiOrder {

    /**
     * Список товаров.
     */
    @GET("products/")
    suspend fun getProductList():List<NewProduct>

    /**
     * Получение товара по id.
     */
    @GET("products/{product_id}/")
    suspend fun getAboutProduct(
        @Path("product_id") productId: Int
    ): InfoProduct?

    @GET("products/measures-list/")
    suspend fun getMeasuresList(): List<Measure>

    /**
     * Список категорий.
     */
    @GET("products/categories-list/")
    suspend fun getCategoryList():List<TypeProduct>?

    /**
     * test
     */
    @GET("products/categories-list/")
    suspend fun getCategoryList1():List<Category>?

    /**
     * Товары по категории.
     */
    @GET("/products/product-by-category/{category_id}/")
    suspend fun getProductByCategory(
        @Path("category_id") categoryId: Int
    ): List<NewProduct>?

    /**
     * Список получения всех акций приложения
     */
    @GET("promo/app-list/")
    suspend fun getPromo():BannerList

    /**
     * Проверка на доступность для клиента стартовых пакетов
     */
    @GET("client/starter-eligible/")
    suspend fun isStartPocket(): StartPocket?

    /**
     * Метод создания заявки клиента.
     */
    @POST("order-app/create/")
    suspend fun createOrder(
        @Body order: JsonObject
    ):CreatedOrder?

    /**
     * Получение заявки по id
     */
    @GET("order-app/{order_app_id}/")
    suspend fun getCreatedOrder(
        @Path("order_app_id") orderAppId: Int
    ): CreatedOrder

    /**
     * Метод обновления статуса оплаты заявки клиента
     */
    @PUT("order-app/acq/{order_id}/")
    suspend fun updatePayStatusInfo(
        @Path("order_id") orderId: Int,
        @Body acqId: JsonObject
    ): Response<JsonObject>

    /**
     * Получение списка заявок клиента по ид клиента.
     */
    @GET("order-app/client-order-app/")
    suspend fun getOrderClient():List<CreatedOrder>?

    companion object {
        private const val BASE_URL_ORDER_API = "https://client-api-orders.iwatercrm.ru/" //test
//        private const val BASE_URL_ORDER_API = "https://orders-app.iwater-crm.online/" //prod

        fun makeOrderApi(token: String): ApiOrder {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
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
                .baseUrl(BASE_URL_ORDER_API)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiOrder::class.java)
        }
    }
}