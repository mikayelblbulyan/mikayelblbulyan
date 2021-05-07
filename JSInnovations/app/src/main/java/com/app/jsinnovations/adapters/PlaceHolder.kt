package com.app.jsinnovations.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.jsinnovations.R
import com.app.jsinnovations.models.Place

class PlaceHolder(private val v: View):RecyclerView.ViewHolder(v) {

    private val address = v.findViewById<TextView>(R.id.place)
    private val placeNumber = v.findViewById<TextView>(R.id.placeNumber)

    fun setListener(listener: () -> Unit){
        v.setOnClickListener {
            listener()
        }
    }

    fun configure(place: Place, position: Int, context: Context){
        placeNumber.text = context.getString(R.string.base, position)
        address.text = place.name
    }
}