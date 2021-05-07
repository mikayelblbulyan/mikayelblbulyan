package com.app.jsinnovations.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.app.jsinnovations.R
import com.app.jsinnovations.models.Setting

class SettingsAdapter(val context: Context):RecyclerView.Adapter<SettingsAdapter.SettingHolder>() {

    private var settings = ArrayList<Setting>()

    fun setSettings(settings: ArrayList<Setting>){
        this.settings = settings
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingHolder {
        val inflatedView = parent.inflate(R.layout.item_setting, false)
        return SettingHolder(inflatedView)
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    override fun getItemCount() = settings.size

    override fun onBindViewHolder(holder: SettingHolder, position: Int) {
        holder.value.text = settings[position].value.toString()
        holder.name.text = settings[position].name?.en
        when(settings[position].mid){
            0 -> holder.unit.text = context.getText(R.string.water_per_minute)
            1 -> holder.unit.text = context.getText(R.string.water_per_cubic_meter)
            2 -> holder.unit.text = context.getText(R.string.energy_consumption)
            3 -> holder.unit.text = context.getText(R.string.kilowatt_price)
            4 -> holder.unit.text = context.getText(R.string.liquid_consumption)
            5 -> holder.unit.text = context.getText(R.string.liquid_price)
        }
    }

    inner class SettingHolder(v: View):RecyclerView.ViewHolder(v){

        val value: TextView = v.findViewById(R.id.value)
        val unit: TextView = v.findViewById(R.id.unit)
        val name: TextView = v.findViewById(R.id.name)
    }
}