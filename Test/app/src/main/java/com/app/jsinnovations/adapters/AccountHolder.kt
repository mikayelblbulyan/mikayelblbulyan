package com.app.jsinnovations.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.jsinnovations.R
import com.app.jsinnovations.enums.Account
import com.app.jsinnovations.models.User

class AccountHolder(v:View):RecyclerView.ViewHolder(v) {

    private val title: TextView = v.findViewById(R.id.title)
    private val value: TextView = v.findViewById(R.id.value)

    fun configure(user: User, account: Account, context: Context){
        when(account){
            Account.FIRST_NAME ->{
                title.text = context.getString(R.string.first_name)
                value.text = user.name
            }
            Account.LAST_NAME ->{
                title.text = context.getString(R.string.last_name)
                value.text = user.surname
            }
            Account.COMPANY ->{
                title.text = context.getString(R.string.company)
                value.text = "JS Innovations"
            }
        }
    }

}