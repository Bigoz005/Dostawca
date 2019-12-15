package com.example.dostawca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
                    @Override
                    public void onClick(View view) {
                        //todo: go to map - points: currentRoute.getPoints();
                    }
                }
        );


    }
}
