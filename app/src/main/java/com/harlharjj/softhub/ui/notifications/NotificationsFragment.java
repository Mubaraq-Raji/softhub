package com.harlharjj.softhub.ui.notifications;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.google.android.material.textfield.TextInputEditText;
import com.harlharjj.softhub.EspTouchActivityAbs;
import com.harlharjj.softhub.MainActivity;
import com.harlharjj.softhub.MyApplication;
import com.harlharjj.softhub.R;
import com.harlharjj.softhub.SharedPrefsUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class NotificationsFragment extends Fragment{

    private NotificationsViewModel notificationsViewModel;
    private RadioGroup radioGroup, group;
    private TextView details, name;
    private Button proceedButton;
    private MutableLiveData<String> mBroadcastData;
    private TextInputEditText passWord, deviceCount;
    private ProgressDialog progressDialog;

    private byte[] mSsidBytes;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        radioGroup = root.findViewById(R.id.mode_group);
        group = root.findViewById(R.id.packageModeGroup);
        details = root.findViewById(R.id.modeDetails);
        name = root.findViewById(R.id.wifiName);
        proceedButton = root.findViewById(R.id.proceed);
        passWord = root.findViewById(R.id.apPasswordEdit);
        deviceCount = root.findViewById(R.id.deviceCountEdit);
       // progressDialog = new ProgressDialog(getActivity());

        MyApplication.getInstance().observeBroadcast(this, broadcast -> {
            Log.d(TAG, "onCreate: Broadcast=" + broadcast);
            ((MainActivity)getActivity()).onWifiChanged(name);
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.mod1:
                        SharedPrefsUtil.save(getContext(), "Mode", "WiFi");

                        break;

                    case R.id.mod2:

                        ((MainActivity) requireActivity()).showDeviceList();
                        SharedPrefsUtil.save(getContext(), "Mode", "Bluetooth");

                        break;

                }
            }
        });

        String modes = SharedPrefsUtil.getStringOrNull(getContext(),"Mode");

         if (modes != null) {
             switch (modes){
                 case "WiFi":
                     radioGroup.check(R.id.mod1);
                     break;
                 case "Bluetooth":
                     radioGroup.check(R.id.mod2);
                     break;

             }
         }

         details.setText(modes);
         proceedButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 ((MainActivity)getActivity()).executeEsptouch(passWord,deviceCount,group);
             }
         });

        return root;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
         ((MainActivity)getActivity()).recreate();

    }

}