package pl.sggw.sggwmeet.instance

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private var httpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(loggingInterceptor)
        .build()

    val retrofit=retrofit2.Retrofit.Builder()
        .baseUrl("http://3.68.195.28")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}