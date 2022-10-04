package com.harlharjj.softhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Register extends AppCompatActivity {

    private EditText names, number, deviceNo, email, passWord, C_password;
    private ProgressDialog progressDialog;
    private final Context context = Register.this;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference storeDefaultDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this);
        setContentView(R.layout.activity_register);

        names = findViewById(R.id.fullName);
        number = findViewById(R.id.phoneNumber);
        deviceNo = findViewById(R.id.DeviceId);
        email = findViewById(R.id.RegEmail);
        passWord = findViewById(R.id.RegPassword);
        C_password = findViewById(R.id.ComfirmPass);
        Button registerButton = findViewById(R.id.regButton);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        registerButton.setOnClickListener(v -> {

            final String name = names.getText().toString();
            final String phone = number.getText().toString();
            final String device = deviceNo.getText().toString();
            final String emails = email.getText().toString();
            String password = passWord.getText().toString();
            String confirmPass = C_password.getText().toString();

            createAccount(name, emails, phone, password, confirmPass, device);

        });
        progressDialog = new ProgressDialog(context);

    }

    private void createAccount(final String userName, final String email, final String phone, String password, String confirmPass, String deviceId) {

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Email is not valid", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(context, "Mobile number is required", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(deviceId)) {
            Toast.makeText(context, "Device ID is required", Toast.LENGTH_SHORT).show();

        } else if (phone.length() < 11) {
            Toast.makeText(context, "Mobile number should be min of 11 characters", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show();

        } else if (password.length() < 6) {
            Toast.makeText(context, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(context, "Password confirmation is required", Toast.LENGTH_SHORT).show();

        } else if (!password.equals(confirmPass)) {
            Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT).show();

        } else {

            DatabaseReference deviceID = FirebaseDatabase.getInstance().getReference().child("DevicesID");
            deviceID.orderByChild("devices").equalTo(deviceId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        //do ur stuff
                        Toast.makeText(context, "DeviceID is valid", Toast.LENGTH_SHORT).show();
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {
                                        // get and link storage
                                        String current_userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                        storeDefaultDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(current_userID);

                                        storeDefaultDatabaseReference.child("user_name").setValue(userName);
                                        storeDefaultDatabaseReference.child("user_mobile").setValue(phone);
                                        storeDefaultDatabaseReference.child("user_email").setValue(email);
                                        storeDefaultDatabaseReference.child("deviceID").setValue(deviceId);
                                        storeDefaultDatabaseReference.child("created_at").setValue(ServerValue.TIMESTAMP)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        // SENDING VERIFICATION EMAIL TO THE REGISTERED USER'S EMAIL
                                                        user = mAuth.getCurrentUser();
                                                        if (user != null) {
                                                            user.sendEmailVerification()
                                                                    .addOnCompleteListener(task11 -> {
                                                                        if (task11.isSuccessful()) {

                                                                             registerSuccessPopUp();

                                                                            // LAUNCH activity after certain time period
                                                                            new Timer().schedule(new TimerTask() {
                                                                                public void run() {
                                                                                    Register.this.runOnUiThread(() -> {
                                                                                        mAuth.signOut();

                                                                                        Intent mainIntent = new Intent(context, Login.class);
                                                                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                        startActivity(mainIntent);
                                                                                        finish();
                                                                                        Toast.makeText(context, "Please check your email & verify.", Toast.LENGTH_LONG).show();

                                                                                    });
                                                                                }
                                                                            }, 8000);


                                                                        } else {
                                                                            mAuth.signOut();
                                                                        }
                                                                    });
                                                        }

                                                    }
                                                });

                                    } else {
                                        String message = Objects.requireNonNull(task.getException()).getMessage();
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                    }

                                    progressDialog.dismiss();

                                });


                        //config progressbar
                        progressDialog.setTitle("Creating new account");
                        progressDialog.setMessage("Please wait a moment....");
                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);





                } else {
                        //do something if not exists
                        Toast.makeText(context, "DeviceID is not valid. Please check!", Toast.LENGTH_LONG).show();
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


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

    private void registerSuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        View view = LayoutInflater.from(Register.this).inflate(R.layout.register_success_popup, null);
        builder.setCancelable(false);
        builder.setView(view);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, Login.class);
        startActivity(intent);
        finish();
    }
}