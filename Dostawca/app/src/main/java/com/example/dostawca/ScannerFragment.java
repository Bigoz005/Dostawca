package com.example.dostawca;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dostawca.dto.Point;
import com.example.dostawca.service.CurrentRouteService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class ScannerFragment extends Fragment implements View.OnClickListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;

    EditText editText;
    Button buttonTakePhoto;
    Button buttonConfirm;
    ImageView imageView;
    File outFile;

    Uri image;
    String imageurl;

    public ScannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Scanner");
        // Inflate the layout for this fragment
        View view = null;
        view = inflater.inflate(R.layout.fragment_scanner, container, false);
        buttonTakePhoto = (Button) view.findViewById(R.id.buttonTakePhoto);
        editText = (EditText) view.findViewById(R.id.editText);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        buttonConfirm = (Button) view.findViewById(R.id.buttonConfirm);

        buttonTakePhoto.setOnClickListener(this);
        buttonConfirm.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonTakePhoto:
                takePhoto();
                break;
            case R.id.buttonConfirm:
                onAddressConfirm();
                break;
        }
    }

    public void onAddressConfirm() {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        String address = editText.getText().toString();
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
        CurrentRouteService.addPointToCurrentRoute(new Point(editText.getText().toString(),
                "",
                lat, lon
        ));

    }

    public void takePhoto() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(Environment.getExternalStorageDirectory(), "/dostawca" + "/photo_" + timeStamp + ".png");
        image = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void parseAddress(ArrayList<String> blocks) {
        String result = "";
        for (int i = 0; i < blocks.size(); i++) {
            result += blocks.get(i);
        }
        editText.setText(result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), image);

                    imageView.setImageBitmap(thumbnail);
                    imageView.setRotation(90);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                FirebaseVisionImage firebaseVisionImage = null;
                try {
                    firebaseVisionImage = FirebaseVisionImage.fromFilePath(getActivity(), image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                Task<FirebaseVisionText> result =
                        detector.processImage(firebaseVisionImage)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                        ArrayList<String> result = new ArrayList<>();
                                        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                            String blockText = block.getText();
                                            blockText = Normalizer.normalize(blockText, Normalizer.Form.NFD);
                                            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
                                            blockText = pattern.matcher(blockText).replaceAll("");
                                            result.add(blockText);
                                            Log.i("TEXT", blockText);
                                        }
                                        parseAddress(result);
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Text recognition error", Toast.LENGTH_SHORT).show();
                                            }
                                        });

            }
        }
    }


}