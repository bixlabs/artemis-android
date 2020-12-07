package com.chemicalwedding.artemis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.MediaType;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

public class MediaFullScreenActivity extends Activity {

    PlayerView playerView;
    ExoPlayer player;
    private long playbackPosition;
    private boolean playWhenReady;
    private int currentWindow;
    String path;
    MediaType mediaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_full_screen);

        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        playerView = (PlayerView) findViewById(R.id.video_view);

        Bundle bundle = this.getIntent().getExtras();
        String mediaPath = bundle.getString("fullScreenMediaPath");
        String mediaTypeString = bundle.getString("fullScreenMediaType");
        path = mediaPath;

        mediaType = MediaType.valueOf(mediaTypeString);
        File mediaFile = new  File(mediaPath);
        if(mediaFile.exists()){
            if (mediaType == MediaType.PHOTO) {
                playerView.setVisibility(View.GONE);
                findViewById(R.id.imageContainer).setVisibility(View.VISIBLE);
                String filePath = mediaFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                fullScreenImageView.setMinimumHeight(bitmap.getHeight());
                fullScreenImageView.invalidate();
                Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
                fullScreenImageView.setImageBitmap(myBitmap);
            } else {
                playerView.setVisibility(View.VISIBLE);
                findViewById(R.id.imageContainer).setVisibility(View.GONE);

                initializePlayer();
            }
        }

        ((TextView) findViewById(R.id.openShotPlanButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mediaFulllScreenIntent = new Intent(MediaFullScreenActivity.this, ShotPlanActivity.class);
                mediaFulllScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                bundle.putString("fullScreenMediaPath", mediaPath);
                bundle.putString("fullScreenMediaType", mediaTypeString);

                mediaFulllScreenIntent.putExtras(bundle);
                startActivity(mediaFulllScreenIntent);
                finish();
            }
        });
    }

    private void initializePlayer() {
        if(mediaType != MediaType.PHOTO) {
            player = ExoPlayerFactory.newSimpleInstance(this);
            playerView.setPlayer(player);
            Uri uri = Uri.parse(path);
            MediaSource mediaSource = null;
            try {
                mediaSource = buildMediaSource(uri);
                player.setPlayWhenReady(playWhenReady);
                player.seekTo(currentWindow, playbackPosition);
                player.prepare(mediaSource, false, false);
            } catch (FileDataSource.FileDataSourceException e) {
                e.printStackTrace();
            }
        }
    }

    private MediaSource buildMediaSource(Uri uri) throws FileDataSource.FileDataSourceException {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        DataSpec spec = new DataSpec(uri);
        FileDataSource dataSource = new FileDataSource();
        dataSource.open(spec);
        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return dataSource;
            }
        };
        MediaSource audioSource = new ExtractorMediaSource(dataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);
        return audioSource;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null && mediaType != MediaType.PHOTO) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}
