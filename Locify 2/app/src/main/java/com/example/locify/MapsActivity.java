package com.example.locify;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnMarkerClickListener {

    static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Location lastLocation;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int REQUEST_CODE = 101;
    private static final String TAG = "Locify";
    boolean locationPermissionGranted;
    public static Context appContext;

    double currLatitude = -34;
    double currLongitude = 151;


    ImageView addPlaylists;
    String pinPlaylists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        pinPlaylists = "";

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

        addPlaylists = (ImageView) findViewById(R.id.addPlaylists);
        addPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this
                        , SharingActivity.class);

                startActivity(intent);
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
                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mn));
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
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

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
        markerOptions1.title("f1");
        //markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.mn));
        mMap.addMarker(markerOptions1);

        //fake pin 2
        LatLng memorialLatLng = new LatLng(37.872250, -122.251358);
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(memorialLatLng);
        markerOptions2.title("f2");
        markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.mn));
        mMap.addMarker(markerOptions2);

        //fake pin 3
        LatLng fakeLatLng = new LatLng(37.4433, -122.0975);
        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(fakeLatLng);
        markerOptions3.title("f3");
        markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.mn));
        mMap.addMarker(markerOptions3);

        LatLng fakeLatLngHW1 = new LatLng(21.306944, -157.858337);
        MarkerOptions markerOptions4 = new MarkerOptions();
        markerOptions4.position(fakeLatLngHW1);
        markerOptions4.title("hw1");
        markerOptions4.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions4);

        LatLng fakeLatLngHW2 = new LatLng(21.316173, -157.847099);
        MarkerOptions markerOptions5 = new MarkerOptions();
        markerOptions5.position(fakeLatLngHW2);
        markerOptions5.title("hw2");
        markerOptions5.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions5);

        LatLng fakeLatLngHW3 = new LatLng(21.301015, -157.839031);
        MarkerOptions markerOptions6 = new MarkerOptions();
        markerOptions6.position(fakeLatLngHW3);
        markerOptions6.title("hw3");
        markerOptions6.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions6);

        LatLng fakeLatLngHW4 = new LatLng(21.316528, -157.868042);
        MarkerOptions markerOptions7 = new MarkerOptions();
        markerOptions7.position(fakeLatLngHW4);
        markerOptions7.title("hw4");
        markerOptions7.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions7);

        LatLng fakeLatLngHW5 = new LatLng(21.305938, -157.879028);
        MarkerOptions markerOptions8 = new MarkerOptions();
        markerOptions8.position(fakeLatLngHW5);
        markerOptions8.title("hw5");
        markerOptions8.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions8);

        LatLng fakeLatLngHW6 = new LatLng(21.337156, -157.851048);
        MarkerOptions markerOptions9 = new MarkerOptions();
        markerOptions9.position(fakeLatLngHW6);
        markerOptions9.title("hw6");
        markerOptions9.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions9);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //ViewGroup viewGroup = findViewById(android.R.id.content);
        //View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.customview, viewGroup, false);
        //builder.setView(dialogView);
        //AlertDialog alertDialog = builder.create();
        //alertDialog.show();
        openDialog(marker);
        return true;
    }

//    private void openDialog(){
//        final Dialog dialog = new Dialog(this,R.style.DialogTheme);
//        View view = View.inflate(this,R.layout.bottomdialog_layout,null);
//        dialog.setContentView(view);
//
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        dialog.show();
//
//    }

    private void openDialog(Marker marker){
        String title = marker.getTitle();

        pinPlaylists = title;
        System.out.println(title);

        if(title.split(",")[0].equals("newPin")){
            String playlistname = title.split(",")[1];
            String playlistlink = title.split(",")[2];
            final Dialog dialog = new Dialog(this,R.style.DialogTheme);
            View view = View.inflate(this,R.layout.bottomdialog_layout,null);
            dialog.setContentView(view);

            TextView tv1 = (TextView) dialog.findViewById(R.id.playlist_name1);
            tv1.setText(playlistname);
            TextView tv2 = (TextView) dialog.findViewById(R.id.playlist_byWhom1);
            tv2.setText(playlistlink);
            tv2.setTextColor(getResources().getColor(R.color.colorAccent));
            TextView tv3 = (TextView) dialog.findViewById(R.id.playlist_like1);
            tv3.setText("0");

            TextView zz21 = (TextView) dialog.findViewById(R.id.playlist_name2);
            zz21.setText("");
            TextView zz22 = (TextView) dialog.findViewById(R.id.playlist_byWhom2);
            zz22.setText("");
            TextView zz23 = (TextView) dialog.findViewById(R.id.playlist_like2);
            zz23.setText("");

            TextView zz31 = (TextView) dialog.findViewById(R.id.playlist_name3);
            zz31.setText("");
            TextView zz32 = (TextView) dialog.findViewById(R.id.playlist_byWhom3);
            zz32.setText("");
            TextView zz33 = (TextView) dialog.findViewById(R.id.playlist_like3);
            zz33.setText("");

            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.show();
        }
        else {
            final Dialog dialog = new Dialog(this,R.style.DialogTheme);
            View view = View.inflate(this,R.layout.bottomdialog_layout,null);
            dialog.setContentView(view);

            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.show();
        }

    }

    public void onBottomDialogClick(View view){
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("Playlists",pinPlaylists);
        startActivity(intent);
    }

    public static Context getAppContext() {return appContext;}

    public static GoogleMap getMap() {return mMap;}
}
