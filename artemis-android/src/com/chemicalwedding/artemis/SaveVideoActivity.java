package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.chemicalwedding.artemis.database.MediaType;

import java.io.File;

public class SaveVideoActivity extends Activity {

    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_video);

        VideoView videoView = findViewById(R.id.videoView);

        Bundle bundle = this.getIntent().getExtras();
        String mediaPath = bundle.getString("fullScreenMediaPath");
        String mediaTypeString = bundle.getString("fullScreenMediaType");

        MediaType mediaType = MediaType.valueOf(mediaTypeString);
        File mediaFile = new File(mediaPath);

        if(mediaFile.exists()){
            final MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);

            Uri videoUri = Uri.fromFile(mediaFile);
            videoView.setVideoURI(videoUri);
            videoView.seekTo(1);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaController.show(0);
                }
            });

            cancelButton = findViewById(R.id.saveVideoCancelButton);
            Button saveButton = findViewById(R.id.saveVideoButton);

            Button editAndSaveButton = findViewById(R.id.saveVideoAndEditButton);

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(SaveVideoActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete file") // TODO - use string resources
                            .setMessage("Are you sure you want to discard the video?") // TODO - use string resources
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mediaFile.delete();
                                    finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            editAndSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent videoMetadataIntent = new Intent(SaveVideoActivity.this, SaveVideoMetadataActivity.class);
                    videoMetadataIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(videoMetadataIntent);
                }
            });
        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }

    @Override
    public void onBackPressed() {
        cancelButton.performClick();
    }
}

