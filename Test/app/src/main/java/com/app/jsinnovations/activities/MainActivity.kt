package com.app.jsinnovations.activities

import android.Manifest
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.app.jsinnovations.R
import com.app.jsinnovations.api.BackendApi
import com.app.jsinnovations.models.User
import com.app.jsinnovations.sharing.Constant
import com.app.jsinnovations.sharing.LoginHelper
import com.app.jsinnovations.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Callback
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.realm.Realm
import okhttp3.OkHttpClient


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.general, R.id.account, R.id.language,
                R.id.contact_us
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1
        )
        val headerLayout = navView.getHeaderView(0)
        val profilePhoto = headerLayout.findViewById<ImageView>(R.id.profilePhoto)
        val name = headerLayout.findViewById<TextView>(R.id.name)
        loadImage(profilePhoto)
        if (Utils.isNetworkConnected(this))
            BackendApi.getInstance().getUser(LoginHelper.getAccessToken(this)!!) {
                val user = it
                name.text = getString(R.string.name, user.name, user.surname)
            }
        else {
            val realm = Realm.getDefaultInstance()
            val user = realm.where(User::class.java).findFirst()!!
            name.text = getString(R.string.name, user.name, user.surname)
        }
    }

    private fun loadImage(profilePhoto: ImageView){
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val accessToken = LoginHelper.getAccessToken(this@MainActivity)!!
                request.addHeader("Authorization", "Bearer $accessToken")
                chain.proceed(request.build())
            }
            .build()
        val picasso = Picasso.Builder(this@MainActivity)
            .downloader(OkHttp3Downloader(client))
            .build()
        picasso.load(Constant.BASE_URL + "/api/images")
            .into(profilePhoto, object: Callback{
                override fun onSuccess() {
                    println()
                }

                override fun onError(e: java.lang.Exception?) {
                    println()
                }

            })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
