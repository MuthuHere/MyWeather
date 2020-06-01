package com.sample.myweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sample.myweather.R
import com.sample.myweather.model.Weather
import com.sample.myweather.service.RetrofitBase
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherAdapter(var data : List<Weather>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(data: Weather, position: Int) {
            itemView.apply {
                findViewById<TextView>(R.id.date_textview).text = getDateString(data.applicable_date)
                findViewById<TextView>(R.id.weather_state_textview).text = data.weather_state_name
                val weatherStateImageView = findViewById<ImageView>(R.id.weather_state_imageview)
                weatherStateImageView?.let {
                    Glide.with(it.context)
                        .load(
                            RetrofitBase.API_WEATHER_STATE_ICON_URL.replace("X".toRegex(),
                                data.weather_state_abbr.orEmpty()))
                        .override(it.context.resources.getDimensionPixelSize(R.dimen.margin_30dp),
                            it.context.resources.getDimensionPixelSize(R.dimen.margin_30dp))
                        .fitCenter()
                        .into(it)
                }
                val suffix = context.getString(R.string.degree_fahrenheit)
                findViewById<TextView>(R.id.min_temp_textview).text = getTemperature(data.min_temp) + suffix
                findViewById<TextView>(R.id.max_temp_textview).text = getTemperature(data.max_temp) + suffix
            }
        }

        private fun getTemperature(temp: Float): String {
            return (temp * (9f / 5) + 32).toInt().toString()
        }

        private fun getDateString(datetime: String?): String {
            return datetime?.let{
                var dateString = ""
                try {
                    val parsedDate =
                        SimpleDateFormat("yyyy-MM-dd",
                            Locale.getDefault()).parse(datetime)
                    dateString = parsedDate?.let {
                        SimpleDateFormat("EEE, MM/dd", Locale.getDefault()).format(parsedDate)
                    }.orEmpty()
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
                dateString
            }.orEmpty()
        }

    }

}
