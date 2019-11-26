package com.example.locify;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ExampleDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String alert1 = "www.youtube.com/link1";
        String alert2 = "www.youtube.com/link2";
        String alert3 = "www.youtube.com/link3";
        //alertDialog.setMessage(alert1 +"\n"+ alert2 +"\n"+ alert3);
        builder.setTitle("Playlists:")
                .setMessage(alert1 +"\n"+ alert2 +"\n"+ alert3)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MapsActivity.getAppContext(), PlaylistActivity.class);
                        startActivity(intent);
                    }
                });

        return builder.create();
    }
}