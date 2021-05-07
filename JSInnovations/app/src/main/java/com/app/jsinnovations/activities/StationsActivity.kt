package com.app.jsinnovations.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.jsinnovations.R
import com.app.jsinnovations.adapters.StationAdapter
import com.app.jsinnovations.api.BackendApi
import com.app.jsinnovations.models.Board
import com.app.jsinnovations.sharing.LoginHelper
import com.app.jsinnovations.utils.ItemOffsetDecorationVertical
import com.app.jsinnovations.utils.NetworkUtil
import com.app.jsinnovations.utils.Utils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_stations.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class StationsActivity : AppCompatActivity() {

    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private var stationAdapter: StationAdapter? = null
    private var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stations)
        val placeId = intent?.getStringExtra("placeId")
        val address = intent?.getStringExtra("address")
        placeTitle.text = address
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        networkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver, intentFilter)
        stationsRecyclerView.layoutManager = LinearLayoutManager(this)
        stationsRecyclerView.addItemDecoration(
            ItemOffsetDecorationVertical(
                Utils.convertDpToPixel(5).roundToInt()
            )
        )
        val realm = Realm.getDefaultInstance()
        stationAdapter = StationAdapter(this)
        stationAdapter?.address = address
        if (Utils.isNetworkConnected(this))
            LoginHelper.getAccessToken(this)?.let {
                BackendApi.getInstance().getBoards(it, placeId!!) { boards ->
                    realm.executeTransaction {
                        realm.copyToRealmOrUpdate(boards!!)
                    }
                    stationAdapter?.boards = boards
                    stationsRecyclerView.adapter = stationAdapter
                }
            }
        else{
            networkStatusText.text = getString(R.string.offline)
            networkStatusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            networkStatusImage.setImageResource(R.drawable.oval_red)
            val boards = ArrayList<Board>()
            boards.addAll(
                realm.copyFromRealm(
                    realm.where(Board::class.java).equalTo("place", placeId).findAll()
                )
            )
            stationAdapter?.boards = boards
            stationsRecyclerView.adapter = stationAdapter
        }
        calendarView.setOnClickListener {
           filterBoardsByDate(placeId)
        }
        backArrow.setOnClickListener {
            finish()
        }
    }

    private fun filterBoardsByDate(placeId: String?){
        val builder: MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()
        val calendarConstraints = CalendarConstraints.Builder()
        calendarConstraints.setStart(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30)
            .setEnd(System.currentTimeMillis())
        val bounds = calendarConstraints.build()
        builder.setCalendarConstraints(bounds)
        val picker: MaterialDatePicker<Pair<Long, Long>> = builder.build()
        picker.addOnPositiveButtonClickListener {
            val startOfRange = it.first
            val endOfRange = it.second
            val startDate = Date(startOfRange!!)
            val endDate = Date(endOfRange!!)
            val dateFormat = SimpleDateFormat("yyyy-dd-mm", Locale.getDefault())
            val startDateFormat = dateFormat.format(startDate)
            val endDateFormat = dateFormat.format(endDate)
            BackendApi.getInstance().getBoardsByDate(
                LoginHelper.getAccessToken(this)!!,
                placeId!!, startDateFormat, endDateFormat
            ) { boards ->
                stationAdapter?.boards = boards
                stationAdapter?.notifyDataSetChanged()
            }
        }
        picker.show(supportFragmentManager, picker.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
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
                    networkStatusText.setTextColor(ContextCompat.getColor(this@StationsActivity, android.R.color.holo_red_dark))
                    networkStatusImage.setImageResource(R.drawable.oval_red)
                    isFirstTime = false
                }
                else{
                    if(!isFirstTime){
                        networkStatusText.text = getString(R.string.online)
                        networkStatusText.setTextColor(ContextCompat.getColor(this@StationsActivity, android.R.color.holo_green_dark))
                        networkStatusImage.setImageResource(R.drawable.oval_green)
                        val realm = Realm.getDefaultInstance()
                        val placeId = getIntent().getStringExtra("placeId")
                        LoginHelper.getAccessToken(this@StationsActivity)?.let {
                            BackendApi.getInstance().getBoards(it, placeId!!) { boards ->
                                realm.executeTransaction {
                                    realm.copyToRealmOrUpdate(boards!!)
                                }
                                stationAdapter?.boards = boards
                                stationAdapter?.notifyDataSetChanged()
                            }
                        }
                    }
                    else
                        isFirstTime = false
                }
            }
        }
    }

}






