package com.sample.myweather.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sample.myweather.model.Location
import com.sample.myweather.model.ServiceResult
import com.sample.myweather.model.WeatherResponse
import com.sample.myweather.repo.Repository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before

import org.junit.Rule
import org.junit.Test

class WeatherViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var weatherViewModel: WeatherViewModel
    private val weatherDataObserver: Observer<WeatherResponse> = mockk(relaxed = true)
    private val repository = mockk<Repository>()

    @Before
    fun setUp() {
        weatherViewModel = WeatherViewModel(repository)
        Dispatchers.setMain(TestCoroutineDispatcher())
        weatherViewModel.weatherData.observeForever(weatherDataObserver)
    }

    @Test
    fun `given location woeId - when searched - returns weather response`(){
        val woeId = 12345
        coEvery { repository.fetchWeatherForLocationId(woeId.toString()) } returns getMockResponse()
        weatherViewModel.fetchWeatherInfoForLocation(woeId, false)
        val slot = slot<WeatherResponse>()
        verify { weatherDataObserver.onChanged(capture(slot)) }
    }

    private fun getMockResponse(): ServiceResult.Success<WeatherResponse> {
        val weatherResponse = mockk<WeatherResponse>(relaxed = true)
        return ServiceResult.Success<WeatherResponse>(weatherResponse)
    }

    fun getMockException(): ServiceResult.Error {
        return ServiceResult.Error("Sorry. Exception returned")
    }
}
