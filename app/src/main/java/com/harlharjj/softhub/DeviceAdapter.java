package com.harlharjj.softhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.harlharjj.softhub.DeviceAdapter.*;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {
    Context context;
    ArrayList<Model>arrayList;
    private List<DeviceModels>modelsList;
    private DatabaseReference database;
    private String userid;
    private FirebaseAuth auth;

    public DeviceAdapter(Context context, List<DeviceModels> modelsList) {
        this.context = context;
        //this.arrayList = arrayList;
        this.modelsList = modelsList;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.device_item,parent,false);


        return new DeviceHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();

        holder.setIsRecyclable(false);
        holder.deviceName.setText(modelsList.get(position).getName());
        holder.subNames.setText(modelsList.get(position).getRoomName());

        switch (modelsList.get(position).getType()){

            case "Smart Switch":
                holder.device.setImageResource(R.drawable.ic_light_bulb);
                break;
            case "Smart Socket":
                holder.device.setImageResource(R.drawable.ic_socket_svg);
                break;
        }


        if (modelsList.get(position).getType().equals("Smart Switch")){

            modelsList.get(position).setImage(R.drawable.ic_light);

        }
        else if (modelsList.get(position).getType().equals("Smart Socket")){

            modelsList.get(position).setImage(R.drawable.ic_socket_technology);
        }

        if (modelsList.get(position).getStatus() == 1){
            holder.button.setChecked(true);
        }
        else {
            holder.button.setChecked(false);
        }

        holder.button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            final String deviceKey = modelsList.get(holder.getAdapterPosition()).getDevice_key();
            final String roomName = modelsList.get(holder.getAdapterPosition()).getRoomName();
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    database.child("DeviceRef").child(userid).child(deviceKey).child("status").setValue(1);
                    database.child("Devices").child(userid).child(roomName).child(deviceKey).child("status").setValue(1);
                }
                else {
                    database.child("DeviceRef").child(userid).child(deviceKey).child("status").setValue(0);
                    database.child("Devices").child(userid).child(roomName).child(deviceKey).child("status").setValue(0);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelsList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class DeviceHolder extends RecyclerView.ViewHolder {

        ImageView device;
        TextView deviceName, deviceCount, subNames;
        Switch button;

        public DeviceHolder(View itemView) {
            super(itemView);

            device = itemView.findViewById(R.id.lampImage);
            deviceName = itemView.findViewById(R.id.lampstxt);
            subNames = itemView.findViewById(R.id.names);
            button = itemView.findViewById(R.id.switchs);
        }
    }
}
