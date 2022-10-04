package com.harlharjj.softhub;

import android.content.Context;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harlharjj.softhub.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RoomDeviceAdapter extends RecyclerView.Adapter<RoomDeviceAdapter.DeviceHolder> {
    Context context;
    ArrayList<Model> arrayList;
    private List<Models> modelsList;
    private DatabaseReference database;
    private String userid;
    private FirebaseAuth auth;
    private bluetoothData listener;
    private long Id;
    private BluetoothManager mBluetoothManager;
    private IncomingDataRunnable incomingDataRunnable;

    public RoomDeviceAdapter(Context context, List<Models> modelsList) {
        this.context = context;
        this.modelsList = modelsList;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_items, parent, false);
        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();

        holder.deviceName.setText(modelsList.get(position).getName());
        holder.deviceCount.setText(modelsList.get(position).getType());
        modelsList.get(position).setDeviceId(holder.getItemId());

        if (modelsList.get(position).getType().equals("Smart Switch")) {
            holder.subNames.setText(modelsList.get(position).getSwitchType());
            holder.device.setImageResource(R.drawable.ic_light_bulb);
            modelsList.get(position).setImage(R.drawable.ic_light_bulb);

        } else if (modelsList.get(position).getType().equals("Smart Socket")) {
            holder.subNames.setText(modelsList.get(position).getSocketType());
            holder.device.setImageResource(R.drawable.ic_socket_svg);
            modelsList.get(position).setImage(R.drawable.ic_socket_svg);

        } else if (modelsList.get(position).getType().equals("Sensor")) {
            holder.subNames.setText(modelsList.get(position).getSensorType());
        }

        if (modelsList.get(position).getStatus() == 1){
            holder.deviceSwitch.setChecked(true);
        }else {
            holder.deviceSwitch.setChecked(false);
        }


        String Mode = SharedPrefsUtil.getStringOrNull(context, "Mode");
        if (Mode != null) {
            switch (Mode) {

                case "WiFi":

                    for (int i = 0; i < 10; i++) {
                        if (modelsList.get(holder.getAdapterPosition()).getPosition() == i) {
                            int n = i;

                            holder.deviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    String deviceKey = modelsList.get(holder.getAdapterPosition()).getDevice_key();
                                    String roomName = modelsList.get(holder.getAdapterPosition()).getRoomName();

                                    if (isChecked) {
                                        database.child("DeviceRef").child(userid).child(deviceKey).child("status").setValue(1);
                                        database.child("Devices").child(userid).child(roomName).child(deviceKey).child("status").setValue(1);
                                        database.child("Devices").child("SSB2567").child("L"+n).setValue("on"+holder.getAdapterPosition());

                                    }else {

                                        database.child("DeviceRef").child(userid).child(deviceKey).child("status").setValue(0);
                                        database.child("Devices").child(userid).child(roomName).child(deviceKey).child("status").setValue(0);
                                        database.child("Devices").child("SSB2567").child("L"+n).setValue("off"+holder.getAdapterPosition());
                                    }
//                                    switch (holder.getAdapterPosition()) {
//
//                                        case 0:
//                                            if (isChecked) {
//                                                database.child("Devices").child("SSB2567").child("L" + n).setValue("on"+n);
//                                                database.child("DeviceRef").child(userid).child(modelsList.get(holder.getAdapterPosition()).getDevice_key()).child("status").setValue(1);
//                                                database.child("Devices").child(userid).child(modelsList.get(holder.getAdapterPosition()).getRoomName()).child(modelsList.get(holder.getAdapterPosition()).getDevice_key()).child("status").setValue(1);
//
//                                            } else {
//
//                                                database.child("Devices").child("SSB2567").child("L" + n).setValue("off"+n);
//                                                database.child("DeviceRef").child(userid).child(modelsList.get(holder.getAdapterPosition()).getDevice_key()).child("status").setValue(0);
//                                            }
//                                            break;
//                                        case 1:
//                                            if (isChecked) {
//
//                                                database.child("Devices").child("SSB2567").child(("S" + n)).setValue("a"+n);
//
//                                            } else {
//
//                                                database.child("Devices").child("SSB2567").child("S" + n).setValue("b"+n);
//                                            }
//                                            break;
//                                        case 2:
//                                            if (isChecked) {
//
//                                                database.child("Devices").child("SSB2567").child("P" + n).setValue("c"+n);
//
//                                            } else {
//
//                                                database.child("Devices").child("SSB2567").child("P" + n).setValue("d"+n);
//                                            }
//                                            break;
//                                        case 3:
//                                            if (isChecked) {
//
//                                                database.child("Devices").child("SSB2567").child("R" + n).setValue("e"+n);
//
//                                            } else {
//
//                                                database.child("Devices").child("SSB2567").child("R" + n).setValue("f"+n);
//                                            }
//                                            break;
//                                        case 4:
//                                            if (isChecked) {
//
//                                                database.child("Devices").child("SSB2567").child("T" + n).setValue("g"+n);
//
//                                            } else {
//
//                                                database.child("Devices").child("SSB2567").child("T" + n).setValue("h"+n);
//                                            }
//                                            break;
//                                    }
                                }
                            });

                        }
                    }

                    break;
                case "Bluetooth":
                    for (int i = 0; i < 10; i++) {
                        if (modelsList.get(holder.getAdapterPosition()).getPosition() == i) {
                            int n = i;

                            holder.deviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                    switch (holder.getAdapterPosition()){

                                        case 0:
                                            if (isChecked) {

                                                listener.sendData("A"+n);

                                            } else {
                                                listener.sendData("B"+n);

                                            }
                                            break;
                                        case 1:
                                            if (isChecked) {
                                                listener.sendData("C"+n);

                                            } else {
                                                listener.sendData("D"+n);

                                            }
                                            break;
                                        case 2:
                                            if (isChecked) {
                                                listener.sendData("E"+n);

                                            } else {
                                                listener.sendData("F"+n);

                                            }
                                            break;
                                        case 3:
                                            if (isChecked) {
                                                listener.sendData("G"+n);

                                            } else {
                                                listener.sendData("H"+n);

                                            }
                                            break;
                                        case 4:
                                            if (isChecked) {
                                                listener.sendData("I"+n);
                                               ;
                                            } else {
                                                listener.sendData("J"+n);

                                            }
                                            break;
                                    }

                                }
                            });


                        }
                    }

                    break;
            }
        }

    }



    public interface bluetoothData {
        void sendData(String str);

    }

    public void bluetoothData(bluetoothData listener) {
        this.listener = listener;

    }


    @Override
    public int getItemCount() {
        return modelsList.size();
    }

    public static class DeviceHolder extends RecyclerView.ViewHolder {

        ImageView device;
        TextView deviceName, deviceCount, subNames;
        SwitchCompat deviceSwitch;

        public DeviceHolder(View itemView) {
            super(itemView);

            device = itemView.findViewById(R.id.lampImage);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceCount = itemView.findViewById(R.id.subtype);
            subNames = itemView.findViewById(R.id.types);
            deviceSwitch = itemView.findViewById(R.id.switchs);
        }
    }
}
