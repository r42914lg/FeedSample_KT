package com.r42914lg.tutukt.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import com.r42914lg.tutukt.model.TuTuViewModel

class NetworkTracker(private val activity: AppCompatActivity, private val vm: TuTuViewModel) {
    private var isOnline = false

    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (!isOnline) {
                vm.setNetworkStatus(true)
            }
            isOnline = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            if (isOnline) {
                vm.setNetworkStatus(false)
            }
            isOnline = false
        }
    }

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    fun register() {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.requestNetwork(networkRequest, networkCallback)
    }

    fun unregister() {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.unregisterNetworkCallback(networkCallback)
    }
}
