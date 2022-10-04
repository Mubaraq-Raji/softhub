package com.harlharjj.softhub;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeviceListDialog extends AppCompatDialogFragment {
    public static final String TAG = "DeviceListDialog";
    private BluetoothAdapter mBtAdapter;
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @SuppressLint("MissingPermission")
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            String str = DeviceListDialog.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Device selected for position ");
            sb.append(i);
            Log.d(str, sb.toString());
            if (DeviceListDialog.this.mDeviceSelectedListener == null) {
                Log.e(DeviceListDialog.TAG, "No device selected listener, impossible use case");
            } else if (DeviceListDialog.this.mPairedDevices == null || DeviceListDialog.this.mPairedDevices.size() <= 0) {
                Log.e(DeviceListDialog.TAG, "No paired devices in cache");
            } else {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) DeviceListDialog.this.mPairedDevices.get(i);
                if (bluetoothDevice != null) {
                    String str2 = DeviceListDialog.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Selected device ");
                    sb2.append(bluetoothDevice.getAddress());
                    sb2.append("| Address ");
                    sb2.append(bluetoothDevice.getName());
                    Log.d(str2, sb2.toString());
                    DeviceListDialog.this.mDeviceSelectedListener.onDeviceSelected(bluetoothDevice.getAddress(), bluetoothDevice.getName());
                    DeviceListDialog.this.dismiss();
                    return;
                }
                Log.e(DeviceListDialog.TAG, "Selected device not found in cache");
            }
        }
    };
    /* access modifiers changed from: private */
    public DeviceSelectedListener mDeviceSelectedListener;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    /* access modifiers changed from: private */
    public SparseArray<BluetoothDevice> mPairedDevices = new SparseArray<>();

    public interface DeviceSelectedListener {
        void onDeviceSelected(String str, String str2);

        void onDismiss();
    }

    public DeviceListDialog() {
    }

    public DeviceListDialog(DeviceSelectedListener deviceSelectedListener) {
        this.mDeviceSelectedListener = deviceSelectedListener;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    @SuppressLint("MissingPermission")
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int i = 0;
        View inflate = layoutInflater.inflate(R.layout.dialog_device_list, viewGroup, false);
        getDialog().setTitle("Select your device");
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.device_name);
        this.mNewDevicesArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.device_name);
        ListView listView = (ListView) inflate.findViewById(R.id.paired_devices);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this.mDeviceClickListener);
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        @SuppressLint("MissingPermission") Set<BluetoothDevice> bondedDevices = this.mBtAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                StringBuilder sb = new StringBuilder();
                sb.append(bluetoothDevice.getName());
                sb.append("\n");
                sb.append(bluetoothDevice.getAddress());
                arrayAdapter.add(sb.toString());
                this.mPairedDevices.put(i, bluetoothDevice);
                i++;
            }
        } else {
            arrayAdapter.add(getResources().getText(R.string.none_paired).toString());
        }
        return inflate;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        DeviceSelectedListener deviceSelectedListener = this.mDeviceSelectedListener;
        if (deviceSelectedListener != null) {
            deviceSelectedListener.onDismiss();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}
