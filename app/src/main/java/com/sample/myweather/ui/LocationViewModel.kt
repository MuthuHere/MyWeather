package com.sample.myweather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sample.myweather.model.Location
import com.sample.myweather.model.ServiceResult
import com.sample.myweather.repo.Repository
import com.sample.myweather.utils.SingleEmissionMutableLiveData
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: Repository) : BaseViewModel() {

    private var _preferredLocation = SingleEmissionMutableLiveData<Int>()
    var preferredLocation: LiveData<Int> = _preferredLocation

    private var _locations = MutableLiveData<List<Location>>()
    var locations: LiveData<List<Location>> = _locations

    fun setAsPreferredLocation(locationWoeid: Int){
        _preferredLocation.value = locationWoeid
    }

    fun fetchLocation(location: String) {
        setProgress(true)
        viewModelScope.launch {
            when (val result = repository.findLocationWithQuery(location)) {
                is ServiceResult.Error -> {
                    setProgress(false)
                    setMessageForDialog(result.error)
                }
                is ServiceResult.Success -> {
                    setProgress(false)
                    _locations.value = result.data
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        clearList()
    }

    fun clearList() {
        _locations.value = null
        _preferredLocation.value = null
    }

}
