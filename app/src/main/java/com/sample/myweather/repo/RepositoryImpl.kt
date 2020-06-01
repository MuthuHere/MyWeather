package com.sample.myweather.repo

import com.sample.myweather.model.Location
import com.sample.myweather.model.ServiceResult
import com.sample.myweather.model.WeatherResponse
import com.sample.myweather.service.ApiService
import com.sample.myweather.service.RetrofitBase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class RepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val apiService: ApiService = RetrofitBase.getApiService()
): Repository {

    override suspend fun findLocationWithQuery(location: String): ServiceResult<List<Location>> {
        return withContext(ioDispatcher) {
            processCall {
                apiService.findLocationWithQuery(location)
            }
        }
    }

    override suspend fun fetchWeatherForLocationId(locationId: String): ServiceResult<WeatherResponse> {
        return withContext(ioDispatcher) {
            processCall {
                apiService.fetchWeatherForLocationId(locationId)
            }
        }
    }

    suspend fun <T> processCall(
        call: suspend () -> Response<T>
    ): ServiceResult<T> {
        val genericError = "Sorry. Something went wrong."
        val networkError = "Sorry. Internet might not be connected."
        return try {

            val serviceCallback = call()
            val body = serviceCallback.body()
            if (serviceCallback.isSuccessful && body != null) {
                ServiceResult.Success(body)
            } else {
                ServiceResult.Error(serviceCallback.message().orEmpty())
            }

        } catch (exception: Exception) {
            when (exception) {
                is IOException -> {
                    ServiceResult.Error(networkError)
                }
                else -> {
                    ServiceResult.Error(genericError)
                }
            }
        }
    }
}
