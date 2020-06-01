package com.sample.myweather.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.sample.myweather.model.Location
import com.sample.myweather.model.ServiceResult
import com.sample.myweather.repo.Repository
import io.mockk.MockK
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class LocationViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var locationViewModel: LocationViewModel
    private val locationIdObserver: Observer<Int> = mockk(relaxed = true)
    private val locationsDataObserver: Observer<List<Location>> = mockk(relaxed = true)
    private val repository = mockk<Repository>()

    @Before
    fun setUp() {
        locationViewModel = LocationViewModel(repository)
        Dispatchers.setMain(TestCoroutineDispatcher())
        locationViewModel.preferredLocation.observeForever(locationIdObserver)
        locationViewModel.locations.observeForever(locationsDataObserver)
    }

    @Test
    fun `given pref location value - when set - observer notified`(){
        locationViewModel.setAsPreferredLocation(12345)
        val slot = slot<Int>()
        verify { locationIdObserver.onChanged(capture(slot)) }
    }

    @Test
    fun `given location query - when searched - returns list of results`(){
        val query = "Chicago"
        coEvery { repository.findLocationWithQuery(query) } returns getMockResponse()
        locationViewModel.fetchLocation("Chicago")
        val slot = slot<List<Location>>()
        verify { locationsDataObserver.onChanged(capture(slot)) }
    }

    private fun getMockResponse(): ServiceResult.Success<List<Location>> {
        val list = ArrayList<Location>()
        list.add(Location(title = "Chicago", woeid = 12345, location_type = "City", latt_long = "123.45, 432.12"))
        return ServiceResult.Success<List<Location>>(list)
    }

    fun getMockException(): ServiceResult.Error {
        return ServiceResult.Error("Sorry. Exception returned")
    }
}
