package com.example.dostawca;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.dostawca.dto.Point;
import com.example.dostawca.dto.Route;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragments extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnPolylineClickListener {

    public GoogleMap mMap;
    private String url, url1, url2;

    private Route route;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location locationData;

    private static final long TIME_INTERVAL_GET_LOCATION = 1000 * 5; // 1 Minute
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 5000;
    private View rootView;

    private MarkerOptions place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
    private MarkerOptions place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");
    private MarkerOptions place3 = new MarkerOptions().position(new LatLng(27.687491, 85.3218583)).title("Location 3");
    private MarkerOptions place4 = new MarkerOptions().position(new LatLng(27.697491, 85.3228583)).title("Location 4");


    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<MarkerOptions> places = new ArrayList<>();

    public Polyline currentPolyline;

    public MapFragments() {
        // Required empty public constructor
    }

    public MapFragments(Route route) {
        this.route= route;
    }

    public void setNewRoute(Route route) {
        int i = 0;
        double lat = 0;
        double lng = 0;
        for (Point point : route.getPoints()) {
            Log.d("myTag", "PUNKT " + point.getLat() + ", " + point.getLng());
            lat = new Double(point.getLat());
            lng = new Double(point.getLng());
            this.places.add(new MarkerOptions().position(new LatLng(lat, lng)).title(point.getName()));
            this.mMap.addMarker(places.get(i));
            i++;
        }

        for (i = 0; i < this.places.size(); i++) {
            if (i + 1 < this.places.size()) {
                this.urls.add(i, getUrl(places.get(i).getPosition(), places.get(i+1).getPosition(), "driving"));
                Log.d("myTag", "i: " + i);
                Log.d("myTag", "url: " + places.get(i).getPosition() + " " + places.get(i).getPosition() + " driving");
            }
        }
        for (i = 0; i < this.urls.size(); i++) {
                new FetchURL(getActivity()).execute(urls.get(i), "driving");
        }
    }

    ;

    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(5);
    //
// Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }
//        Toast.makeText(this.getContext(), "Route type" + polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    public MapFragments getInstance() {
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        if (rootView == null) {
            ((MainActivity) getActivity()).setActionBarTitle("Map");
            rootView = inflater.inflate(R.layout.fragment_home, container, false);

        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

//        url = getUrl(place1.getPosition(), place2.getPosition(), "driving");
//        url1 = getUrl(place2.getPosition(), place3.getPosition(), "driving");
//        url2 = getUrl(place3.getPosition(), place4.getPosition(), "driving");
        return rootView;
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(TIME_INTERVAL_GET_LOCATION)    // 3 seconds, in milliseconds
                    .setFastestInterval(TIME_INTERVAL_GET_LOCATION); // 1 second, in milliseconds

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
                locationChecker(mGoogleApiClient, getActivity());
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                locationData = locationResult.getLastLocation();
                // Toast.makeText(getActivity(), "Latitude: " + locationData.getLatitude() + ", Longitude: " + locationData.getLongitude(), Toast.LENGTH_SHORT).show();
                //  Log.w("==>UpdateLocation<==", "" + String.format("%.6f", locationData.getLatitude()) + "," + String.format("%.6f", locationData.getLongitude()));

                if (locationData != null) {

                    LatLng point = new LatLng(locationData.getLatitude(), locationData.getLongitude());
//                    mMap.clear();

//                    Marker marker = mMap.addMarker(new MarkerOptions().position(point).title("Your Current Location"));
//                    marker.showInfoWindow();

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(15).build();
//                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//                    LatLng point1 = new LatLng(51.736630, 19.449100);
//                    LatLng point2 = new LatLng(51.746630, 19.489100);
//                    LatLng point3 = new LatLng(51.756630, 18.59100);
//                    LatLng point4 = new LatLng(51.77, 18.7);
//                    LatLng point5 = new LatLng(52.77, 18.5);
//
//                    Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
//                            .clickable(true)
//                            .add(
//                                    point1, point2, point3, point4, point5
////                                    ,new LatLng(52.555, 19.0)
//                            ));
////                    polyline1.setTag("Route 1");
//                    onPolylineClick(polyline1);
//                    mMap.addMarker(new MarkerOptions().position(point1).title("1"));
//                    mMap.addMarker(new MarkerOptions().position(point2).title("2"));
//                    mMap.addMarker(new MarkerOptions().position(point3).title("3"));
//                    mMap.addMarker(new MarkerOptions().position(point4).title("4"));
//                    mMap.addMarker(new MarkerOptions().position(point5).title("5"));

                }

            }
        }, null);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution() && getActivity() instanceof Activity) {
            try {
                Activity activity = (Activity) getActivity();
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    public void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, 1000);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }


        });
    }


    @Override
    public void onLocationChanged(Location location) {
        //Log.w("==>UpdateLocation<==", "" + String.format("%.6f", location.getLatitude()) + "," + String.format("%.6f", location.getLongitude()));
        //locationData = location;
        //Toast.makeText(getActivity(), "Latitude: " + locationData.getLatitude() + ", Longitude: " + locationData.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setIndoorEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);

//        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(51.736630, 19.449100),
//                        new LatLng(52.736630, 20.449100),
//                        new LatLng(51.736630, 18.59100),
//                        new LatLng(51.0, 19.0),
//                        new LatLng(52.0, 19.5)));
//        polyline1.setTag("A");

        mMap.setOnPolylineClickListener(this);

//        Log.d("mylog", "Added Markers");
//        mMap.addMarker(place1);
//        mMap.addMarker(place2);
//        mMap.addMarker(place3);
//        mMap.addMarker(place4);

//        new FetchURL(getActivity()).execute(url1, "driving");
//        new FetchURL(getActivity()).execute(url2, "driving");
//        new FetchURL(getActivity()).execute(url, "driving");
        if(route!=null) {
            setNewRoute(route);
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        Log.d("mylog", "MAP FRAGMENT onTaskDone");
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
