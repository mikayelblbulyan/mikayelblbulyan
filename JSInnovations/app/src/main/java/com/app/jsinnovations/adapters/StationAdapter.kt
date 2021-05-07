package com.app.jsinnovations.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.app.jsinnovations.R
import com.app.jsinnovations.activities.ModulesActivity
import com.app.jsinnovations.models.Board

class StationAdapter(val context: Context) : RecyclerView.Adapter<StationAdapter.StationHolder>() {

    var boards: ArrayList<Board>? = null
    var address: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationHolder {
        val inflatedView = parent.inflate(R.layout.item_station, false)
        return StationHolder(inflatedView)
    }


    override fun getItemCount() = boards!!.size


    override fun onBindViewHolder(holder: StationHolder, position: Int) {
        holder.configure(position)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ModulesActivity::class.java)
            intent.putExtra("boardId", boards!![position]._id)
            intent.putExtra("address", address)
            context.startActivity(intent)
        }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    inner class StationHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val number: TextView = v.findViewById(R.id.number)
        private val cost: TextView = v.findViewById(R.id.cost)
        private val income: TextView = v.findViewById(R.id.income)
        private val revenue: TextView = v.findViewById(R.id.revenue)
        private val nominal: TextView = v.findViewById(R.id.nominal)

        fun configure(position: Int) {
            number.text = (position + 1).toString()
            cost.text = boards?.get(position)?.coasts.toString()
            income.text = boards?.get(position)?.income.toString()
            revenue.text = boards?.get(position)?.nominal.toString()
            nominal.text = boards?.get(position)?.nominal.toString()
        }

    }

}