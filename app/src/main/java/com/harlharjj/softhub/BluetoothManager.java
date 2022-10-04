/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.content.Context
 *  android.support.annotation.AnyThread
 *  android.support.annotation.WorkerThread
 *  android.util.Log
 *  java.lang.Class
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 */
package com.harlharjj.softhub;

import android.content.Context;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.WorkerThread;

public abstract class BluetoothManager
        implements BluetoothDisconnectedListener {
    private static BluetoothManager INSTANCE;
    private static final String TAG = "BluetoothManager";

    public static BluetoothManager getInstance() {
        BluetoothManager bluetoothManager = INSTANCE;
        if (bluetoothManager != null) {
            return bluetoothManager;
        }
        throw new IllegalStateException("BluetoothManager has not been initialized");
    }

    public static void initialize(Context context) {
        Class<BluetoothManager> class_ = BluetoothManager.class;
        synchronized (BluetoothManager.class) {
            Log.d((String)TAG, (String)"Initializing BluetoothManager");
            if (INSTANCE != null) {
                Log.e((String)TAG, (String)"BluetoothManager already instantiated");

                return;
            }
            INSTANCE = new BluetoothManagerImpl(context);

        }
    }

    public abstract void addDisconnectionListener(BluetoothDisconnectedListener var1);

    public abstract void addIncomingDataListener(IncomingDataListener var1);

    @WorkerThread
    public abstract boolean connectTo(String var1);

    public abstract String getConnectedDeviceName();

    public abstract boolean isBluetoothEnabled();

    public abstract boolean isConnected();

    public abstract void removeDisconnectionListener(BluetoothDisconnectedListener var1);

    public abstract void removeIncomingDataListener(IncomingDataListener var1);

    @AnyThread
    public abstract boolean resetConnection();

    @AnyThread
    public abstract void sendData(String var1);
}

