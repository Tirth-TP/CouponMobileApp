package com.example.couponMobileApp

import android.util.Base64
import okhttp3.OkHttpClient
import  retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val AUTH = "Basic "+ Base64.encodeToString("stocard:123456".toByteArray(),Base64.NO_WRAP)
  private const val Base_URL = "http://stocard.project-demo.info/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Authorization", AUTH)
                    .addHeader("Connection", "close")
                .method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()


    val instance: UserApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(Base_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(UserApi::class.java)
    }
}