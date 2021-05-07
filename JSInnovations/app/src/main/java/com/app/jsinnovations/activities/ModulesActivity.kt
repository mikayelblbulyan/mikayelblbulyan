package com.app.jsinnovations.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.jsinnovations.R
import com.app.jsinnovations.adapters.ModulesAdapter
import com.app.jsinnovations.api.BackendApi
import com.app.jsinnovations.models.Module
import com.app.jsinnovations.models.Total
import com.app.jsinnovations.sharing.LoginHelper
import com.app.jsinnovations.utils.ItemOffsetDecorationVertical
import com.app.jsinnovations.utils.NetworkUtil
import com.app.jsinnovations.utils.Utils
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_modules.*
import java.util.*
import kotlin.math.roundToInt

class ModulesActivity : AppCompatActivity() {

    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private var modulesAdapter: ModulesAdapter? = null
    private var isFirstTime = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val boardId = intent.getStringExtra("boardId")
        val address = intent?.getStringExtra("address")
        placeTitle.text = address
        val realm = Realm.getDefaultInstance()
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        networkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver, intentFilter)
        stationRecyclerView.addItemDecoration(
            ItemOffsetDecorationVertical(
                Utils.convertDpToPixel(5).roundToInt()
            )
        )
        stationRecyclerView.layoutManager = LinearLayoutManager(this)
        modulesAdapter = ModulesAdapter(this)
        if (Utils.isNetworkConnected(this))
            BackendApi.getInstance().getModules(LoginHelper.getAccessToken(this)!!, boardId!!) {
                    modules, total ->
                total._id = boardId
                realm.executeTransaction {
                    realm.copyToRealmOrUpdate(modules)
                    realm.copyToRealmOrUpdate(total)
                }
                modulesAdapter?.setBoardId(boardId)
                modulesAdapter?.setModules(modules)
                modulesAdapter?.setTotal(total)
                stationRecyclerView.adapter = modulesAdapter
            }
        else {
            networkStatusText.text = getString(R.string.offline)
            networkStatusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            networkStatusImage.setImageResource(R.drawable.oval_red)
            val modules = ArrayList<Module>()
            modules.addAll(realm.copyFromRealm(
                realm.where(Module::class.java).equalTo("boardId", boardId).findAll()
            ))
            val total = realm.copyFromRealm(
                realm.where(Total::class.java).equalTo("_id", boardId).findFirst()!!
            )!!
            modulesAdapter?.setBoardId(boardId!!)
            modulesAdapter?.setModules(modules)
            modulesAdapter?.setTotal(total)
            stationRecyclerView.adapter = modulesAdapter
        }
        backArrow.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    inner class NetworkChangeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val status: Int = NetworkUtil.getConnectivityStatusString(context)
            if ("android.net.conn.CONNECTIVITY_CHANGE" == intent.action) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    networkStatusText.text = getString(R.string.offline)
                    networkStatusText.setTextColor(ContextCompat.getColor(this@ModulesActivity, android.R.color.holo_red_dark))
                    networkStatusImage.setImageResource(R.drawable.oval_red)
                    isFirstTime = false
                }
                else{
                    if(!isFirstTime){
                        networkStatusText.text = getString(R.string.online)
                        networkStatusText.setTextColor(ContextCompat.getColor(this@ModulesActivity, android.R.color.holo_green_dark))
                        networkStatusImage.setImageResource(R.drawable.oval_green)
                        val realm = Realm.getDefaultInstance()
                        val boardId = getIntent().getStringExtra("boardId")
                        BackendApi.getInstance().getModules(LoginHelper.getAccessToken(this@ModulesActivity)!!, boardId!!)
                        { modules, total ->
                            total._id = boardId
                            realm.executeTransaction {
                                realm.copyToRealmOrUpdate(modules)
                                realm.copyToRealmOrUpdate(total)
                            }
                            modulesAdapter?.setModules(modules)
                            modulesAdapter?.setTotal(total)
                            modulesAdapter?.notifyDataSetChanged()
                        }
                    }
                    else
                        isFirstTime = false
                }
            }
        }
    }
}
