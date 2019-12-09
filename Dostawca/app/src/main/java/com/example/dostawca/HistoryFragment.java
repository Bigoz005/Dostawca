package com.example.dostawca;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {

    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Historia");
        ArrayList<String> pointsHistory = new ArrayList<>();
        pointsHistory.add("Trasa testowa 1");
        pointsHistory.add("Trasa testowa 2");
        pointsHistory.add("Trasa testowa 3");

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);


        ListView lv = (ListView) rootView.findViewById(R.id.list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = new Intent(getActivity(), PointsActivity.class);
                startActivity(intent);



            }
        });

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, pointsHistory
        );

        lv.setAdapter(listViewAdapter);

        return rootView;

    }

}
