package com.sample.myweather.utils

import android.content.Context

class ExtUtils {

    companion object {
        const val PREF_FILE = "LOCATION_PREFS"
        const val KEY_SAVED_LOCATION = "SAVED_LOCATION"

        fun isWeatherLocDataAvailable(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
            return prefs.getInt(KEY_SAVED_LOCATION, 0) != 0
        }

        fun saveWeatherLoc(context: Context, locationWoeid: Int) {
            val prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
            prefs.edit().putInt(KEY_SAVED_LOCATION, locationWoeid).commit()
        }

        fun getWeatherLocId(context: Context): Int {
            val prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
            return prefs.getInt(KEY_SAVED_LOCATION, 0)
        }

        fun clearPrefs(context: Context) = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit().clear().commit()
    }

}
