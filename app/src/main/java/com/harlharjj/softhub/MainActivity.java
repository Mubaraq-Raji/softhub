package com.harlharjj.softhub;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.harlharjj.softhub.ui.home.HomeFragment;
import com.harlharjj.softhub.ui.notifications.NotificationsFragment;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.location.LocationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import static android.content.ContentValues.TAG;

public class MainActivity extends EspTouchActivityAbs implements BluetoothDisconnectedListener {

    private static final String DEFAULT_DEVICE_NAME = "none";
    private static final String DEFAULT_MAC_ADDRESS = "none";
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
    private boolean isDeviceListShown;
    private TextView bluetooth;
    private BroadcastReceiver mBluetoothDisconnectedReceiver;
    private BluetoothManager mBluetoothManager;
    private boolean mIsActivityRunning;
    public SharedPreferences sharedPreferences;
    private ProgressBar progressbar;
    private ConstraintLayout contents, progressViews;
    private static final int REQUEST_PERMISSION = 0x01;
    private WifiManager mWifiManager;
    private String mSsid, inetIp;
    private ProgressDialog progressDialog;


    private EsptouchAsyncTask4 mTask;
    private byte[] mSsidBytes;
    private String mBssid;


    String API_key = "0ba8178e8a5d3239ce3d7f74533702d2";
    String CITY = "Gbagada";


    @Override
    public void onBluetoothDisconnected() {
        runOnUiThread(new Runnable() {
            public void run() {
                Utils.showToastMessage(MainActivity.this, "Device disconnected");
                if (MainActivity.this.mIsActivityRunning) {
                    MainActivity.this.showDeviceList();
                }
            }
        });

    }


    private class BluetoothConnectTask extends AsyncTask<Void, Void, Void> {
        private final String mAddress;
        private final String mDeviceName;
        private LoadingDialog mLoadingDialog;

        BluetoothConnectTask(String str, String str2) {
            this.mAddress = str;
            this.mDeviceName = str2;
        }

        public void onPreExecute() {
            this.mLoadingDialog = new LoadingDialog();
            Bundle bundle = new Bundle();
            bundle.putString(LoadingDialog.DEVICE_NAME_KEY, this.mDeviceName);
            this.mLoadingDialog.setArguments(bundle);
            this.mLoadingDialog.show(MainActivity.this.getSupportFragmentManager(), LoadingDialog.TAG);
        }

        protected Void doInBackground(Void... voidArr) {
            MainActivity.this.connectToDevice(this.mAddress, this.mDeviceName);
            return null;
        }

        protected void onPostExecute(Void voidR) {
            LoadingDialog loadingDialog = this.mLoadingDialog;
            if (loadingDialog != null) {

                loadingDialog.dismissAllowingStateLoss();
            }
            MainActivity.this.isDeviceListShown = false;
        }
    }

    @Override
    protected String getEspTouchVersion() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(MainActivity.this);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        progressDialog = new ProgressDialog(this);
       // new weatherTask().execute();

        this.bluetooth = findViewById(R.id.bluetooth);
        BluetoothManager.initialize(getApplicationContext());
        MainActivity.this.mBluetoothManager = BluetoothManager.getInstance();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            //requestPermissions(permissions, REQUEST_PERMISSION);
        }


//        MyApplication.getInstance().observeBroadcast(MainActivity.this, s -> {
//            Log.d(TAG, "onCreate: Broadcast=" + s);
//            onWifiChanged(null);
//        });

        String Mode = SharedPrefsUtil.getStringOrNull(getApplicationContext(), "Mode");
                if (Mode != null) {
                    switch (Mode){

                        case "WiFi":
                            bluetooth.setVisibility(View.GONE);


                            break;
                        case "Bluetooth":
                            bluetooth.setVisibility(View.VISIBLE);

                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
                            registerReceiver(MainActivity.this.mBluetoothDisconnectedReceiver, intentFilter);
                            setupBluetooth();
                            Utils.showToastMessage(getApplicationContext(), Mode);
                            //Utils.showSnackBar(getCurrentFocus(),Mode,Mode, null);
                            break;
                    }
                }



    }


    private void setupBluetooth() {
        if (this.mBluetoothManager.isBluetoothEnabled()) {
            String stringOrDefault = SharedPrefsUtil.getStringOrDefault(getApplicationContext(), SharedPrefsUtil.MAC_ADDRESS_KEY, "none");
            String stringOrDefault2 = SharedPrefsUtil.getStringOrDefault(getApplicationContext(), SharedPrefsUtil.DEVICE_NAME_KEY, "none");
            if (!stringOrDefault.equals("none")) {
                new BluetoothConnectTask(stringOrDefault, stringOrDefault2).execute(new Void[0]);
            } else {
                showDeviceList();
            }
        } else {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
            Toast.makeText(this, "Please enable bluetooth on your device", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void showDeviceList() {
        if (!this.isDeviceListShown) {
            new DeviceListDialog(new DeviceListDialog.DeviceSelectedListener() {
                public void onDeviceSelected(String str, String str2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Selected device ");
                    sb.append(str2);
                    sb.append("| Address ");
                    sb.append(str);
                    Log.d("HomeActivity", sb.toString());
                    new BluetoothConnectTask(str, str2).execute(new Void[0]);
                }

                public void onDismiss() {
                    MainActivity.this.isDeviceListShown = false;
                }
            }).show(getSupportFragmentManager(), DeviceListDialog.TAG);
            this.isDeviceListShown = true;
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 1) {
            if (i == 3 && i2 != -1) {
                finish();
            }
        } else if (i2 == -1) {
            Toast.makeText(this, "Connecting ...", Toast.LENGTH_SHORT).show();
            connectToDevice(intent);
        }
    }

    private void connectToDevice(Intent intent) {
        String string = intent.getExtras().getString(DeviceList.EXTRA_DEVICE_ADDRESS);
        String substring = (string == null || string.length() <= 17) ? "none" : string.substring(string.length() - 17);
        String substring2 = (string == null || string.length() <= 17) ? "none" : string.substring(0, string.length() - 17);
        SharedPrefsUtil.save(getApplicationContext(), SharedPrefsUtil.MAC_ADDRESS_KEY, substring);
        SharedPrefsUtil.save(getApplicationContext(), SharedPrefsUtil.DEVICE_NAME_KEY, substring2);
        boolean connectTo = this.mBluetoothManager.connectTo(substring);
        if (connectTo) {
            StringBuilder sb = new StringBuilder();
            sb.append("Connected to ");
            sb.append(substring2);
            Utils.showToastMessage(this, sb.toString());
        }

    }

    public void connectToDevice(String str, final String str2) {
        SharedPrefsUtil.save(getApplicationContext(), SharedPrefsUtil.MAC_ADDRESS_KEY, str);
        SharedPrefsUtil.save(getApplicationContext(), SharedPrefsUtil.DEVICE_NAME_KEY, str2);
        final boolean connectTo = this.mBluetoothManager.connectTo(str);
        runOnUiThread(new Runnable() {
            public void run() {
                String str = connectTo ? "Connected to " : "Could not connect to ";
                MainActivity mainActivity = MainActivity.this;
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(str2);
                Utils.showToastMessage(mainActivity, sb.toString());
                bluetooth.setText(sb.toString());

            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //onWifiChanged(null);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.esptouch1_location_permission_title)
                        .setMessage(R.string.esptouch1_location_permission_message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> finish())
                        .show();
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showProgress(boolean show) {
        if (show) {
            progressDialog.setTitle("Broadcasting new wifi setup");
            progressDialog.setMessage("Please wait a moment....");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        } else {
          //  contents.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        }
    }

    private StateResult check() {
        StateResult result = checkPermission();
        if (!result.permissionGranted) {
            return result;
        }
        result = checkLocation();
        result.permissionGranted = true;
        if (result.locationRequirement) {
            return result;
        }
        result = checkWifi();
        result.permissionGranted = true;
        result.locationRequirement = false;
        return result;
    }

    public void onWifiChanged(TextView textView) {
        StateResult stateResult = check();
        mSsid = stateResult.ssid;
        mSsidBytes = stateResult.ssidBytes;
        mBssid = stateResult.bssid;
        CharSequence message = stateResult.message;
        boolean confirmEnable = false;
        if (stateResult.wifiConnected) {
            confirmEnable = true;
            if (stateResult.is5G) {
                message = getString(R.string.esptouch1_wifi_5g_message);
            }
        } else {
            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.esptouch1_configure_wifi_change_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        }

        textView.setText(mSsid);
    }

    public void executeEsptouch(TextInputEditText passwords,TextInputEditText count, RadioGroup radioGroup ) {
        byte[] ssid = mSsidBytes == null ? ByteUtil.getBytesByString(this.mSsid)
                : mSsidBytes;
        byte[] bssid = TouchNetUtil.parseBssid2bytes(this.mBssid);
        CharSequence pwdStr = passwords.getText();
        CharSequence devCountStr = count.getText();
        byte[] deviceCount = devCountStr == null ? new byte[0] : devCountStr.toString().getBytes();
        byte[] password = pwdStr == null ? null : ByteUtil.getBytesByString(pwdStr.toString());
        byte[] broadcast = {(byte) (radioGroup.getCheckedRadioButtonId() == R.id.packageBroadcast
                ? 1 : 0)};
        if (mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(MainActivity.this);
        mTask.execute(ssid,bssid, password,deviceCount, broadcast);
    }

    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private final WeakReference<MainActivity> mActivity;

        private final Object mLock = new Object();
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;
        private ProgressDialog progressDialog;
        private LoadDialog mLoadingDialog;

        EsptouchAsyncTask4(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
            progressDialog = new ProgressDialog(mActivity.get());
        }

        void cancelEsptouch() {
            cancel(true);
            MainActivity activity = mActivity.get();
            if (activity != null) {
                //activity.showProgress(false);
                progressDialog.dismiss();
            }
            if (mResultDialog != null) {
                mResultDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = mActivity.get();
            this.mLoadingDialog = new LoadDialog();
            Bundle bundle = new Bundle();
            this.mLoadingDialog.setArguments(bundle);
            this.mLoadingDialog.show(activity.getSupportFragmentManager(), LoadDialog.TAG);
            this.mLoadingDialog.setCancelable(false);

        }

        @Override
        protected void onProgressUpdate(IEsptouchResult... values) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                IEsptouchResult result = values[0];
                Log.i(TAG, "EspTouchResult: " + result);
                String text = result.getBssid() + " is connected to the wifi";
                activity.inetIp = result.getInetAddress().getHostAddress();
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();

//                activity.text_results.append(String.format(
//                        Locale.ENGLISH,
//                        "%s,%s\n",
//                        result.getInetAddress().getHostAddress(),
//                        result.getBssid()
//                ));
            }
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            MainActivity activity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                byte[] broadcastData = params[4];
                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
                Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
                mEsptouchTask.setEsptouchListener(this::publishProgress);
            }
            return mEsptouchTask.executeForResults(0);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            MainActivity activity = mActivity.get();
            activity.mTask = null;
           // activity.showProgress(false);
            LoadDialog loadingDialog = this.mLoadingDialog;
            if (loadingDialog != null) {

                loadingDialog.dismissAllowingStateLoss();
            }
            if (result == null) {
                mResultDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.esptouch1_configure_result_failed_port)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                mResultDialog.setCanceledOnTouchOutside(false);
                return;
            }

            // check whether the task is cancelled and no results received
            IEsptouchResult firstResult = result.get(0);
            if (firstResult.isCancelled()) {
                return;
            }

            if (!firstResult.isSuc()) {
                mResultDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.esptouch1_configure_result_failed)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                mResultDialog.setCanceledOnTouchOutside(false);
                return;
            }

            ArrayList<CharSequence> resultMsgList = new ArrayList<>(result.size());
            for (IEsptouchResult touchResult : result) {
                String message = activity.getString(R.string.esptouch1_configure_result_success_item,
                        touchResult.getBssid(), touchResult.getInetAddress().getHostAddress());
                resultMsgList.add(message);
            }
            CharSequence[] items = new CharSequence[resultMsgList.size()];
            mResultDialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.esptouch1_configure_result_success)
                    .setItems(resultMsgList.toArray(items), null)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            mResultDialog.setCanceledOnTouchOutside(false);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.mBluetoothManager.removeDisconnectionListener(this);
        //  unregisterReceiver(this.mBluetoothDisconnectedReceiver);
    }

    public void onResume() {
        super.onResume();
        this.mIsActivityRunning = true;
    }


    public void onPause() {
        super.onPause();
        this.mIsActivityRunning = false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity){
        Window window = activity.getWindow();
        @SuppressLint("UseCompatLoadingForDrawables") Drawable background = activity.getResources().getDrawable(R.drawable.gradient_colors);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

    }


}