package com.chemicalwedding.artemis;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.chemicalwedding.artemis.database.MediaType;

import java.io.File;

public class MediaFullScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_full_screen);

        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        VideoView fullScreenVideoView = findViewById(R.id.fullScreenVideoView);

        Bundle bundle = this.getIntent().getExtras();
        String mediaPath = bundle.getString("fullScreenMediaPath");
        String mediaTypeString = bundle.getString("fullScreenMediaType");

        MediaType mediaType = MediaType.valueOf(mediaTypeString);
        File mediaFile = new  File(mediaPath);
        if(mediaFile.exists()){
            if (mediaType == MediaType.PHOTO) {
                fullScreenVideoView.setVisibility(View.GONE);
                fullScreenImageView.setVisibility(View.VISIBLE);
                Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
                fullScreenImageView.setImageBitmap(myBitmap);
            } else {
                fullScreenVideoView.setVisibility(View.VISIBLE);
                fullScreenImageView.setVisibility(View.GONE);

                final MediaController mediaController = new MediaController(this);
                fullScreenVideoView.setMediaController(mediaController);

                Uri videoUri = Uri.fromFile(mediaFile);
                fullScreenVideoView.setVideoURI(videoUri);
                fullScreenVideoView.seekTo(1);
                fullScreenVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaController.show(0);
                    }
                });
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
