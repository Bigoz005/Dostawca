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
import android.widget.Toast;

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
    private List<Point> points = CurrentRouteService.getCurrentRoute().getPoints();
    private TextView textView;

    public ListOfAddressesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("List Of Addresses");

        if (points.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Brak adresów! Przejdź do skanera aby dodać elementy.", Toast.LENGTH_LONG);
            toast.show();
        }


        View rootView = inflater.inflate(R.layout.fragment_listofaddresses, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.current_route_list);
        CustomPointElementAdapter adapter = new CustomPointElementAdapter(points, getActivity());
        lv.setAdapter(adapter);
        Button button = (Button) rootView.findViewById(R.id.save_point_button);
        textView = rootView.findViewById(R.id.address_input);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = textView.getText().toString();
                        if (text.length() == 0) {
                            return;
                        }
                        points.add(new Point(textView.getText().toString(), "", "12.4, 49.3"));
                        textView.setText("");
                    }
                }
        );
        return rootView;
    }
}
