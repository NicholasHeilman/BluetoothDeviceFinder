package com.e.bluetoothdevices

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var btAdapter: BluetoothAdapter? = null
    var deviceList = ArrayList<String>()
    lateinit var deviceAdapter: ArrayAdapter<String>


    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            var action = intent?.action
//            Log.i("ACTION: ", action)

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
//                Log.i("DISCOVERY FINISHED", action)
                tv_Status.text = getString(R.string.found)
                bt_Search.isEnabled = true
            } else if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? =
                    intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val name = device?.name
                val address = device?.address
                val type = device?.type
                val rssi = intent?.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE).toString()
//                Log.i("DEVICE FOUND", "NAME: $name ADDRESS: $address RSSI: $rssi TYPE: $type")
                var deviceString = ""
                deviceString = if(name == null || name == ""){
                    "$address RSSI: $rssi dBm"
                } else {
                    "$name RSSI: $rssi dBm"
                }
                if(!deviceList.contains(deviceString)){
                    deviceList.add(deviceString)
                }
                deviceAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 44)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        deviceAdapter =  ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)


        lv_device_list.adapter = deviceAdapter

            val btFilter = IntentFilter()
            btFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            btFilter.addAction(BluetoothDevice.ACTION_FOUND)
            btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            registerReceiver(broadCastReceiver, btFilter)
        }// end onCreate

        fun searchClicked(view: View) {
//            Log.i("START DISCOVERY", intent.action)
            tv_Status.setText(R.string.searching)
            bt_Search.isEnabled = false
            deviceList.clear()
            btAdapter?.startDiscovery()
        }// end searchClicked

}// end MainActivity