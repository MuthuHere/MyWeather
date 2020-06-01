package com.sample.myweather.ui

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseFragment: Fragment() {

    private var alertDialog: AlertDialog? = null

    inline fun <reified V : ViewModel> Fragment.obtainViewModel(crossinline instance: () -> V): V {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return instance() as T
            }
        }
        return ViewModelProvider(this, factory).get(V::class.java)
    }

    fun showMessageDialog(message: String) {
        alertDialog?.dismiss()
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Weather Info")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                alertDialog?.dismiss()
            }
        alertDialog = builder.create()
        alertDialog?.show()
    }
}
