/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.bluetooth.BluetoothDevice
 *  android.bluetooth.BluetoothSocket
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.util.Log
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.reflect.Method
 *  java.util.UUID
 */
package com.harlharjj.softhub;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothHelper {
    private static final String TAG = "BluetoothHelper";

    static void closeSocket(BluetoothSocket bluetoothSocket) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
                return;
            }
            catch (IOException iOException) {
                Log.e((String)TAG, (String)"Could not close bluetooth socket");
                return;
            }
        }
        Log.e((String)TAG, (String)"Bluetooth socket not retrieved");
    }

    static BluetoothSocket createBluetoothSocket(BluetoothDevice bluetoothDevice, UUID uUID) throws IOException {
        try {
            bluetoothDevice.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
            Class class_ = bluetoothDevice.getClass();
            Class[] arrclass = new Class[]{Integer.TYPE};
            Method method = class_.getMethod("createRfcommSocket", arrclass);
            Object[] arrobject = new Object[]{1};
            BluetoothSocket bluetoothSocket = (BluetoothSocket)method.invoke((Object)bluetoothDevice, arrobject);
            return bluetoothSocket;
        }
        catch (Exception exception) {
            Log.d((String)TAG, (String)"Exception while creating socket", (Throwable)exception);
        }
        return bluetoothDevice.createRfcommSocketToServiceRecord(uUID);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static boolean resetConnection(InputStream inputStream, OutputStream outputStream, BluetoothSocket bluetoothSocket) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bluetoothSocket == null)
            return true;
        try {
            bluetoothSocket.close();
            return true;
        }
        catch (Exception exception) {
            Log.e((String)TAG, (String)"Could not reset connection");
            return false;
        }
    }

    static void sendData(OutputStream outputStream, String string) {
        if (outputStream != null) {
            byte[] arrby = string.getBytes();
            try {
                outputStream.write(arrby);
                return;
            }
            catch (IOException iOException) {
                Log.d((String)TAG, (String)"Could not write to bluetooth outputstream");
                return;
            }
        }
        Log.e((String)TAG, (String)"No output stream is available.");
    }

    static void receiveData(InputStream inputStream, String string){
        string = "";
        if (inputStream != null){
            byte[] inBuffer = new byte[1024];
            int bytesRead = 0;
            try {
                bytesRead = inputStream.read(inBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Convert read bytes into a string
            try {
                string = new String(inBuffer, "ASCII");
                return;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            string = string.substring(0, bytesRead);

        }
    }
}

