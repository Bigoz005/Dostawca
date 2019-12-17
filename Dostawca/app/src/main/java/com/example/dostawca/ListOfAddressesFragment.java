package com.example.dostawca;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.core.widget.ListViewAutoScrollHelper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.dostawca.dao.FirebaseDAO;
import com.example.dostawca.dto.Point;
import com.example.dostawca.dto.Route;
import com.example.dostawca.service.CurrentRouteService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class ListOfAddressesFragment extends Fragment {
    private List<Point> points = CurrentRouteService.getCurrentRoute().getPoints();
    private TextView textView;
    FirebaseDAO firebaseDAO = new FirebaseDAO();
    CustomPointElementAdapter adapter;

    public ListOfAddressesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("List Of Addresses");

        Window window = ((MainActivity) getActivity()).getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (points.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Brak adresów! Przejdź do skanera aby dodać elementy.", Toast.LENGTH_LONG);
            toast.show();
        }


        View rootView = inflater.inflate(R.layout.fragment_listofaddresses, container, false);

        ListView lv = (ListView) rootView.findViewById(R.id.current_route_list);
        adapter = new CustomPointElementAdapter(points, getActivity());
        lv.setAdapter(adapter);
        Button button = (Button) rootView.findViewById(R.id.save_point_button);
        textView = rootView.findViewById(R.id.address_input);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onPointAdd();

                    }
                }
        );

        Button goToMapBtn = (Button) rootView.findViewById(R.id.go_to_map);
        goToMapBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Route currentRoute = CurrentRouteService.getCurrentRoute();
                        String routeName = "";
                        for (int i = 0; i < currentRoute.getPoints().size(); i++) {
                            if (i > 3) {
                                break;
                            }
                            routeName += (currentRoute.getPoints().get(i).getName() + ", ");

                        }
                        routeName = routeName.substring(0, routeName.length() - 1);
                        routeName += "...";

                        currentRoute.setName(routeName);
                        firebaseDAO.saveRouteForCurrentUser(currentRoute);
                        CurrentRouteService.
                                setCurrentRoute(new Route());

                        Log.d("mylog", "CLICKED");


                        List fragments = getFragmentManager().getFragments();

                        MapFragments mapFragments = new MapFragments();
//
//                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.replace(R.id.flMain, mapFragments);
//                        ft.commit();
                        mapFragments.setNewRoute(currentRoute);
                    }
                }
        );
        return rootView;


    }

    public void onPointAdd() {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String address = textView.getText().toString();

        String addressQuery = "";
        try {
            addressQuery = URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getActivity(), "Błąd w walidacji adresu", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if (addressQuery.length() < 3) {
            Toast.makeText(getActivity(), "Podano za krótki adres", Toast.LENGTH_SHORT).show();
            return;
        }

        // String key = "AIzaSyDhp7h_vV3XuKAOloMuG_fQMR9WE5yM12I";
        // String url ="https://maps.googleapis.com/maps/api/geocode/json?address=" + addressQuery + "&key=" + key;
        String url = "https://nominatim.openstreetmap.org/search?q=" + addressQuery + "&format=json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        JSONArray places = null;
                        try {
                            places = new JSONArray(response);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Błąd w walidacji adresu", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        if (places.length() == 0) {
                            Toast.makeText(getActivity(), "Taki adres nie istnieje!", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject result = places.getJSONObject(0);
                                onAddressValidationSuccess(result.getString("lat"), result.getString("lon"));
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "Błąd w walidacji adresu", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Błąd w walidacji adresu", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
        Log.i("SCANNER", "Chosen address: " + address);
    }


    public void onAddressValidationSuccess(String lat, String lon) {
        CurrentRouteService.addPointToCurrentRoute(new Point(textView.getText().toString(),
                "",
                lat, lon
        ));

        textView.setText("");
        adapter.notifyDataSetChanged();


    }

}
