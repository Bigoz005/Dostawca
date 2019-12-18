package com.example.dostawca;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dostawca.dto.Point;
import com.example.dostawca.service.CurrentRouteService;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pub.devrel.easypermissions.EasyPermissions;

public class QRScannerFragment extends Fragment implements View.OnClickListener {
    private static final int CAMERA_REQUEST_CODE =100;
    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;
    Button buttonTakePhoto1;

    public QRScannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EasyPermissions.requestPermissions(
                this,
                "Camera",
                CAMERA_REQUEST_CODE,
                Manifest.permission.CAMERA );
        ((MainActivity)getActivity()).setActionBarTitle("QR Scanner");
        View view = null;
         view = inflater.inflate(R.layout.fragment_qrscanner, container, false);

        // Inflate the layout for this fragment
        surfaceView = (SurfaceView) view.findViewById(R.id.camerapreview);
        buttonTakePhoto1 = (Button) view.findViewById(R.id.buttonTakePhoto1);
        textView = view.findViewById(R.id.QRText);


        barcodeDetector = new BarcodeDetector.Builder(getActivity().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(getActivity().getApplicationContext(),barcodeDetector)
                .setRequestedPreviewSize(640,480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //
                // if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)

                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if(qrCodes.size()!=0){
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(qrCodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

        buttonTakePhoto1.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonTakePhoto1:
                onAddressConfirm();
                break;
        }
    }

    public void onAddressConfirm() {
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
        Toast.makeText(getActivity(), "Lat: " + lat + " Lon: " + lon, Toast.LENGTH_SHORT).show();
        CurrentRouteService.addPointToCurrentRoute(new Point(textView.getText().toString(),
                "",
                lat, lon
        ));

    }


}