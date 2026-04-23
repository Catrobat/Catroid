/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData

class NetworkConnectionMonitor(private val context: Context) : LiveData<Boolean>() {

    companion object {
        private val TAG = NetworkConnectionMonitor::class.simpleName
    }

    lateinit var callback: ConnectionNetworkCallback

    fun unregisterDefaultNetworkCallback() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(callback)
    }

    fun registerDefaultNetworkCallback() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
        postValue(checkConnection(connectivityManager))
        callback = ConnectionNetworkCallback()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(callback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build()
            connectivityManager.registerNetworkCallback(request, callback)
        }
    }

    private fun checkConnection(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(network)
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH))
    }

    inner class ConnectionNetworkCallback : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
            Log.d(TAG, "onAvailable")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
            Log.d(TAG, "onLost")
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setValueTo(value: Boolean) {
        postValue(value)
    }
}
