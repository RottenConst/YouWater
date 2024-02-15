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


//const val ImageUrl = "https://crm.new.iwatercrm.ru/iwatercrm/images"//prod
//const val BASE_URL_API = "https://app.iwatercrm.ru/iwater/" //prod
//const val ShopId = "248648"
//const val Key = "live_VPol5OKACLck1xIFichGArRTc4mJdso46woVL9AC2Jk"
//
//const val BASE_URL_API = "https://api.iwatercrm.ru/iwater/" //test
const val ImageUrl = "https://orders.dev.iwatercrm.ru/image"//test
const val BASE_URL_CLIENT_API = "https://client-api-clients.iwatercrm.ru/" //fast api client test
const val BASE_URL_ORDERS_API = "https://client-api-orders.iwatercrm.ru/"