package com.harlharjj.softhub;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class roomFragment extends Fragment {

    int images;
    String temp, hum;
    private final List<Rooms> roomsInfo = new ArrayList<>();
    ImageView roomImage;
    TextView temps, hums;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         //setStatusBarGradient(getActivity());
        View root = inflater.inflate(R.layout.fragment_rooms, container, false);

        Bundle bundle = getArguments();
        temp = bundle.getString("temperature");
        hum = bundle.getString("humidity");
        images = bundle.getInt("images");

        roomImage = root.findViewById(R.id.roomLayout);
        temps = root.findViewById(R.id.insideValue);
        hums = root.findViewById(R.id.txthum);

        roomImage.setImageResource(images);
        hums.setText(hum);
        temps.setText(temp);


        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getSupportFragmentManager().beginTransaction().remove(roomFragment.this).commit();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradients);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }

    }



    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_rooms);
//
//        images = getIntent().getExtras().getInt("images");
//        temp = getIntent().getExtras().getString("temperature");
//        hum = getIntent().getExtras().getString("humidity");
//
//        roomImage = findViewById(R.id.roomLayout);
//        temps = findViewById(R.id.insideValue);
//        hums = findViewById(R.id.txthum);
//
//        roomImage.setImageResource(images);
//        temps.setText(temp);
//        hums.setText(hum);
//
//
//    }


}