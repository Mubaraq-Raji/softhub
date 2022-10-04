/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.util.Log
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 */
package com.harlharjj.softhub;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;

import androidx.core.content.ContextCompat;

public class IncomingDataRunnable
        implements Runnable {
    private final String TAG = "IncomingDataRunnable";
    private final byte delimiter = (byte)10;
    private final InputStream mInputStream;
    private final IncomingDataListener mListener;
    private final byte[] readBuffer = new byte[1024];
    private int readBufferPosition = 0;

    IncomingDataRunnable(InputStream inputStream, IncomingDataListener incomingDataListener) {
        this.mInputStream = inputStream;
        this.mListener = incomingDataListener;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void run() {
        block2 : while (!Thread.currentThread().isInterrupted()) {
            try {
                if (this.mInputStream == null) {
                    Log.e((String)"IncomingDataRunnable", (String)"Interrupting IncomingDataRunnable,inputstream=null");
                    Thread.currentThread().interrupt();
                    return;
                }
                int n = this.mInputStream.available();
                if (n <= 0) continue;
                byte[] arrby = new byte[n];
                this.mInputStream.read(arrby);
                int n2 = 0;
                do {
                    if (n2 >= n) continue block2;
                    int n3 = arrby[n2];
                    if (this.readBufferPosition == 1023) {
                        n3 = 10;
                    }
                    if (n3 == 10) {
                        byte[] arrby2 = new byte[this.readBufferPosition];
                        System.arraycopy((Object)this.readBuffer, (int)0, (Object)arrby2, (int)0, (int)arrby2.length);
                        String string = new String(arrby2, "US-ASCII");
                        this.readBufferPosition = 0;
                        this.mListener.onDataReceived(string);
                    } else {
                        byte[] arrby3 = this.readBuffer;
                        int n4 = this.readBufferPosition;
                        this.readBufferPosition = n4 + 1;
                        arrby3[n4] = (byte) n3;
                    }
                    ++n2;
                } while (true);
            }
            catch (IOException iOException) {
                Log.e((String)"IncomingDataRunnable", (String)"IOException while reading bluetooth incoming data", (Throwable)iOException);
                continue;
            }
        }
        return;
    }
}

