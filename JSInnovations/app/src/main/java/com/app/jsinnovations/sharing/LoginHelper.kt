package com.app.jsinnovations.sharing

import android.content.Context

object LoginHelper {

    fun saveAuthInformation(context: Context, accessToken: String) {
        val prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE).edit()
        prefs.putString("accessToken", accessToken)
        prefs.apply()
    }

    fun deleteAuthInformation(context: Context) {
        val prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE).edit()
        prefs.putString("accessToken", null)
        prefs.apply()
    }

    fun getAccessToken(context: Context): String? {
        val prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }

    fun getRefreshToken(context: Context): String {
        val prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE)
        return prefs.getString("refreshToken", "")!!
    }

}