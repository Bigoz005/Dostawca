package com.example.dostawca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dostawca.dto.Point;
import com.example.dostawca.dto.Route;

import java.util.ArrayList;

public class PointsActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Intent i = getIntent();
        Route route = (Route) i
                .getSerializableExtra("route");
        ListView listview = findViewById(R.id.pointslist);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        for (Point point : route.getPoints()) {
            adapter.add(point.getName() + "\n" + point.getCoordinates());
        }


        listview.setAdapter(adapter);

    }
}
