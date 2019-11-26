package com.example.locify;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnMarkerClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Location lastLocation;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int REQUEST_CODE = 101;
    boolean locationPermissionGranted;
    public static Context appContext;

    double currLatitude = -34;
    double currLongitude = 151;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        appContext = getApplicationContext();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation();
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //LatLng USA = new LatLng(37.0902, -95.7129);
        //mMap.addMarker(new MarkerOptions().position(USA).title("USA"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(USA, 3));

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        mMap.setOnMarkerClickListener(this);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastLocation = (Location) task.getResult();
                            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                            //set marker
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng).title("I am here!");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            mMap.addMarker(markerOptions);

                            currLatitude = lastLocation.getLatitude();
                            currLongitude = lastLocation.getLongitude();
                        } else {
                            Log.d("MapsActivity", "Current location is null. Using defaults.");
                            Log.e("Exception", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                        displayPins();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastLocation = null;
                getLocationPermission();
            }
            displayPins();
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void searchLocation() {
        EditText addressField = (EditText) findViewById(R.id.location_search);
        String address = addressField.getText().toString();

        List<Address> addressList = null;
        MarkerOptions userMarkerOptions = new MarkerOptions();


        if (!TextUtils.isEmpty(address)) {
            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(address, 1);
                if (addressList != null) {


                    for (int i = 0; i < addressList.size(); i++) {
                        Address userAddress = addressList.get(i);
                        LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                        userMarkerOptions.position(latLng).title("I am here!");
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                        currLatitude = userAddress.getLatitude();
                        currLongitude = userAddress.getLongitude();

                    }
                } else {
                    Toast.makeText(this, "Location not found.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Write a location name...", Toast.LENGTH_SHORT).show();
        }

    }
    private void getCountry() {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
            String country = addresses.get(0).getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void displayPins() {
        //fake pin 1
        LatLng zellerbachLatLng = new LatLng(37.869262, -122.260474);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(zellerbachLatLng);
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions1);

        //fake pin 2
        LatLng memorialLatLng = new LatLng(37.872250, -122.251358);
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(memorialLatLng);
        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions2);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //ViewGroup viewGroup = findViewById(android.R.id.content);
        //View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.customview, viewGroup, false);
        //builder.setView(dialogView);
        //AlertDialog alertDialog = builder.create();
        //alertDialog.show();
        openDialog();
        return true;
    }

    public void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public static Context getAppContext() {return appContext;}
}
