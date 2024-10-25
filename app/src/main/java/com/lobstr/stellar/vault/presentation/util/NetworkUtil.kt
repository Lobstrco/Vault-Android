package com.lobstr.stellar.vault.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_VPN
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build

object NetworkUtil {
    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo?.isConnected == true
            } else {
                val network = connectivityManager.activeNetwork

                if (network != null) {
                    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                    return if (networkCapabilities != null) networkCapabilities.hasTransport(TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(TRANSPORT_WIFI) || networkCapabilities.hasTransport(TRANSPORT_VPN) else false
                }
            }
        }
        return false
    }
}
