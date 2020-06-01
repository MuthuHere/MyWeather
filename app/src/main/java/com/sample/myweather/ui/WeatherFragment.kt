package com.sample.myweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sample.myweather.R
import com.sample.myweather.adapter.WeatherAdapter
import com.sample.myweather.model.WeatherResponse
import com.sample.myweather.repo.RepositoryImpl
import com.sample.myweather.service.RetrofitBase
import com.sample.myweather.utils.ExtUtils
import kotlinx.android.synthetic.main.layout_progress.progress_view
import kotlinx.android.synthetic.main.weather_fragment.current_date_textview
import kotlinx.android.synthetic.main.weather_fragment.current_location_textview
import kotlinx.android.synthetic.main.weather_fragment.current_weather_state_textview
import kotlinx.android.synthetic.main.weather_fragment.divider1
import kotlinx.android.synthetic.main.weather_fragment.divider2
import kotlinx.android.synthetic.main.weather_fragment.divider3
import kotlinx.android.synthetic.main.weather_fragment.humidity_label_textview
import kotlinx.android.synthetic.main.weather_fragment.humidity_value_textview
import kotlinx.android.synthetic.main.weather_fragment.min_max_temp_textview
import kotlinx.android.synthetic.main.weather_fragment.settings_btn
import kotlinx.android.synthetic.main.weather_fragment.sunrise_label_textview
import kotlinx.android.synthetic.main.weather_fragment.sunrise_value_textview
import kotlinx.android.synthetic.main.weather_fragment.sunset_label_textview
import kotlinx.android.synthetic.main.weather_fragment.sunset_value_textview
import kotlinx.android.synthetic.main.weather_fragment.swipe_refresh_layout
import kotlinx.android.synthetic.main.weather_fragment.temp_type_textview
import kotlinx.android.synthetic.main.weather_fragment.the_temp_textview
import kotlinx.android.synthetic.main.weather_fragment.weather_list_recycler_view
import kotlinx.android.synthetic.main.weather_fragment.wind_label_textview
import kotlinx.android.synthetic.main.weather_fragment.wind_value_textview
import java.lang.Exception
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.Locale


class WeatherFragment : BaseFragment() {


    private val viewModel: WeatherViewModel by lazy {
        obtainViewModel{
            WeatherViewModel(RepositoryImpl())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.weather_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!ExtUtils.isWeatherLocDataAvailable(requireContext()))
            findNavController().navigate(R.id.action_weather_to_location)
        setDefaultViews(View.GONE)
        initUi(view)
        initViewModel()
    }

    private fun initUi(view: View) {
        swipe_refresh_layout?.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorWhite)
            setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            setOnRefreshListener {
                ExtUtils.getWeatherLocId(requireContext()).apply {
                    if(this != 0){
                        viewModel.fetchWeatherInfoForLocation(this, true)
                    }
                }
            }
        }
        settings_btn.setOnClickListener {
            findNavController().navigate(R.id.action_weather_to_location)
        }
    }

    private fun initViewModel() {
        ExtUtils.getWeatherLocId(requireContext()).apply {
            if(this != 0){
                viewModel.fetchWeatherInfoForLocation(this, false)
            }
        }

        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            it?.let{
                if(it)
                    progress_view.visibility = View.VISIBLE
                else
                    progress_view.visibility = View.GONE
            }
        })
        viewModel.dialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                showMessageDialog(it)
            }
        })
        viewModel.weatherData.observe(viewLifecycleOwner, Observer {
            it?.let {
                updateUI(it)
            }
        })
    }

    private fun updateUI(weather: WeatherResponse) {
        if(viewModel.showSwipeRefreshHint()){
            Toast.makeText(requireContext(), "Pull down to refresh the weather updates.", Toast.LENGTH_LONG).show()
            viewModel.setShouldShowSwipeRefreshHint(false)
        }
        swipe_refresh_layout.isRefreshing = false
        current_location_textview.text = getLocation(weather)
        current_date_textview.text = getLocalDateString("MMM dd, hh:mm a", weather.time)
        sunrise_value_textview.text = getLocalDateString("hh:mm a", weather.sun_rise)
        sunset_value_textview.text = getLocalDateString("hh:mm a", weather.sun_set)
        weather.consolidated_weather?.first()?.let { todaysWeather ->
            the_temp_textview.text = getTemperature(todaysWeather.the_temp)
            min_max_temp_textview.text = getMinMaxTemperature(todaysWeather.min_temp, todaysWeather.max_temp)
            val weatherStateImageView = view?.findViewById<ImageView>(R.id.current_weather_state_imageview)
            weatherStateImageView?.let {
                Glide.with(requireContext())
                    .load(RetrofitBase.API_WEATHER_STATE_ICON_URL.replace("X".toRegex(),
                        todaysWeather.weather_state_abbr.orEmpty()))
                    .fitCenter()
                    .into(it)
            }
            current_weather_state_textview.text = todaysWeather.weather_state_name.orEmpty()
            wind_value_textview.text = "${todaysWeather.wind_direction_compass.orEmpty()} ${todaysWeather.wind_speed.toInt()} mph"
            humidity_value_textview.text = "${todaysWeather.humidity.toInt()}%"
            setDefaultViews(View.VISIBLE)
        }


        weather.consolidated_weather?.let {
            weather_list_recycler_view.layoutManager = LinearLayoutManager(requireContext())
            val subList = if(it.size > 1) it.drop(1) else it
            weather_list_recycler_view.adapter = WeatherAdapter(subList)
        }
    }

    private fun setDefaultViews(visibility: Int) {
        divider1.visibility = visibility
        divider2.visibility = visibility
        divider3.visibility = visibility
        sunrise_label_textview.visibility = visibility
        sunset_label_textview.visibility = visibility
        wind_label_textview.visibility = visibility
        humidity_label_textview.visibility = visibility
        temp_type_textview.visibility = visibility
    }

    private fun getLocation(weather: WeatherResponse): String{
        return StringBuilder()
            .append(weather.title)
            .append(if(!weather.parent?.title.isNullOrEmpty()) ", ${weather.parent?.title}" else "").toString()
    }

    private fun getLocalDateString(format: String, datetime: String?): String {
        return datetime?.let{
            var dateString = ""
            try {
                val parsedDate =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                        Locale.getDefault()).parse(datetime)
                dateString = parsedDate?.let {
                    SimpleDateFormat(format, Locale.getDefault()).format(parsedDate)
                }.orEmpty()
            }
            catch (e: Exception){
                e.printStackTrace()
            }
            dateString
        }.orEmpty()
    }

    private fun getTemperature(temp: Float): String {
        return (temp * (9f / 5) + 32).toInt().toString()
    }

    private fun getMinMaxTemperature(minTemp: Float, maxTemp: Float): String {
        val suffix = getString(R.string.degree_fahrenheit)
        return "Day ${getTemperature(maxTemp)+suffix} -> Night ${getTemperature(minTemp)+suffix} "
    }

}
