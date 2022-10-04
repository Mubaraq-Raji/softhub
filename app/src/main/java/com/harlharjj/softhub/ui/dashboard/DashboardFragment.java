package com.harlharjj.softhub.ui.dashboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.harlharjj.softhub.Models;
import com.harlharjj.softhub.R;
import com.harlharjj.softhub.RoomAdapter;
import com.harlharjj.softhub.Rooms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment {

    RecyclerView recyclerViews;
    private final List <Rooms> roomList = new ArrayList<>();
    private DatabaseReference rootReference;
    private FirebaseAuth mAuth;
    private String userID;
    private String roomName;
    private String roomType;
    private Button addNewRoom;
    private RoomAdapter roomAdapter;
    private List<Models> deviceList = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;

    private final List<Models> modelsList = new ArrayList<>();


    private DashboardViewModel dashboardViewModel;

    int[] rooms = {R.drawable.kitchen, R.drawable.living, R.drawable.children, R.drawable.bedroom, R.drawable.guest, R.drawable.dining};
    String[] temp = {"37⁰C","25.5⁰C","30.5⁰C","28.5⁰C","28.5⁰C","32.5⁰C"};
    String[] humd = {"50.5%","55.7%","68.5%","45.5%","50%","60.5%"};
    String[] roomNames = {"Kitchen", "Living Room", "Children Room", "Bedroom", "Guest Room", "Dining Room"};


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerViews = root.findViewById(R.id.roomRecycler);

        rootReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Rooms");

        userID = mAuth.getCurrentUser().getUid();
        addNewRoom = root.findViewById(R.id.addRoomBtn);
        addNewRoom.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.add_room_layout,null);
            Button button = dialogView.findViewById(R.id.roomButton);
            RadioGroup radioGroup = dialogView.findViewById(R.id.room_group_1);
            RadioGroup radioGroup1 = dialogView.findViewById(R.id.room_group_2);
            RadioGroup radioGroup2 = dialogView.findViewById(R.id.room_group_3);
            EditText editText = dialogView.findViewById(R.id.edit_room);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    switch (checkedId){


                        case R.id.room1:
                            roomType = "Living Room";
                            radioGroup1.clearCheck();
                            radioGroup2.clearCheck();
                            break;

                        case R.id.room2:
                            roomType = "BedRoom";
                            radioGroup1.clearCheck();
                            radioGroup2.clearCheck();
                            break;

                        case R.id.room3:
                            roomType = "Guest Room";
                            radioGroup1.clearCheck();
                            radioGroup2.clearCheck();
                            break;

                    }
                }
            });

            radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){

                        case R.id.room4:
                            roomType = "Children Room";
                            radioGroup.clearCheck();
                            radioGroup2.clearCheck();
                            break;

                        case R.id.room5:
                            roomType = "Dining Room";
                            radioGroup.clearCheck();
                            radioGroup2.clearCheck();
                            break;

                        case R.id.room6:
                            roomType = "Kitchen";
                            radioGroup.clearCheck();
                            radioGroup2.clearCheck();
                            break;

                    }

                }
            });

            radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){

                        case R.id.room7:
                            roomType = "Toilet";
                            radioGroup.clearCheck();
                            radioGroup1.clearCheck();
                            break;

                    }
                }
            });


            button.setOnClickListener(v1 -> {
                roomName = editText.getText().toString();
                Toast.makeText(getActivity(), roomName + roomType, Toast.LENGTH_LONG).show();

                addNewRoom(roomName, roomType);


            });
            alertDialog.show();

        });

        //roomAdapter = new RoomAdapter(getContext(),modelsList);

        roomAdapter = new RoomAdapter(getContext(),roomList);


        rootReference.child("Rooms").child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Rooms models = snapshot.getValue(Rooms.class);
                    roomList.add(models);
                    roomAdapter.notifyDataSetChanged();
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


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2,RecyclerView.VERTICAL,false);
        recyclerViews.setLayoutManager(layoutManager);
        //recyclerViews.setHasFixedSize(true);

//        for (int i = 0; i < rooms.length; i++){
//            Rooms rooms1 = new Rooms();
//            rooms1.setImage(rooms[i]);
//            rooms1.setName(roomNames[i]);
//            rooms1.setHumdity(humd[i]);
//            rooms1.setTemp(temp[i]);
//
//            arrayLists.add(rooms1);
//
//        }


        recyclerViews.setAdapter(roomAdapter);
        rootReference.keepSynced(true);



        return root;
    }

    private void addNewRoom(String roomNames, String roomTypes){

        DatabaseReference pushId = mDatabaseRef.child(userID);
        String user_reference = "Rooms/" + userID;

        HashMap<String, Object> roomDetails = new HashMap<>();
        roomDetails.put("name", roomNames);
        roomDetails.put("roomType", roomTypes);

        HashMap<String, Object> roomPush = new HashMap<>();
        roomPush.put(user_reference + "/" + roomNames, roomDetails );

        rootReference.updateChildren(roomPush, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

            }
        });
    }


    protected void replaceContentLayout(int sourceId, int destinationId) {
        View contentLayout = getView().findViewById(destinationId);
        ViewGroup parent = (ViewGroup) contentLayout.getParent();
        int index = parent.indexOfChild(contentLayout);
        parent.removeView(contentLayout);
        contentLayout = getLayoutInflater().inflate(sourceId, parent, false);
        parent.addView(contentLayout, index);
    }



}