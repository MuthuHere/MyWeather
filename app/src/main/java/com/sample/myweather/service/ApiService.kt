package com.sample.myweather.service

import com.sample.myweather.model.Location
import com.sample.myweather.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @GET("/api/location/search/")
    suspend fun findLocationWithQuery(@Query("query") query: String): Response<List<Location>>

    @GET("/api/location/{woeid}/")
    suspend fun fetchWeatherForLocationId(@Path("woeid") woeid: String): Response<WeatherResponse>

}
