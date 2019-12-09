package com.example.dostawca;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dostawca.dto.Route;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class HistoryFragment extends Fragment {

    ArrayAdapter<String> listViewAdapter;
    ArrayList<String> routeNamesHistory = new ArrayList<>();
    ArrayList<Route> routesHistory = new ArrayList<>();

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Historia");


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("users").child("Du9Rue5ssZS4gYPmeUuL2rrKu4O2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e("Count ", "" + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Route route = postSnapshot.getValue(Route.class);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'godz. 'hh:mm");
                    String result = formatter.format(route.getCreated());
                    routeNamesHistory.add(route.getName() + "\n" + "Utworzono: " + result);
                    routesHistory.add(route);
                    listViewAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Count ", "" + databaseError.getMessage());
            }
        });

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = new Intent(getActivity(), PointsActivity.class);
                intent.putExtra("route", routesHistory.get(position));
                startActivity(intent);


            }
        });


        listViewAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, routeNamesHistory
        );

        lv.setAdapter(listViewAdapter);


        return rootView;

    }

}
