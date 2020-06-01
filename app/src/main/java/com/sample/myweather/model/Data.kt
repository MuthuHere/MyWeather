package com.sample.myweather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherResponse(val consolidated_weather: List<Weather>?,
                           val time: String?,
                           val sun_rise: String?,
                           val sun_set: String?,
                           val timezone_name: String?,
                           val parent: Parent?,
                           val title: String?,
                           val location_type: String?,
                           val timezone: String?,
                           val woeid: String?): Parcelable

@Parcelize
data class Parent(val title: String?,
                  val location_type: String?,
                  val woeid: String?): Parcelable

@Parcelize
data class Location(val title: String?,
                    val location_type: String?,
                    val woeid: Int,
                    val latt_long: String?): Parcelable

@Parcelize
data class Weather(
    val weather_state_name: String?,
    val weather_state_abbr: String?,
    val created: String?,
    val applicable_date: String?,
    val min_temp: Float,
    val max_temp: Float,
    val the_temp: Float,
    val wind_speed: Float,
    val wind_direction_compass: String?,
    val visibility: Float,
    val humidity: Float
) : Parcelable

