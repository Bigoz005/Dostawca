package com.example.dostawca;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.widget.ListViewAutoScrollHelper;
import androidx.fragment.app.Fragment;

import com.example.dostawca.dto.Point;
import com.example.dostawca.dto.Route;
import com.example.dostawca.service.CurrentRouteService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class ListOfAddressesFragment extends Fragment {
    ArrayAdapter listViewAdapter;
    List<String> points = new ArrayList();
    TextView textView;

    public ListOfAddressesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("List Of Addresses");

        Route currentRoute = CurrentRouteService.getCurrentRoute();
        for (Point p : currentRoute.getPoints()) {
            points.add(p.getName());
        }
        if (points.isEmpty()) {
            points.add("Lista jest pusta!");
        }


        View rootView = inflater.inflate(R.layout.fragment_listofaddresses, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.current_route_list);

        listViewAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, points
        );

        lv.setAdapter(listViewAdapter);


        Button button = (Button) rootView.findViewById(R.id.save_point_button);
        textView = rootView.findViewById(R.id.address_input);

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = textView.getText().toString();
                        points.add(text);
                        CurrentRouteService.addPointToCurrentRoute(new Point(text, "XD", "XDD"));
                        textView.setText("");


                    }
                }
        );


        return rootView;
    }
}
