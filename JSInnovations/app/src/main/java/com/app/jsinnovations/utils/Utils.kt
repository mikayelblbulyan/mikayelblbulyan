package com.app.jsinnovations.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.webkit.MimeTypeMap
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class Utils {

    companion object{

        fun convertDpToPixel(dp: Int): Float {
            val metrics = Resources.getSystem().displayMetrics
            return dp.toFloat() * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }


        fun encryptThisString(input: String): String {
            try {
                val md = MessageDigest.getInstance("SHA-512")
                val messageDigest = md.digest(input.toByteArray())

                // Convert byte array into signum representation
                val no = BigInteger(1, messageDigest)

                // Convert message digest into hex value
                var hashtext = no.toString(16)

                // Add preceding 0s to make it 32 bit
                while (hashtext.length < 32) {
                    hashtext = "0$hashtext"
                }

                // return the HashText
                return hashtext
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
            // For specifying wrong message digest algorithms
        }

        fun isEmailValid(email: String): Boolean {
            return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun getMimeType(url: String): String {
            var type = ""
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
            }
            return type
        }

        fun getImageSize(imageFile: File): BitmapFactory.Options {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imageFile.toString(), options)
            return options
        }

        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo

                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val n = cm.activeNetwork

                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)

                    return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                }
            }

            return false
        }
    }

}