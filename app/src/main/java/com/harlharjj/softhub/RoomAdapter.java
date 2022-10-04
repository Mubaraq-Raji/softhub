package com.harlharjj.softhub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harlharjj.softhub.ui.dashboard.DashboardFragment;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomHolder> {
    Context context;
    private List<Rooms>arrayLists;
    private List<Models>modelsList;
    private DatabaseReference databaseReference;
    private String livingRoom, diningRoom, kitchen, guestRoom, childrenRoom, bedroom;


    public RoomAdapter(Context context, List<Rooms> arrayLists) {
        this.context = context;
        this.arrayLists = arrayLists;
    }

    public RoomAdapter() {
    }

    @NonNull
    @Override
    public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rooms_items,parent,false);
        return new RoomHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RoomHolder holder, int position) {

        String rooms = arrayLists.get(position).getName();
        int images = arrayLists.get(position).getImage();
        String roomType = arrayLists.get(position).getRoomType();
        String hum = arrayLists.get(position).getHumdity();
        String temp = arrayLists.get(position).getTemp();
        holder.RoomName.setText(rooms);
        holder.roomHumd.setText(hum);
        holder.RoomTemp.setText(roomType);


        switch (roomType) {
            case "Kitchen":
                holder.device.setImageResource(R.drawable.kitchen);
                break;
            case "BedRoom":
                holder.device.setImageResource(R.drawable.bedroom);
                //Picasso.get().load(bedroom).networkPolicy(NetworkPolicy.OFFLINE).into(holder.device);
                break;
            case "Living Room":
                holder.device.setImageResource(R.drawable.living);
                //Picasso.get().load(livingRoom).networkPolicy(NetworkPolicy.OFFLINE).into(holder.device);
                break;
            case "Guest Room":
                holder.device.setImageResource(R.drawable.guest);
                //Picasso.get().load(guestRoom).networkPolicy(NetworkPolicy.OFFLINE).into(holder.device);
                break;
            case "Children Room":
                holder.device.setImageResource(R.drawable.children);
                //Picasso.get().load(childrenRoom).networkPolicy(NetworkPolicy.OFFLINE).into(holder.device);
                break;
            case "Dining Room":
                holder.device.setImageResource(R.drawable.dining);
                //Picasso.get().load(diningRoom).networkPolicy(NetworkPolicy.OFFLINE).into(holder.device);
                break;
            case "Toilet":
                holder.device.setImageResource(R.drawable.toilet);
                break;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("images");
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                livingRoom = snapshot.child("living").getValue().toString();
//                kitchen = snapshot.child("kitchen").getValue().toString();
//                bedroom = snapshot.child("bedroom").getValue().toString();
//                guestRoom = snapshot.child("guest").getValue().toString();
//                childrenRoom = snapshot.child("children").getValue().toString();
//                diningRoom = snapshot.child("guest").getValue().toString();
                //Toast.makeText(context, livingRoom+kitchen, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
//               AppCompatActivity activity = (AppCompatActivity) v.getContext();
//               DashboardFragment dashboardFragment = new DashboardFragment();
//               roomFragment fragment = new roomFragment();
//               Bundle bundle = new Bundle();
//               bundle.putString("humidity",hum);
//               bundle.putString("temperature",temp);
//               bundle.putInt("images",images);
//               fragment.setArguments(bundle);
//               activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,fragment).addToBackStack(null).commit();
               Intent intent = new Intent(holder.itemView.getContext(), roomActivity.class);
               intent.putExtra("position",holder.getAdapterPosition());
               intent.putExtra("roomNames",arrayLists.get(holder.getAdapterPosition()).getName());
               intent.putExtra("roomType",roomType);
               holder.itemView.getContext().startActivity(intent);

           }
       });
    }
    @Override
    public int getItemCount() {
        return arrayLists.size();
    }



    public static class RoomHolder extends RecyclerView.ViewHolder {

        ImageView device;
        TextView RoomName, RoomTemp, roomHumd;

        public RoomHolder(@NonNull View itemView) {
            super(itemView);

            device = itemView.findViewById(R.id.roomImage);
            RoomName = itemView.findViewById(R.id.roomName);
            RoomTemp = itemView.findViewById(R.id.tempTxt);
            roomHumd = itemView.findViewById(R.id.HumTxt);


        }
    }
}
