package com.app.jsinnovations.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.app.jsinnovations.R
import com.app.jsinnovations.enums.Account
import com.app.jsinnovations.models.User

class AccountAdapter(val context: Context):RecyclerView.Adapter<AccountHolder>() {

    lateinit var user: User

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        val inflatedView = parent.inflate(R.layout.item_account, false)
        return AccountHolder(inflatedView)
    }

    override fun getItemCount() = 3

    override fun onBindViewHolder(holder: AccountHolder, position: Int) {
        val account = Account.values()[position]
        holder.configure(user, account, context)
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }
}