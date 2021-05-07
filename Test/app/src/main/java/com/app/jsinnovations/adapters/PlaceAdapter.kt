package com.app.jsinnovations.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.app.jsinnovations.R
import com.app.jsinnovations.models.Place

class PlaceAdapter(val context: Context):RecyclerView.Adapter<PlaceHolder>() {

    lateinit var places: ArrayList<Place>
    var listener: ((placeId: String, address: String)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        val inflatedView = parent.inflate(R.layout.item_place, false)
        return PlaceHolder(inflatedView)
    }

    override fun getItemCount() = places.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.configure(places[position], position + 1, context)
        holder.setListener {
            listener?.invoke(places[position]._id!!, places[position].name!!)
        }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }
}