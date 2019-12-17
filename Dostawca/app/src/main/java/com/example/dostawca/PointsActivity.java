package com.example.dostawca;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dostawca.dto.Point;
import com.example.dostawca.dto.Route;
import com.example.dostawca.service.CurrentRouteService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PointsActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<String>();
    Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Intent i = getIntent();
        route = (Route) i
                .getSerializableExtra("route");
        ListView listview = findViewById(R.id.pointslist);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        for (Point point : route.getPoints()) {
            adapter.add(point.getName() + "\n" + point.getLat() + ", " + point.getLng());
        }


        listview.setAdapter(adapter);

        Button goToMapBtn = (Button) findViewById(R.id.show_route);
        goToMapBtn.setOnClickListener(
                new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        Log.d("mylog", "CLICKED");

                        Intent myIntent = new Intent(PointsActivity.this, MainActivity.class);
                        myIntent.putExtra("route", route);
                        PointsActivity.this.startActivity(myIntent);

////                        MapFragments mapFragments = new MapFragments();
//                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.replace(, mapFragments, "mapFragments");
//                        ft.addToBackStack(null);
//                        ft.commit();
//                        mapFragments.setNewRoute(route);
//                        MapFragments mapFragments = (MapFragments) getSupportFragmentManager().findFragmentById(R.id.flMain);
//                        MapFragments mapFragments = new MapFragments();
//                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.replace(R.id.flMain, mapFragments, "");
//                        ft.commit();

//                        FragmentManager fm = getSupportFragmentManager();
//                        fm.beginTransaction()
//                                .show(mapFragments)
//                                .commit();
                    }
                }
        );
    }
}

