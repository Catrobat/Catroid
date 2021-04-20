/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.bluetooth

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity
import org.catrobat.catroid.bluetooth.base.BluetoothConnection
import org.catrobat.catroid.bluetooth.base.BluetoothConnectionFactory
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceFactory
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.common.ServiceProvider
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import org.catrobat.catroid.utils.ToastUtil
import java.util.ArrayList

class ConnectBluetoothDeviceActivity : AppCompatActivity() {
    protected var btDevice: BluetoothDevice? = null
    private var btManager: BluetoothManager? = null
    private var pairedDevicesArrayAdapter: ArrayAdapter<String>? = null
    private var newDevicesArrayAdapter: ArrayAdapter<Pair<*,*>>? = null
    fun addPairedDevice(pairedDevice: String?) {
        if (pairedDevicesArrayAdapter != null) {
            pairedDevicesArrayAdapter!!.add(pairedDevice)
        }
    }

    // end hooks for testing
    private val deviceClickListener: AdapterView.OnItemClickListener =
        object : AdapterView.OnItemClickListener {
            private fun getSelectedBluetoothAddress(view: View): String? {
                val info = (view as TextView).text.toString()
                return if (info.lastIndexOf('-') != info.length - DEVICE_MAC_ADDRESS_LENGTH) {
                    null
                } else info.substring(info.lastIndexOf('-') + 1)
            }

            override fun onItemClick(av: AdapterView<*>?, view: View, position: Int, id: Long) {
                val address = getSelectedBluetoothAddress(view)
                var pair: Pair<*, *>? = null
                if (!newDevicesArrayAdapter!!.isEmpty) {
                    pair = newDevicesArrayAdapter!!.getItem(position)
                }
                if (address == null) {
                    return
                }
                if (pair == null || pair.second == android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                    connectDevice(address)
                }
            }
        }
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (android.bluetooth.BluetoothDevice.ACTION_FOUND == action) {
                val device =
                    intent.getParcelableExtra<android.bluetooth.BluetoothDevice>(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)
                if (device.bondState != android.bluetooth.BluetoothDevice.BOND_BONDED) {
                    val deviceInfo = device.name + "-" + device.address
                    if (device.type == android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC || device.type == android.bluetooth.BluetoothDevice.DEVICE_TYPE_DUAL) {
                        val listElement = Pair(
                            deviceInfo,
                            android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC
                        )
                        if (newDevicesArrayAdapter!!.getPosition(listElement) < 0) {
                            newDevicesArrayAdapter!!.add(listElement)
                        }
                    }
                    if (device.type == android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE || device.type == android.bluetooth.BluetoothDevice.DEVICE_TYPE_DUAL) {
                        val deviceInfoBLE = "BLE - $deviceInfo"
                        val listElement =
                            Pair(deviceInfoBLE, android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE)
                        if (newDevicesArrayAdapter!!.getPosition(listElement) < 0) {
                            newDevicesArrayAdapter!!.add(listElement)
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                setProgressBarIndeterminateVisibility(false)
                findViewById<View>(R.id.device_list_progress_bar).visibility = View.GONE
                title = getString(R.string.select_device) + " " + btDevice!!.name
                if (newDevicesArrayAdapter!!.isEmpty) {
                    val noDevices = resources.getString(R.string.none_found)
                    val listElement = Pair(noDevices, 0)
                    newDevicesArrayAdapter!!.add(listElement)
                }
            }
        }
    }

    private open inner class ConnectDeviceTask : AsyncTask<String?, Void?, BluetoothConnection.State>
        () {
        var btConnection: BluetoothConnection? = null
        private var connectingProgressDialog: ProgressDialog? = null
        override fun onPreExecute() {
            setVisible(false)
            connectingProgressDialog = ProgressDialog.show(
                this@ConnectBluetoothDeviceActivity, "",
                resources.getString(R.string.connecting_please_wait), true
            )
        }

        override fun doInBackground(vararg addresses: String?): BluetoothConnection.State? {
            if (btDevice == null) {
                Log.e(TAG, "Try connect to device which is not implemented!")
                return BluetoothConnection.State.NOT_CONNECTED
            }
            btConnection = connectionFactory!!.createBTConnectionForDevice(
                btDevice!!.deviceType,
                addresses[0],
                btDevice!!.bluetoothDeviceUUID,
                this@ConnectBluetoothDeviceActivity.applicationContext
            )
            return btConnection!!.connect()
        }

        override fun onPostExecute(connectionState: BluetoothConnection.State) {
            connectingProgressDialog!!.dismiss()
            var result = RESULT_CANCELED
            if (connectionState == BluetoothConnection.State.CONNECTED) {
                btDevice!!.setConnection(btConnection)
                result = RESULT_OK
                val btDeviceService =
                    ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
                try {
                    btDeviceService.deviceConnected(btDevice)
                } catch (e: MindstormsException) {
                    ToastUtil.showError(
                        this@ConnectBluetoothDeviceActivity,
                        R.string.bt_connection_failed
                    )
                }
            } else {
                ToastUtil.showError(
                    this@ConnectBluetoothDeviceActivity,
                    R.string.bt_connection_failed
                )
            }
            setResult(result)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createAndSetDeviceService()
        setContentView(R.layout.device_list)
        title = getString(R.string.select_device) + " " + btDevice!!.name
        setResult(RESULT_CANCELED)
        val scanButton = findViewById<View>(R.id.button_scan) as Button
        scanButton.setOnClickListener { view ->
            doDiscovery()
            view.visibility = View.GONE
        }
        pairedDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)
        newDevicesArrayAdapter = object : ArrayAdapter<Pair<*,*>>(
            this, R.layout.device_name,
            ArrayList<Pair<*,*>>()
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.text = getItem(position)!!.first as String
                return view
            }
        }
        val pairedListView = findViewById<View>(R.id.paired_devices) as ListView
        pairedListView.adapter = pairedDevicesArrayAdapter
        pairedListView.onItemClickListener = deviceClickListener
        val newDevicesListView = findViewById<View>(R.id.new_devices) as ListView
        newDevicesListView.adapter = newDevicesArrayAdapter
        newDevicesListView.onItemClickListener = deviceClickListener
        var filter = IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(receiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(receiver, filter)
        val bluetoothState = activateBluetooth()
        if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
            listAndSelectDevices()
        }
    }

    private fun listAndSelectDevices() {
        val pairedDevices = btManager!!.bluetoothAdapter!!.bondedDevices
        if (pairedDevices.size > 0) {
            findViewById<View>(R.id.title_paired_devices).visibility = View.VISIBLE
            for (device in pairedDevices) {
                pairedDevicesArrayAdapter!!.add(device.name + "-" + device.address)
            }
        }
        if (pairedDevices.size == 0) {
            val noDevices = resources.getText(R.string.none_paired).toString()
            pairedDevicesArrayAdapter!!.add(noDevices)
        }
        setVisible(true)
    }

    protected fun createAndSetDeviceService() {
        val serviceType = intent.getSerializableExtra(DEVICE_TO_CONNECT) as Class<BluetoothDevice>
        btDevice = deviceFactory!!.createDevice(serviceType, this.applicationContext)
    }

    private fun connectDevice(address: String) {
        btManager!!.bluetoothAdapter!!.cancelDiscovery()
        ConnectDeviceTask().execute(address)
    }

    override fun onDestroy() {
        if (btManager != null && btManager!!.bluetoothAdapter != null) {
            btManager!!.bluetoothAdapter!!.cancelDiscovery()
        }
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun doDiscovery() {
        setProgressBarIndeterminateVisibility(true)
        findViewById<View>(R.id.title_new_devices).visibility = View.VISIBLE
        findViewById<View>(R.id.device_list_progress_bar).visibility =
            View.VISIBLE
        if (btManager!!.bluetoothAdapter!!.isDiscovering) {
            btManager!!.bluetoothAdapter!!.cancelDiscovery()
        }
        btManager!!.bluetoothAdapter!!.startDiscovery()
    }

    private fun activateBluetooth(): Int {
        btManager = BluetoothManager(this)
        val bluetoothState = btManager!!.activateBluetooth()
        if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {
            ToastUtil.showError(this, R.string.notification_blueth_err)
            setResult(RESULT_CANCELED)
            finish()
        }
        return bluetoothState
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "Bluetooth activation activity returned")
        when (resultCode) {
            RESULT_OK -> listAndSelectDevices()
            RESULT_CANCELED -> {
                ToastUtil.showError(this, R.string.notification_blueth_err)
                setResult(RESULT_CANCELED)
                finish()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)

        }
    }

    companion object {
        val TAG = ConnectBluetoothDeviceActivity::class.java.simpleName
        const val DEVICE_TO_CONNECT = "org.catrobat.catroid.bluetooth.DEVICE"
        private const val DEVICE_MAC_ADDRESS_LENGTH = 18
        private var btDeviceFactory: BluetoothDeviceFactory? = null
        private var btConnectionFactory: BluetoothConnectionFactory? = null

        // hooks for testing
        @JvmStatic
		private var deviceFactory: BluetoothDeviceFactory?
            get() {
                if (btDeviceFactory == null) {
                    btDeviceFactory = BluetoothDeviceFactoryImpl()
                }
                return btDeviceFactory
            }
            set(deviceFactory) {
                btDeviceFactory = deviceFactory
            }
        @JvmStatic
		private var connectionFactory: BluetoothConnectionFactory?
            get() {
                if (btConnectionFactory == null) {
                    btConnectionFactory = BluetoothConnectionFactoryImpl()
                }
                return btConnectionFactory
            }
            set(connectionFactory) {
                btConnectionFactory = connectionFactory
            }
    }
}