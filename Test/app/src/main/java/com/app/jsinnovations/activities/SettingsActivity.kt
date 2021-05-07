package com.app.jsinnovations.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.jsinnovations.R
import com.app.jsinnovations.adapters.SettingsAdapter
import com.app.jsinnovations.api.BackendApi
import com.app.jsinnovations.models.Setting
import com.app.jsinnovations.sharing.LoginHelper
import com.app.jsinnovations.utils.Utils
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val boardId = intent.getStringExtra("boardId")
        val moduleId = intent.getStringExtra("moduleId")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val realm = Realm.getDefaultInstance()
        settingsRecyclerView.layoutManager = LinearLayoutManager(this)
        val settingsAdapter = SettingsAdapter(this)
        if (Utils.isNetworkConnected(this))
            LoginHelper.getAccessToken(this)?.let {
                BackendApi.getInstance().settings(boardId!!, moduleId!!, it) { settings ->
                    realm.executeTransaction {
                        realm.copyToRealmOrUpdate(settings)
                    }
                    settingsAdapter.setSettings(settings)
                    settingsRecyclerView.adapter = settingsAdapter
                }
            }
        else{
            val settings = ArrayList<Setting>()
            settings.addAll(realm.copyFromRealm(
                realm.where(Setting::class.java).findAll()
            ))
            settingsAdapter.setSettings(settings)
            settingsRecyclerView.adapter = settingsAdapter
        }
        backArrow.setOnClickListener {
            finish()
        }
    }

}