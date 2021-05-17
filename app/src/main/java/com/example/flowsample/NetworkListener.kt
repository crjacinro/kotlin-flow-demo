package com.example.flowsample

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Suppress("EXPERIMENTAL_API_USAGE")
class NetworkListener(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    /**
     * Creates a flow of the number of disconnected events occurred
     *
     * @return a flow that will emit the number when collected
     */
    fun subscribeToNetworkFlow(): Flow<Int> = callbackFlow {
        var networkDisconnectedCounter = 0

        val networkEvents = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (isNetworkNotAvailable()) {
                    offer(++networkDisconnectedCounter)         // similar to 'emit()' in the context of callbackFlow {}
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                if (isNetworkNotAvailable()) {
                    offer(++networkDisconnectedCounter)         // similar to 'emit()' in the context of callbackFlow {}
                }
            }
        }

        if (isNetworkNotAvailable()) {
            offer(++networkDisconnectedCounter)                 // similar to 'emit()' in the context of callbackFlow {}
        }

        // Register the callback
        connectivityManager?.registerDefaultNetworkCallback(networkEvents)

        // Deregister the callback
        // This will be invoked when flow scope is cancelled / ended
        awaitClose { connectivityManager?.unregisterNetworkCallback(networkEvents) }
    }




    /**
     *  Utility functions to ensure network connectivity
     *
     * @return true if network is connected. Otherwise, false
     */
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager?.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    private fun isNetworkNotAvailable() = !isNetworkAvailable()
}