package com.harlharjj.softhub.ui.home;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androdocs.httprequest.HttpRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harlharjj.softhub.AddedDevices;
import com.harlharjj.softhub.DeviceAdapter;
import com.harlharjj.softhub.DeviceModels;
import com.harlharjj.softhub.MainActivity;
import com.harlharjj.softhub.Models;
import com.harlharjj.softhub.R;
import com.harlharjj.softhub.SharedPrefsUtil;
import com.harlharjj.softhub.TempModel;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private Button addNewButton, stateButton, stateButton1;
    RecyclerView recyclerView;
    private DatabaseReference rootReference;
    private FirebaseAuth mAuth;
    private String userID;
    private String status;
    private DeviceAdapter adapter;
    public TextView outTemp, outHum;
    private final List<DeviceModels>modelsList = new ArrayList<>();

    String API_key = "0ba8178e8a5d3239ce3d7f74533702d2";
    String CITY = "Gbagada";

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API_key);
            return response;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                JSONObject sys = jsonObj.getJSONObject("sys");
// CALL VALUE IN API :
                String city_name = jsonObj.getString("name");
                String countryname = sys.getString("country");
                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Last Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temperature = main.getString("temp");
                String cast = weather.getString("description");
                String humi_dity = main.getString("humidity");
                String temp_min = main.getString("temp_min");
                String temp_max = main.getString("temp_max");
                Long rise = sys.getLong("sunrise");
                String sunrise = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(rise * 1000));
                Long set = sys.getLong("sunset");
                String sunset = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(set * 1000));

                SharedPrefsUtil.save(getContext(),"temp", temp_max);

                float temp = Float.parseFloat(temp_max);

                outTemp.setText(Math.round(temp) + "⁰c");
                outHum.setText(humi_dity + "%");

            } catch (Exception e) {
            }
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home,  container, false);

        recyclerView = root.findViewById(R.id.recycler_devices);
        addNewButton = root.findViewById(R.id.newDevicesBtn);
        stateButton = root.findViewById(R.id.turnButton);
        stateButton1 = root.findViewById(R.id.turnButton1);
        rootReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        outTemp = root.findViewById(R.id.outsideTemp);
        outHum = root.findViewById(R.id.outsideHum);

        new weatherTask().execute();

        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        addNewButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddedDevices.class);
            startActivity(intent);
        });

        modelsList.clear();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DeviceAdapter(getContext(),modelsList);

        String deviceStatus = SharedPrefsUtil.getStringOrNull(getContext(),"status");

        if (deviceStatus != null){
            switch (deviceStatus){
                case "allDevicesON":
                    stateButton1.setVisibility(View.GONE);
                    stateButton.setVisibility(View.VISIBLE);
                    break;
                case "allDevicesOFF":
                    stateButton.setVisibility(View.INVISIBLE);
                    stateButton1.setVisibility(View.VISIBLE);
                    break;

            }
        }

        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i;

                    for (i = 0; i < modelsList.size(); i++) {
                        rootReference.child("DeviceRef").child(userID).child(modelsList.get(i).getDevice_key()).child("status").setValue(0);
                        SharedPrefsUtil.save(getContext(),"status", "allDevicesOFF");

                    }
                }

        });

        stateButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i;
                for (i = 0; i < modelsList.size(); i++) {
                    rootReference.child("DeviceRef").child(userID).child(modelsList.get(i).getDevice_key()).child("status").setValue(1);
                    SharedPrefsUtil.save(getContext(),"status", "allDevicesON");

                }

            }
        });





        rootReference.child("DeviceRef").child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                if (snapshot.exists()){
                    DeviceModels models = snapshot.getValue(DeviceModels.class);
                    modelsList.add(models);
                    adapter.notifyDataSetChanged();

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


        recyclerView.setAdapter(adapter);
        rootReference.keepSynced(true);


        return root;
    }



 }