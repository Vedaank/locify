package com.example.locify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.locify.R;

public class PlaylistActivity extends AppCompatActivity {

    ImageView home, add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        home = (ImageView) findViewById(R.id.homepage);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaylistActivity.this.finish();
            }
        });

        add = (ImageView) findViewById(R.id.addP);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaylistActivity.this
                        , SharingActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String title = intent.getStringExtra("Playlists");

        if(title.split(",")[0].equals("newPin")){
            String playlistname = title.split(",")[1];
            String playlistlink = title.split(",")[2];

            TextView tv1 = (TextView) this.findViewById(R.id.playlist_name1);
            tv1.setText(playlistname);
            TextView tv2 = (TextView) this.findViewById(R.id.playlist_byWhom1);
            tv2.setTextColor(getResources().getColor(R.color.colorAccent));
            tv2.setText(playlistlink);

            TextView tv3 = (TextView) this.findViewById(R.id.playlist_like1);
            tv3.setText("0");

            TextView zz21 = (TextView) this.findViewById(R.id.playlist_name2);
            zz21.setText("");
            TextView zz22 = (TextView) this.findViewById(R.id.playlist_byWhom2);
            zz22.setText("");
            TextView zz23 = (TextView) this.findViewById(R.id.playlist_like2);
            zz23.setText("");

            TextView zz31 = (TextView) this.findViewById(R.id.playlist_name3);
            zz31.setText("");
            TextView zz32 = (TextView) this.findViewById(R.id.playlist_byWhom3);
            zz32.setText("");
            TextView zz33 = (TextView) this.findViewById(R.id.playlist_like3);
            zz33.setText("");

            TextView zz41 = (TextView) this.findViewById(R.id.playlist_name4);
            zz41.setText("");
            TextView zz42 = (TextView) this.findViewById(R.id.playlist_byWhom4);
            zz42.setText("");
            TextView zz43 = (TextView) this.findViewById(R.id.playlist_like4);
            zz43.setText("");

            TextView zz51 = (TextView) this.findViewById(R.id.playlist_name5);
            zz51.setText("");
            TextView zz52 = (TextView) this.findViewById(R.id.playlist_byWhom5);
            zz52.setText("");
            TextView zz53 = (TextView) this.findViewById(R.id.playlist_like5);
            zz53.setText("");

            TextView zz61 = (TextView) this.findViewById(R.id.playlist_name6);
            zz61.setText("");
            TextView zz62 = (TextView) this.findViewById(R.id.playlist_byWhom6);
            zz62.setText("");
            TextView zz63 = (TextView) this.findViewById(R.id.playlist_like6);
            zz63.setText("");

            TextView zz71 = (TextView) this.findViewById(R.id.playlist_name7);
            zz71.setText("");
            TextView zz72 = (TextView) this.findViewById(R.id.playlist_byWhom7);
            zz72.setText("");
            TextView zz73 = (TextView) this.findViewById(R.id.playlist_like7);
            zz73.setText("");

            TextView zz81 = (TextView) this.findViewById(R.id.playlist_name8);
            zz81.setText("");
            TextView zz82 = (TextView) this.findViewById(R.id.playlist_byWhom8);
            zz82.setText("");
            TextView zz83 = (TextView) this.findViewById(R.id.playlist_like8);
            zz83.setText("");
        }

    }
}
