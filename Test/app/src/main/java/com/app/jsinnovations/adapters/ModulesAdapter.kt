package com.app.jsinnovations.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.app.jsinnovations.R
import com.app.jsinnovations.activities.SettingsActivity
import com.app.jsinnovations.models.Module
import com.app.jsinnovations.models.Total

class ModulesAdapter(val context:Context) : RecyclerView.Adapter<ModulesAdapter.ModuleHolder>() {

    private var modules: ArrayList<Module>? = null
    private var total: Total? = null
    private var boardId: String? = null

    fun setBoardId(boardId: String){
        this.boardId = boardId
    }

    fun setTotal(total: Total?) {
        this.total = total
    }

    fun setModules(modules: ArrayList<Module>?) {
        this.modules = modules
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleHolder {
        val inflatedView = parent.inflate(R.layout.item_wash_resourse, false)
        return ModuleHolder(inflatedView)
    }

    override fun getItemCount() = 6

    override fun onBindViewHolder(holder: ModuleHolder, position: Int) {
        if (position == 5)
            holder.configureTotal()
        else{
            holder.configureModule(position)
            holder.dragItem.setOnClickListener {
                val intent = Intent(context, SettingsActivity::class.java)
                intent.putExtra("boardId", boardId)
                intent.putExtra("moduleId", modules?.get(position)?._id)
                context.startActivity(intent)
            }
        }

    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    inner class ModuleHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val number: TextView = v.findViewById(R.id.number)
        private val name: TextView = v.findViewById(R.id.name)
        private val water: TextView = v.findViewById(R.id.water)
        private val electricity: TextView = v.findViewById(R.id.electricity)
        private val liquid: TextView = v.findViewById(R.id.liquid)
        private val time: TextView = v.findViewById(R.id.time)
        private val moduleRelativeLayout: RelativeLayout = v.findViewById(R.id.module)
        private val totalRelativeLayout: RelativeLayout = v.findViewById(R.id.total)
        val dragItem: LinearLayout = v.findViewById(R.id.drag_item)

        fun configureModule(position: Int) {
            number.text = (position + 1).toString()
            name.text = modules?.get(position)?.name
            water.text = modules?.get(position)?.waterValue.toString()
            electricity.text = modules?.get(position)?.electricityValue.toString()
            liquid.text = modules?.get(position)?.liquidValue.toString()
            time.text = modules?.get(position)?.time.toString()
        }

        fun configureTotal() {
            moduleRelativeLayout.visibility = View.GONE
            totalRelativeLayout.visibility = View.VISIBLE
            water.text = total?.water.toString()
            electricity.text = total?.electricity.toString()
            liquid.text = total?.liquid.toString()
            time.text = total?.minutes.toString()
        }
    }
}