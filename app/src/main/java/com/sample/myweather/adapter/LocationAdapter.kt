package com.sample.myweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sample.myweather.R
import com.sample.myweather.model.Location
import com.sample.myweather.ui.LocationViewModel

class LocationAdapter(private val locationViewModel: LocationViewModel,
                      var locations : List<Location>) : RecyclerView.Adapter<LocationAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return ViewHolder(view, locationViewModel)
    }

    fun updateData(newPosts: List<Location>){
        locations = newPosts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(locations[position], position)
    }

    class ViewHolder(itemView: View, private val viewModel: LocationViewModel) : RecyclerView.ViewHolder(itemView){

        fun bind(data: Location, position: Int) {
            itemView.apply {
                val textView: TextView = findViewById(R.id.titleTextView)
                textView.text = data.title
                textView.setOnClickListener {
                    viewModel.setAsPreferredLocation(data.woeid)
                }
            }

        }

    }

}
