package com.example.locify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class SharingActivity extends AppCompatActivity {

    Button select, add;
    EditText  name_playlist, link_playlist;
    TextView location;
    GoogleMap mMap;
    Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        select = (Button) findViewById(R.id.select);
        add = (Button) findViewById(R.id.add);
        name_playlist = (EditText) findViewById(R.id.name_playlist);
        link_playlist = (EditText) findViewById(R.id.link_playlist);

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("Latitude", 0.0);
        lng = intent.getDoubleExtra("Longitude", 0.0);

        location = (TextView) findViewById(R.id.location);
        location.setText("  Lat: " + String.format("%.2f", lat) + "  Log: " + String.format("%.2f", lng));


//        lat = 37.4433;
//        lng = -122.00;

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mMap = MapsActivity.getMap();

                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions markeroptions= new MarkerOptions();
                markeroptions.position(latLng);
                markeroptions.title("newPin," + name_playlist.getText() + "," + link_playlist.getText());
                markeroptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mn));
                mMap.addMarker(markeroptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

                SharingActivity.this.finish();

            }
        });



        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SharingActivity.this, MapsActivity2.class);
                startActivity(intent);
                SharingActivity.this.finish();
            }
        });

    }
}
