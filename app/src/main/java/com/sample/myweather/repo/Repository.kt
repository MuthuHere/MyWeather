package com.sample.myweather.repo

import com.sample.myweather.model.Location
import com.sample.myweather.model.ServiceResult
import com.sample.myweather.model.WeatherResponse

interface Repository {

    suspend fun findLocationWithQuery(location: String): ServiceResult<List<Location>>

    suspend fun fetchWeatherForLocationId(locationId: String): ServiceResult<WeatherResponse>
}
