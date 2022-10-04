package com.harlharjj.softhub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AddedDevices extends AppCompatActivity {

    private EditText newDevice;
    private String sensorType, deviceName, switchType, socketType, deviceType,location;
    private RadioGroup switchGroup, socketGroup, sensorGroup;
    private List<Models> deviceList = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private DatabaseReference rootReference;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(AddedDevices.this);
        setContentView(R.layout.addnew_devices);
        newDevice = findViewById(R.id.edit_device);
        Button addButton = findViewById(R.id.addNewDevice);
        switchGroup = findViewById(R.id.switch_group);
        socketGroup = findViewById(R.id.socket_group);
        sensorGroup = findViewById(R.id.sensor_group);
        progressDialog = new ProgressDialog(AddedDevices.this);

        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("DeviceModel");
        rootReference = FirebaseDatabase.getInstance().getReference();




        addButton.setOnClickListener(v -> {
            deviceName = newDevice.getText().toString();

            addNewDevices();
            Toast.makeText(AddedDevices.this, deviceName + deviceType + switchType + socketType + sensorType + location, Toast.LENGTH_SHORT).show();


        });
    }

    private void addNewDevices(){

        DatabaseReference push_id = mDatabaseRef.child(userID);
        String unique_key = push_id.getKey();
        String user_reference = "deviceModel/" + userID;
        Calendar calendar = Calendar.getInstance();
        String date = String.valueOf((calendar.get(Calendar.MILLISECOND)));


        switch (deviceType) {
            case "Smart Switch": {
                HashMap<String, Object> deviceDetails = new HashMap<>();
                deviceDetails.put("name", deviceName);
                deviceDetails.put("type", deviceType);
                deviceDetails.put("switchType", switchType);
                deviceDetails.put("location", location);
                HashMap<String, Object> messageBodyDetails = new HashMap<>();
                messageBodyDetails.put(user_reference + "/" + date, deviceDetails);
                rootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.e("Sending message", error.getMessage());
                            SuccessPopUp();
//                            new Timer().schedule(new TimerTask() {
//                                public void run() {
//                                    AddedDevices.this.runOnUiThread(() -> {
//                                        Intent mainIntent = new Intent(AddedDevices.this, MainActivity.class);
//                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(mainIntent);
//                                        finish();
//                                    });
//                                }
//                            }, 3000);
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
                deviceDetails.put("name", deviceName);
                deviceDetails.put("type", deviceType);
                deviceDetails.put("socketType", socketType);
                deviceDetails.put("location", location);
                HashMap<String, Object> messageBodyDetails = new HashMap<>();
                messageBodyDetails.put(user_reference + "/" + date, deviceDetails);
                rootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.e("Sending message", error.getMessage());
                            SuccessPopUp();
                            new Timer().schedule(new TimerTask() {
                                public void run() {
                                    AddedDevices.this.runOnUiThread(() -> {
                                        Intent mainIntent = new Intent(AddedDevices.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    });
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

            case "Sensor": {
                HashMap<String, Object> deviceDetails = new HashMap<>();
                deviceDetails.put("name", deviceName);
                deviceDetails.put("type", deviceType);
                deviceDetails.put("sensorType", sensorType);
                deviceDetails.put("location", location);
                HashMap<String, Object> messageBodyDetails = new HashMap<>();
                messageBodyDetails.put(user_reference + "/" + date, deviceDetails);
                rootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.e("Sending message", error.getMessage());
                            SuccessPopUp();
                            new Timer().schedule(new TimerTask() {
                                public void run() {
                                    AddedDevices.this.runOnUiThread(() -> {
                                        Intent mainIntent = new Intent(AddedDevices.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    });
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

    public void selectedSwitch(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){

            case R.id.SS1:
                if (checked){
                    switchType = "1 Gang";
                    break;
                }
            case R.id.SS2:
                if (checked){
                    switchType = "2 Gang";
                    break;
                }
            case R.id.SS3:
                if (checked){
                    switchType = "3 Gang";
                    break;
                }
            case R.id.SS4:
                if (checked){
                    switchType = "4 Gang";
                    break;
                }
        }

    }

    public void selectedSocket(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){

            case R.id.Sock1:
                if (checked){
                    socketType = "10 Amps";
                    break;
                }
            case R.id.Sock2:
                if (checked){
                    socketType = "30 Amps";
                    break;
                }

        }


    }

    public void selectedSensor(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){

            case R.id.S1:
                if (checked){
                    sensorType = "Motion Sensor";
                    break;
                }
            case R.id.S2:
                if (checked){
                    sensorType = "Temperature Sensor";
                    break;
                }
            case R.id.S3:
                if (checked){
                    sensorType = "Humidity Sensor";
                    break;
                }
            case R.id.S4:
                if (checked){
                    sensorType = "Light Intensity Sensor";
                    break;
                }
        }

    }

    public void selectedRoom(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){

            case R.id.R1:
                if (checked){
                    location = "Living Room";

                    break;
                }
            case R.id.R2:
                if (checked){
                    location = "BedRoom";
                    break;
                }
            case R.id.R3:
                if (checked){
                    location = "Guest Room";
                    break;
                }
            case R.id.R4:
                if (checked){
                    location = "Toilet";
                    break;
                }

            case R.id.R5:
                if (checked){
                    location = "Children Room";
                    break;
                }

            case R.id.R6:
                if (checked){
                    location = "Dining Room";
                    break;
                }
            case R.id.R7:
                if (checked){
                    location = "Kitchen";
                    break;
                }

        }

    }

    public void selectedM(View view){
    boolean checked = ((RadioButton) view).isChecked();
    switch (view.getId()){

        case R.id.M1:
            if (checked){
                deviceType = "Smart Switch";
                switchGroup.setVisibility(View.VISIBLE);
                socketGroup.setVisibility(View.GONE);
                sensorGroup.setVisibility(View.GONE);
                break;
            }
        case R.id.M2:
            if (checked){
                deviceType = "Smart Socket";
                socketGroup.setVisibility(View.VISIBLE);
                switchGroup.setVisibility(View.GONE);
                sensorGroup.setVisibility(View.GONE);
                break;
            }
        case R.id.M3:
            if (checked){
                deviceType = "Sensor";
                sensorGroup.setVisibility(View.VISIBLE);
                switchGroup.setVisibility(View.GONE);
                socketGroup.setVisibility(View.GONE);
                break;
            }



      }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        Window window = activity.getWindow();
        @SuppressWarnings("deprecation") @SuppressLint("UseCompatLoadingForDrawables") Drawable background = activity.getResources().getDrawable(R.drawable.gradient_colors);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

    }

}