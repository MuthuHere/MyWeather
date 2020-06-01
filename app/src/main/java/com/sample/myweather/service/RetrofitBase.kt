package com.sample.myweather.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBase {

    const val API_BASE_URL = "https://www.metaweather.com"
    const val API_WEATHER_STATE_ICON_URL = "$API_BASE_URL/static/img/weather/png/64/X.png"

    fun getApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(OkHttpClient().newBuilder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}

