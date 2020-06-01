package com.sample.myweather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    private var _showProgress = MutableLiveData<Boolean>()
    var showProgress: LiveData<Boolean> = _showProgress

    private var _dialog = MutableLiveData<String>()
    var dialog: LiveData<String> = _dialog

    fun setProgress(status: Boolean) {
        _showProgress.value = status
    }

    fun setMessageForDialog(message: String?) {
        _dialog.value = message
    }

}
