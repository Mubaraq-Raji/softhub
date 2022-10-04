package com.harlharjj.softhub;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceList extends AppCompatActivity {
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_NAME = "device_name";

    private BluetoothAdapter mBtAdapter;
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @SuppressLint("MissingPermission")
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            DeviceList.this.mBtAdapter.cancelDiscovery();
            String charSequence = ((TextView) view).getText().toString();
            Intent intent = new Intent();
            intent.putExtra(DeviceList.EXTRA_DEVICE_ADDRESS, charSequence);
            DeviceList.this.setResult(-1, intent);
            DeviceList.this.finish();
        }
    };

    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (bluetoothDevice.getBondState() != 12) {
                    ArrayAdapter access$100 = DeviceList.this.mNewDevicesArrayAdapter;
                    StringBuilder sb = new StringBuilder();
                    sb.append(bluetoothDevice.getName());
                    sb.append("\n");
                    sb.append(bluetoothDevice.getAddress());
                    access$100.add(sb.toString());
                }
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                DeviceList.this.setProgressBarIndeterminateVisibility(false);
                if (DeviceList.this.mNewDevicesArrayAdapter.getCount() == 0) {
                    DeviceList.this.mNewDevicesArrayAdapter.add(DeviceList.this.getResources().getText(R.string.none_found).toString());
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(5);
        setContentView((int) R.layout.activity_device_list);
        setResult(0);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.device_name);
        this.mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        ListView listView = (ListView) findViewById(R.id.paired_devices);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this.mDeviceClickListener);
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED"));
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBtAdapter.startDiscovery();
        Set<BluetoothDevice> bondedDevices = this.mBtAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                StringBuilder sb = new StringBuilder();
                sb.append(bluetoothDevice.getName());
                sb.append("\n");
                sb.append(bluetoothDevice.getAddress());
                arrayAdapter.add(sb.toString());
            }
            return;
        }
        arrayAdapter.add(getResources().getText(R.string.none_paired).toString());
    }

    @SuppressLint("MissingPermission")
    protected void onDestroy() {
        super.onDestroy();
        BluetoothAdapter bluetoothAdapter = this.mBtAdapter;
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(this.mReceiver);
    }
}
