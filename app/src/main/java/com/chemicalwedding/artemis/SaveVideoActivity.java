package com.chemicalwedding.artemis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.chemicalwedding.artemis.database.MediaType;
import com.chemicalwedding.artemis.utils.ArtemisFileUtils;
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

import org.jcodec.containers.mp4.boxes.MetaValue;
import org.jcodec.movtool.MetadataEditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
<<<<<<< HEAD
=======
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
<<<<<<< HEAD
>>>>>>> 5e1520f (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
=======
>>>>>>> d36402c (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class SaveVideoActivity extends Activity {

    Button cancelButton;
    PlayerView playerView;
    ExoPlayer player;
    private long playbackPosition;
    private boolean playWhenReady;
    private int currentWindow;
    String path;
    String mediaTypeString;
    String mediaPath;
    HashMap<String, String> metadata;
    private static final String logTag = "SaveVideoActivity";
    private File mediaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_video);

//        VideoView videoView = findViewById(R.id.videoView);

        playerView = findViewById(R.id.video_view);
        Bundle bundle = this.getIntent().getExtras();
        mediaPath = bundle.getString("fullScreenMediaPath");
        mediaTypeString = bundle.getString("fullScreenMediaType");
        metadata = (HashMap) bundle.getSerializable("metadata");

        MediaType mediaType = MediaType.valueOf(mediaTypeString);
        mediaFile = new File(mediaPath);

        if(mediaFile.exists()){
            final MediaController mediaController = new MediaController(this);
            path = mediaPath;
            initializePlayer();

            cancelButton = findViewById(R.id.saveVideoCancelButton);
            Button saveButton = findViewById(R.id.saveVideoButton);

            Button editAndSaveButton = findViewById(R.id.saveVideoAndEditButton);

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
<<<<<<< HEAD
<<<<<<< HEAD
                    try {
                        MetadataEditor editor = MetadataEditor.createFrom(new File(path));
                        Map<String, MetaValue> meta = editor.getKeyedMeta();

                        for(String key: metadata.keySet()) {
                            meta.put(key, MetaValue.createString(metadata.get(key)));
                        }

                        editor.save(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
=======
                    SharedPreferences artemisPrefs = getSharedPreferences(
                            ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);
                    restoreVideo();
>>>>>>> 5e1520f (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
=======
                    SharedPreferences artemisPrefs = getSharedPreferences(
                            ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);
                    restoreVideo();
>>>>>>> d36402c (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
                    finish();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteVideoAndBack();
                }
            });

            editAndSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent videoMetadataIntent = new Intent(SaveVideoActivity.this, SaveVideoMetadataActivity.class);
                    videoMetadataIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    Bundle bundle = new Bundle();
                    bundle.putString("fullScreenMediaPath", mediaFile.getPath());
                    bundle.putString("fullScreenMediaType", mediaTypeString);
                    bundle.putSerializable("metadata", metadata);

                    videoMetadataIntent.putExtras(bundle);
                    startActivity(videoMetadataIntent);
                    finish();
                }
            });
        }

    }

    private void initializePlayer() {
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
    protected void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
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
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    public void restoreVideo() {
        String fileName = mediaPath.substring(0, mediaPath.lastIndexOf("."));
        String fileExt = mediaPath.substring(mediaPath.lastIndexOf("."));
        String[] cmd = { "-i", mediaPath, "-c", "copy", fileName + "_output_" + fileExt };
        int rc = FFmpeg.execute(cmd);

        if(rc == RETURN_CODE_SUCCESS) {
            File file = new File(mediaPath);
            if(file.exists()) file.delete();
        } else {
            Log.e("TAG", "test");
        }
    }

    @Override
    public void onBackPressed() {
        deleteVideoAndBack();
    }

    public void deleteVideoAndBack() {
        new AlertDialog.Builder(SaveVideoActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete file") // TODO - use string resources
                .setMessage("Are you sure you want to discard the video?") // TODO - use string resources
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mediaFile.exists()) mediaFile.delete();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}

