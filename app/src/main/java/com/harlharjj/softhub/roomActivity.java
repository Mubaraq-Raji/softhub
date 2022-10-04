package com.harlharjj.softhub;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class roomActivity extends AppCompatActivity implements RoomDeviceAdapter.bluetoothData{

    private Button addDeviceBtn, deviceButton;
    private BluetoothManager mBluetoothManager;
    private IncomingDataRunnable incomingDataRunnable;
    private String sensorType, deviceName, switchType, socketType, deviceType, userId, roomName, roomType;
    private EditText deviceEdit;
    private TextView rooms, outTemp;
    private ImageView roomImage;
    private RecyclerView deviceRecycler;
    private DatabaseReference databaseReference;
    private DatabaseReference rootReference;
    private FirebaseAuth auth;
    private RoomDeviceAdapter roomDeviceAdapter;
    private ProgressDialog progressDialog;
    private int position;
    private final List<Models> modelsList = new ArrayList<>();
    private RadioGroup switchGroup, socketGroup, sensorGroup, typeGroup;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStatusBarGradient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        auth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Devices");
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        BluetoothManager.initialize(this);
        this.mBluetoothManager = BluetoothManager.getInstance();

        progressDialog = new ProgressDialog(this);
        rooms = findViewById(R.id.rooms);
        outTemp = findViewById(R.id.outsideValues);

        String temp = SharedPrefsUtil.getStringOrNull(getApplicationContext(), "temp");
        if (temp != null){
            float temperature = Float.parseFloat(temp);
            outTemp.setText(Math.round(temperature) + "⁰c");
        }

        roomName = getIntent().getExtras().getString("roomNames");
        roomType = getIntent().getExtras().getString("roomType");
        position = getIntent().getExtras().getInt("position");
        rooms.setText(roomName);
        roomImage = findViewById(R.id.roomLayout);

        switch (roomType){
            case "Kitchen":
                roomImage.setImageResource(R.drawable.kitchen);
                break;
            case "BedRoom":
                roomImage.setImageResource(R.drawable.bedroom);
                break;
            case "Living Room":
                roomImage.setImageResource(R.drawable.living);
               break;
            case "Guest Room":
                roomImage.setImageResource(R.drawable.guest);
               break;
            case "Children Room":
                roomImage.setImageResource(R.drawable.children);
                 break;
            case "Dining Room":
                roomImage.setImageResource(R.drawable.dining);
                break;
            case "Toilet":
                roomImage.setImageResource(R.drawable.toilet);
                break;
        }

        deviceRecycler = findViewById(R.id.room_devices);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        deviceRecycler.setLayoutManager(layoutManager);

        addDeviceBtn = findViewById(R.id.addDeviceButton);
        addDeviceBtn.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View deviceDialog = LayoutInflater.from(v.getContext()).inflate(R.layout.new_device, null);
            builder.setView(deviceDialog);
            final AlertDialog dialog = builder.create();

            deviceEdit = deviceDialog.findViewById(R.id.deviceEditTxt);
            deviceButton = deviceDialog.findViewById(R.id.addNewDeviceBtn);
            typeGroup = deviceDialog.findViewById(R.id.type_group);
            switchGroup = deviceDialog.findViewById(R.id.switch_group);
            socketGroup = deviceDialog.findViewById(R.id.socket_group);
            sensorGroup = deviceDialog.findViewById(R.id.sensor_group);

            typeGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId){

                    case R.id.M1:
                        switchGroup.setVisibility(View.VISIBLE);
                        socketGroup.setVisibility(View.GONE);
                        socketGroup.clearCheck();
                        sensorGroup.setVisibility(View.GONE);
                        socketGroup.clearCheck();
                        deviceType = "Smart Switch";
                        break;

                    case R.id.M2:
                        switchGroup.setVisibility(View.GONE);
                        switchGroup.clearCheck();
                        socketGroup.setVisibility(View.VISIBLE);
                        sensorGroup.setVisibility(View.GONE);
                        sensorGroup.clearCheck();
                        deviceType = "Smart Socket";
                        break;

                    case R.id.M3:
                        switchGroup.setVisibility(View.GONE);
                        switchGroup.clearCheck();
                        socketGroup.setVisibility(View.GONE);
                        socketGroup.clearCheck();
                        sensorGroup.setVisibility(View.VISIBLE);
                        deviceType = "Sensor";
                        break;

                }
            });

            switchGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId){
                    case R.id.SS1:
                        switchType = "1 Gang";
                        break;

                    case R.id.SS2:
                        switchType = "2 Gang";
                        break;

                    case R.id.SS3:
                        switchType = "3 Gang";
                        break;

                    case R.id.SS4:
                        switchType = "4 Gang";
                        break;
                }

            });

            socketGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId){
                    case R.id.Sock1:
                        socketType = "10 Amps";
                        break;

                    case R.id.Sock2:
                        socketType = "30 Amps";
                        break;


                }
            });

            sensorGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId){
                    case R.id.S1:
                        sensorType = "Motion Sensor";
                        break;

                    case R.id.S3:
                        sensorType = "Temperature Sensor";
                        break;

                    case R.id.S2:
                        sensorType = "Humidity Sensor";
                        break;

                    case R.id.S4:
                        sensorType = "Light Intensity Sensor";
                        break;

                }


            });

            deviceButton.setOnClickListener(v1 -> {
                deviceName = deviceEdit.getText().toString();
                addNewDevice(deviceName,deviceType,switchType,socketType,sensorType);

            });

            dialog.show();

        });

        roomDeviceAdapter = new RoomDeviceAdapter(this, modelsList);
        deviceRecycler.setAdapter(roomDeviceAdapter);
        roomDeviceAdapter.bluetoothData(this);

        rootReference.child("Devices").child(userId).child(roomName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    Models models = snapshot.getValue(Models.class);
                    modelsList.add(models);
                    roomDeviceAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //rootReference.keepSynced(true);
    }

    public void sendData(String string) {
        if (this.mBluetoothManager.isConnected()) {
            this.mBluetoothManager.sendData(string);

        } else {

        }
    }

    private void addNewDevice(String deviceNames, String deviceTypes, String switchTypes, String socketTypes, String sensorTypes){
        DatabaseReference push_id = databaseReference.child(userId).push();
        String unique_key = push_id.getKey();
        String device_reference = "DeviceRef/" +userId;
        String user_reference = "Devices/" +userId + "/"+ roomName;

        switch (deviceType) {
            case "Smart Switch": {
                HashMap<String, Object> deviceDetails = new HashMap<>();
                deviceDetails.put("name", deviceNames);
                deviceDetails.put("type", deviceTypes);
                deviceDetails.put("roomName", roomName);
                deviceDetails.put("position", position);
                deviceDetails.put("status", 1);
                deviceDetails.put("device_key", unique_key);
                deviceDetails.put("switchType", switchTypes);
                HashMap<String, Object> messageBodyDetails = new HashMap<>();
                messageBodyDetails.put(user_reference + "/" + unique_key, deviceDetails);
                messageBodyDetails.put(device_reference + "/" + unique_key, deviceDetails);
                rootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.e("Sending message", error.getMessage());
                            SuccessPopUp();
                            new Timer().schedule(new TimerTask() {
                                public void run() {
                                    roomActivity.this.runOnUiThread(roomActivity.this::finish);
                                }
                            }, 3000);
                        }
                        progressDialog.dismiss();
                    }

                });

                progressDialog.setTitle("Creating new device");
                progressDialog.setMessage("Please wait a moment....");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                break;
            }
            case "Smart Socket": {
                HashMap<String, Object> deviceDetails = new HashMap<>();
                deviceDetails.put("name", deviceNames);
                deviceDetails.put("type", deviceTypes);
                deviceDetails.put("roomName", roomName);
                deviceDetails.put("position", position);
                deviceDetails.put("status", 1);
                deviceDetails.put("device_key", unique_key);
                deviceDetails.put("socketType", socketTypes);

                HashMap<String, Object> messageBodyDetails = new HashMap<>();
                messageBodyDetails.put(user_reference + "/" + unique_key, deviceDetails);
                messageBodyDetails.put(device_reference + "/" + unique_key, deviceDetails);
                rootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.e("Sending message", error.getMessage());
                            SuccessPopUp();
                            new Timer().schedule(new TimerTask() {
                                public void run() {
                                    roomActivity.this.runOnUiThread(() -> {
                                        progressDialog.dismiss();
                                    });
                                }
                            }, 3000);
                            progressDialog.dismiss();
                        }
                        progressDialog.dismiss();
                    }
                });
                progressDialog.setTitle("Creating new device");
                progressDialog.setMessage("Please wait a moment....");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                break;
            }

            case "Sensor": {
                HashMap<String, Object> deviceDetails = new HashMap<>();
                deviceDetails.put("name", deviceNames);
                deviceDetails.put("type", deviceTypes);
                deviceDetails.put("roomName", roomName);
                deviceDetails.put("position", position);
                deviceDetails.put("status", 1);
                deviceDetails.put("device_key", unique_key);
                deviceDetails.put("sensorType", sensorTypes);

                HashMap<String, Object> messageBodyDetails = new HashMap<>();
                messageBodyDetails.put(user_reference + "/" + unique_key, deviceDetails);
                messageBodyDetails.put(device_reference + "/" + unique_key, deviceDetails);
                rootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.e("Sending message", error.getMessage());
                            SuccessPopUp();
                        }
                        progressDialog.dismiss();
                    }
                });

                progressDialog.setTitle("Creating new device");
                progressDialog.setMessage("Please wait a moment....");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                break;
            }
        }

    }

    private void SuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.device_success_popup, null);
        builder.setCancelable(false);
        builder.setView(view);
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity){
        Window window = activity.getWindow();
        @SuppressLint("UseCompatLoadingForDrawables") Drawable background = activity.getResources().getDrawable(R.drawable.gradients);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

    }
}
