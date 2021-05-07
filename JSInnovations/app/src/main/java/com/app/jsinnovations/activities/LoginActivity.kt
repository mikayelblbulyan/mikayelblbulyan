package com.app.jsinnovations.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.androidadvance.topsnackbar.TSnackbar
import com.app.jsinnovations.R
import com.app.jsinnovations.api.BackendApi
import com.app.jsinnovations.sharing.LoginHelper
import com.app.jsinnovations.utils.Utils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //BackendApi.getInstance().images(LoginHelper.getAccessToken(this)!!)
        //BackendApi.getInstance().register("", "")
        //BackendApi.getInstance().getBoard()
        //BackendApi.getInstance().getPlace(LoginHelper.getAccessToken(this)!!, places[0]!!)
        //BackendApi.getInstance().addPlace(LoginHelper.getAccessToken(this)!!, id!!)
        //BackendApi.getInstance().addUser(LoginHelper.getAccessToken(this)!!, "a", "b", "c", "d@d.d", "123456")
        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                email = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                password = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        next.setOnClickListener {
            //if(::email.isInitialized && ::password.isInitialized && Utils.isEmailValid(email))
            if (Utils.isNetworkConnected(this))
                BackendApi.getInstance().auth("", "") {
                    if (it != null) {
                        LoginHelper.saveAuthInformation(this, it)
                        BackendApi.getInstance().getUser(it) {
                            startActivity(Intent(this, MainActivity::class.java))
                        }

                    }
                }
            else
                showNetworkWarning()
        }
    }


    private fun showNetworkWarning() {
        networkWarning.visibility = View.VISIBLE
        GlobalScope.launch {
            delay(3000)
            runOnUiThread {
                networkWarning.visibility = View.GONE
            }
        }
//        val snackBar = TSnackbar.make(
//            findViewById(android.R.id.content),
//            getString(R.string.network_connection),
//            TSnackbar.LENGTH_LONG
//        )
//        val snackBarView = snackBar.view
//        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
//        val error =
//            snackBarView.findViewById<TextView>(R.id.snackbar_text)
//        error.setTextColor(ContextCompat.getColor(this, android.R.color.white))
//        error.gravity = Gravity.CENTER
//        snackBar.show()
    }

}
