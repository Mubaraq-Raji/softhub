package com.harlharjj.softhub;


import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private EditText loginEmail, loginPass;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this);
        setContentView(R.layout.activity_login);
        loginEmail = findViewById(R.id.email);
        loginPass = findViewById(R.id.password);
        Button loginButtons = findViewById(R.id.loginBtn);
        Button regButton = findViewById(R.id.RegisterBtn);
        TextView forgetPass = findViewById(R.id.forgetPasstxt);

        progressDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");


        loginButtons.setOnClickListener(v -> {
            final String email = loginEmail.getText().toString();
            String password = loginPass.getText().toString();
            loginAccount(email,password);

        });

        regButton.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });

        forgetPass.setOnClickListener(v -> {

        });

    }

    public void loginAccount(final String email, String password){

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"email is required",Toast.LENGTH_LONG).show();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password is required", Toast.LENGTH_LONG).show();
        }else if (password.length() < 6){
            Toast.makeText(this, "password must be more than 6 characters",Toast.LENGTH_LONG).show();
        }
        else {

            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()){

                            checkVerifiedEmail();


                        } else {
                            Toast.makeText(Login.this, "Your email and password may be incorrect. Please check & try again.", Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();

                    });
        }

        }

    private void checkVerifiedEmail() {
        user = mAuth.getCurrentUser();
        boolean isVerified = false;
        if (user != null) {
            isVerified = user.isEmailVerified();
        }
        if (isVerified){
            String UID = mAuth.getCurrentUser().getUid();
            userDatabaseReference.child(UID).child("verified").setValue("true");

            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(Login.this, "Email is not verified. Please verify first", Toast.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity){
        Window window = activity.getWindow();
        @SuppressWarnings("deprecation") @SuppressLint("UseCompatLoadingForDrawables") Drawable background = activity.getResources().getDrawable(R.drawable.gradients);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

    }


}