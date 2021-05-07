package com.app.jsinnovations.sharing

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration



class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config =
            RealmConfiguration.Builder().name("mydb.realm").schemaVersion(1)
                .build()
        Realm.setDefaultConfiguration(config)
        Realm.getInstance(config)
    }

    override fun onTerminate() {
        Realm.getDefaultInstance().close()
        super.onTerminate()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}