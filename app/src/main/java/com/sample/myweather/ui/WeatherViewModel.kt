package com.sample.myweather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sample.myweather.model.ServiceResult
import com.sample.myweather.model.WeatherResponse
import com.sample.myweather.repo.Repository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: Repository) : BaseViewModel() {

    private var _weatherData = MutableLiveData<WeatherResponse>()
    var weatherData: LiveData<WeatherResponse> = _weatherData

    private var showSwipeRefreshHint = true

    fun showSwipeRefreshHint() = showSwipeRefreshHint

    fun setShouldShowSwipeRefreshHint(flag: Boolean){
        showSwipeRefreshHint = flag
    }

    fun fetchWeatherInfoForLocation(woeId: Int, swipeRefreshed: Boolean) {
        if(!swipeRefreshed)
            setProgress(true)
        viewModelScope.launch {
            when (val result = repository.fetchWeatherForLocationId(woeId.toString())) {
                is ServiceResult.Error -> {
                    setProgress(false)
                    setMessageForDialog(result.error)
                }
                is ServiceResult.Success -> {
                    setProgress(false)
                    _weatherData.value = result.data
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
