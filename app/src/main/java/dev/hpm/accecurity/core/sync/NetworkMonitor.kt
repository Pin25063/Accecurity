package dev.hpm.accecurity.core.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Se mantiene durante la vida del proceso de la aplicación. */
class NetworkMonitor(context: Context) {
    private val manager = context.applicationContext.getSystemService(ConnectivityManager::class.java)
    private val state = MutableStateFlow(hasInternet())
    val online = state.asStateFlow()

    fun hasInternet(): Boolean {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    init {
        manager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { state.value = hasInternet() }
            override fun onLost(network: Network) { state.value = hasInternet() }
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                state.value = hasInternet()
            }
        })
    }
}
