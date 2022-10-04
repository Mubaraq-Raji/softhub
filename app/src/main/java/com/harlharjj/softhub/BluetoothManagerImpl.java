/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.bluetooth.BluetoothAdapter
 *  android.bluetooth.BluetoothDevice
 *  android.bluetooth.BluetoothSocket
 *  android.content.Context
 *  android.media.Ringtone
 *  android.media.RingtoneManager
 *  android.net.Uri
 *  android.os.Handler
 *  android.os.Handler$Callback
 *  android.os.HandlerThread
 *  android.os.Looper
 *  android.os.Message
 *  android.util.Log
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Thread
 *  java.util.Collections
 *  java.util.HashSet
 *  java.util.Iterator
 *  java.util.Set
 *  java.util.UUID
 */
package com.harlharjj.softhub;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import androidx.core.app.ActivityCompat;

public class BluetoothManagerImpl
        extends BluetoothManager
        implements Handler.Callback,
        IncomingDataListener {
    private static final UUID mUUID = UUID.fromString((String) "00001101-0000-1000-8000-00805F9B34FB");
    private final boolean DEBUG = true;
    private final int MSG_BLUETOOTH_DISCONNECTED = 1;
    private final int MSG_SEND_DATA = 0;
    private final String TAG = "BluetoothManagerImpl";
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private Set<BluetoothDisconnectedListener> disconnectedListeners = Collections.synchronizedSet((Set) new HashSet());
    private InputStream inStream;
    private Set<IncomingDataListener> incomingDataListeners = Collections.synchronizedSet((Set) new HashSet());
    private boolean isConnectedToDevice;
    private BluetoothDevice mBluetoothDevice = null;
    private final Context mContext;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private Thread mIncomingDataWorkerThread;
    private Uri notificationUri = null;
    private OutputStream outStream;
    private Ringtone ringtone;
    public final boolean showToast = false;

    BluetoothManagerImpl(Context context) {
        this.mContext = context;
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mHandlerThread = new HandlerThread("BluetoothManagerImpl");
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper(), (Handler.Callback) this);
    }

    private void notifyBluetoothDisconnected() {
        Log.d((String) "BluetoothManagerImpl", (String) "notifyBluetoothDisconnected");
        for (BluetoothDisconnectedListener disconnectedListener : this.disconnectedListeners) {
            disconnectedListener.onBluetoothDisconnected();
        }
    }

    private void notifyIncomingData(String string) {
        for (IncomingDataListener incomingDataListener : this.incomingDataListeners) {
            incomingDataListener.onDataReceived(string);
        }
    }

    private void resetIncomingDataWorker() {
        Log.d((String) "BluetoothManagerImpl", (String) "resetIncomingDataWorker");
        Thread thread = this.mIncomingDataWorkerThread;
        if (thread != null) {
            thread.interrupt();
            this.mIncomingDataWorkerThread = null;
        }
    }

    @Override
    public void addDisconnectionListener(BluetoothDisconnectedListener bluetoothDisconnectedListener) {
        Log.d((String) "BluetoothManagerImpl", (String) "addDisconnectionListener");
        if (bluetoothDisconnectedListener == null) {
            return;
        }
        this.disconnectedListeners.add((BluetoothDisconnectedListener) bluetoothDisconnectedListener);
    }

    @Override
    public void addIncomingDataListener(IncomingDataListener incomingDataListener) {
        Thread thread;
        Log.d((String) "BluetoothManagerImpl", (String) "addIncomingDataListener");
        if (incomingDataListener == null) {
            return;
        }
        if (this.inStream == null) {
            Log.d((String) "BluetoothManagerImpl", (String) "InputStream is null, ignoring adding listener");
        }
        if ((thread = this.mIncomingDataWorkerThread) == null) {
            Log.d((String) "BluetoothManagerImpl", (String) "Instantiating Incoming data thread and starting it.");
            this.mIncomingDataWorkerThread = new Thread((Runnable) new IncomingDataRunnable(this.inStream, this));
            this.mIncomingDataWorkerThread.start();
        } else if (thread.isInterrupted()) {
            Log.d((String) "BluetoothManagerImpl", (String) "Incoming data thread is in interrupted state, now restarting.");
            this.mIncomingDataWorkerThread.start();
        }
        this.incomingDataListeners.add((IncomingDataListener) incomingDataListener);
    }

    @Override
    public boolean connectTo(String string) {
        block3:
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Connecting to ");
            stringBuilder.append(string);
            Log.d((String) "BluetoothManagerImpl", (String) stringBuilder.toString());
            this.notificationUri = RingtoneManager.getDefaultUri((int) 4);
            this.ringtone = RingtoneManager.getRingtone((Context) this.mContext, (Uri) this.notificationUri);
            this.mBluetoothDevice = this.btAdapter.getRemoteDevice(string);
            try {
                this.btSocket = BluetoothHelper.createBluetoothSocket(this.mBluetoothDevice, mUUID);
                Log.d((String) "BluetoothManagerImpl", (String) "bluetooth socket created");
                if (this.btSocket == null) break block3;
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                this.btSocket.connect();
                this.outStream = this.btSocket.getOutputStream();
                this.inStream = this.btSocket.getInputStream();
            } catch (IOException iOException) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unable to connect to device ");
                stringBuilder2.append((Object) iOException);
                Log.e((String) "BluetoothManagerImpl", (String) stringBuilder2.toString());
                BluetoothHelper.closeSocket(this.btSocket);
                this.outStream = null;
                this.inStream = null;
                return false;
            }
            this.btAdapter.cancelDiscovery();
            this.isConnectedToDevice = true;
            return true;
        }
        Log.e((String) "BluetoothManagerImpl", (String) "Could not get bluetooth socket");
        return false;
    }

    @Override
    public String getConnectedDeviceName() {
        BluetoothDevice bluetoothDevice = this.mBluetoothDevice;
        if (bluetoothDevice != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            return bluetoothDevice.getName();
        }
        return null;
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            default: {
                return false;
            }
            case 1: {
                Log.d((String)"BluetoothManagerImpl", (String)"[MSG_BLUETOOTH_DISCONNECTED]");
                this.notifyBluetoothDisconnected();
                this.isConnectedToDevice = false;
                this.mBluetoothDevice = null;
                BluetoothHelper.closeSocket(this.btSocket);
                this.resetIncomingDataWorker();
                return true;
            }
            case 0:
        }
        String string = (String)message.obj;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[MSG_SEND_DATA] Message data=");
        stringBuilder.append(string);
        Log.d((String)"BluetoothManagerImpl", (String)stringBuilder.toString());
        if (this.isConnectedToDevice) {
            BluetoothHelper.sendData(this.outStream, string);

            return true;
        }
        Log.e((String)"BluetoothManagerImpl", (String)"Trying to send data while no device is connected");
        return true;
    }

    @Override
    public boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = this.btAdapter;
        if (bluetoothAdapter == null) {
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    @Override
    public boolean isConnected() {
        return this.isConnectedToDevice;
    }

    @Override
    public void onBluetoothDisconnected() {
        Log.d((String)"BluetoothManagerImpl", (String)"onBluetoothDisconnected");
        this.mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onDataReceived(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onDataReceived ");
        stringBuilder.append(string);
        Log.d((String)"BluetoothManagerImpl", (String)stringBuilder.toString());
        this.notifyIncomingData(string);
    }

    @Override
    public void removeDisconnectionListener(BluetoothDisconnectedListener bluetoothDisconnectedListener) {
        Log.d((String)"BluetoothManagerImpl", (String)"removeDisconnectionListener");
        if (bluetoothDisconnectedListener == null) {
            return;
        }
        this.disconnectedListeners.remove((Object)bluetoothDisconnectedListener);
    }

    @Override
    public void removeIncomingDataListener(IncomingDataListener incomingDataListener) {
        Log.d((String)"BluetoothManagerImpl", (String)"removeIncomingDataListener");
        if (incomingDataListener == null) {
            return;
        }
        this.incomingDataListeners.remove((Object)incomingDataListener);
        if (this.incomingDataListeners.size() == 0) {
            this.resetIncomingDataWorker();
        }
    }

    @Override
    public boolean resetConnection() {
        if (this.isConnectedToDevice) {
            this.notifyBluetoothDisconnected();
        }
        this.isConnectedToDevice = false;
        this.mBluetoothDevice = null;
        this.resetIncomingDataWorker();
        return BluetoothHelper.resetConnection(this.inStream, this.outStream, this.btSocket);
    }

    @Override
    public void sendData(String string) {
        Log.d((String)"BluetoothManagerImpl", (String)"send Data");
        this.mHandler.obtainMessage(0, (Object)string).sendToTarget();
    }


}

