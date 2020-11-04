package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.bumptech.glide.Glide;
import com.chemicalwedding.artemis.utils.ArtemisFileUtils;
import com.chemicalwedding.artemis.utils.ImageUtils;

import org.jcodec.containers.mp4.boxes.MetaValue;
import org.jcodec.movtool.MetadataEditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class SaveVideoMetadataActivity extends Activity {

    protected static boolean gpsEnabled = false, sensorEnabled = false;
    private static final String logTag = "SaveVideoMetadata";
    protected Bitmap bitmapToSave;

    String mediaTypeString;
    String mediaPath;
    HashMap<String, String> metadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_video_metadata);

        Bundle bundle = this.getIntent().getExtras();
        mediaPath = bundle.getString("fullScreenMediaPath");
        mediaTypeString = bundle.getString("fullScreenMediaType");
        metadata = (HashMap) bundle.getSerializable("metadata");

        Glide.with(this)
                .load(R.raw.loading)
                .into((ImageView) findViewById(R.id.loadingIndicator));

        SharedPreferences artemisPrefs = getSharedPreferences(
                ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);
        Button cancelButton = findViewById(R.id.cancelVideoMetadata);
        Button saveTitleButton = findViewById(R.id.saveVideoTitleMetadata);
        TextView videoTitleTextView = findViewById(R.id.videoTitle);
        videoTitleTextView.setText(artemisPrefs.getString(
                ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, ""));

        saveTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVideoMetadata(artemisPrefs, true, videoTitleTextView.getText().toString());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVideoAndBack();
            }
        });

    }

    private void saveVideoMetadata(SharedPreferences artemisPrefs, Boolean withIntro, String videoTitle){
        findViewById(R.id.loadingIndicatorContainer).setVisibility(View.VISIBLE);

        HashMap<String, String> newMeta = new HashMap<>();
        String videoNotes = artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, "");
        String videoContactName = artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME, "");
        String videoContactEmail = artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL, "");
        String sunriseAndSunsetDate = artemisPrefs.getString(
                ArtemisPreferences.SAVE_PICTURE_SUNRISE_AND_SUNSET, "");

        // Metadata
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, videoTitle);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, videoNotes);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME, videoContactName);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL, videoContactEmail);

        // Video intro
        TextView titleTextView = findViewById(R.id.titleVideoMetadata);
        titleTextView.setText(videoTitle.equals("") ? "Untitled" : videoTitle);

        ArrayList<String> takenByArrayList = new ArrayList<>();
        takenByArrayList.add(videoContactName);
        takenByArrayList.add(videoContactEmail);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy 'at' h:mm aa", Locale.getDefault());
        takenByArrayList.add(sdf.format(new Date()));
        takenByArrayList.removeAll(Collections.singleton(""));

        String takenByString = TextUtils.join(" / ", takenByArrayList);
        TextView contactInfoTextView = findViewById(R.id.takenByVideoMetadata);
        contactInfoTextView.setText(takenByString);

        TextView notesTextView = findViewById(R.id.notesVideoMetadata);
        if (videoNotes.length() > 0) {
            notesTextView.setText(videoNotes);
        }

        boolean shouldSaveCameraInformation = artemisPrefs.getBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS, true);
        if(shouldSaveCameraInformation){
            // Metadata
            newMeta.put(ExifInterface.TAG_MAKE, metadata.get(ExifInterface.TAG_MAKE) );
            newMeta.put(ExifInterface.TAG_MODEL, metadata.get(ExifInterface.TAG_MODEL));

            // Video intro
            TextView cameraInfoTextView = findViewById(R.id.cameraInformationVideoMetadata);
            cameraInfoTextView.setText(ArtemisActivity._cameraDetailsText.getText().toString());
        }

        boolean shouldSaveLensInfo = artemisPrefs.getBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS, true);
        if(shouldSaveLensInfo){
            // Metadata
            newMeta.put(ExifInterface.TAG_FOCAL_LENGTH, metadata.get(ExifInterface.TAG_FOCAL_LENGTH));
            newMeta.put(ExifInterface.TAG_APERTURE, metadata.get(ExifInterface.TAG_APERTURE));

            // Video intro
            String fltext = ArtemisActivity._lensFocalLengthText.getText()
                    .toString() + "mm";
            TextView lensFocalLengthMetadataTextView = findViewById(R.id.lensFocalLengthVideoMetadata);
            lensFocalLengthMetadataTextView.setText(fltext);

            String lensMake = ArtemisActivity._lensMakeText.getText()
                    .toString();
            TextView lensMakeMetadataTextView = findViewById(R.id.lensMakeVideoMetadata);
            lensMakeMetadataTextView.setText(lensMake);
        }

        boolean showGpsCoordinates = artemisPrefs.getBoolean(
                ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION, true);
        boolean showGpsAddress = artemisPrefs.getBoolean(
                ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_ADDRESS, true);
        if(showGpsCoordinates || showGpsAddress){
            // Metadata
            metadata.put(ExifInterface.TAG_GPS_LATITUDE, metadata.get(ExifInterface.TAG_GPS_LATITUDE));
            metadata.put(ExifInterface.TAG_GPS_LATITUDE_REF, metadata.get(ExifInterface.TAG_GPS_LATITUDE_REF));
            metadata.put(ExifInterface.TAG_GPS_LONGITUDE, metadata.get(ExifInterface.TAG_GPS_LONGITUDE));
            metadata.put(ExifInterface.TAG_GPS_LONGITUDE_REF, metadata.get(ExifInterface.TAG_GPS_LONGITUDE_REF));

            // Video intro
            String[] gpsDetailsAndLocation = ArtemisActivity.getGPSLocationDetailStrings(SaveVideoMetadataActivity.this);
            TextView locationTextView = findViewById(R.id.locationVideoMetadata);
            if (showGpsCoordinates) {
                locationTextView.setText(gpsDetailsAndLocation[0]);
            }
            if (showGpsAddress) {
                locationTextView.setText(gpsDetailsAndLocation[1]);
            }
        }

        boolean shouldSaveExposureTime = artemisPrefs.getBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_EXPOSURE, true);
        if(shouldSaveExposureTime){
            // Metadata
            newMeta.put(ExifInterface.TAG_EXPOSURE_TIME, metadata.get(ExifInterface.TAG_EXPOSURE_TIME));

            // Video intro
            String exposureSeconds = metadata.containsKey(ExifInterface.TAG_EXPOSURE_TIME) ? "Exposure time: " + metadata.get(ExifInterface.TAG_EXPOSURE_TIME) : "AUTO EXPOSURE";
            TextView exposureTextView = findViewById(R.id.exposureVideoMetadata);
            exposureTextView.setText(exposureSeconds);
        }

        boolean showTiltAndDirection = artemisPrefs.getBoolean(
                ArtemisPreferences.SAVE_PICTURE_SHOW_TILT_AND_DIRECTION, true);
        if (showTiltAndDirection) {
            TextView tiltTextView = findViewById(R.id.tiltVideoMetadata);
            tiltTextView.setText(ArtemisActivity.pictureSaveHeadingTiltString);
        }

        TextView sunriseAndSunsetTextView = findViewById(R.id.sunriseAndSunsetVideoMetadata);
        sunriseAndSunsetTextView.setText("No date chosen yet");
        boolean showSunriseAndSunset = artemisPrefs.getBoolean(
                ArtemisPreferences.SAVE_PICTURE_SHOW_SUNRISE_AND_SUNSET, true);
        if (showSunriseAndSunset && sunriseAndSunsetDate.length() > 0) {
            // Metadata
            newMeta.put("sunrise and sunset", sunriseAndSunsetDate);

            // Video intro
            sunriseAndSunsetTextView.setText(sunriseAndSunsetDate);
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (withIntro) {
                    // Add 2 secs clip with metadata info
                    View pictureMetadataView = findViewById(R.id.videoMetadataIntro);
                    Log.i("MetadataView height", String.valueOf(pictureMetadataView.getHeight()));
                    Log.i("MetadataView width", String.valueOf(pictureMetadataView.getWidth()));

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(mediaPath);
                    int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    Bitmap blankBmp = getBitmapFromView(pictureMetadataView);
                    String metadataImageIntroPath = saveMetadataIntroPicture(blankBmp);
                    retriever.release();
                    String videoSize = videoWidth + ":" + videoHeight;
                    String metadataVideoIntroPath = saveMetadataIntroVideo(metadataImageIntroPath, videoSize);

                    insertIntroToVideo(metadataVideoIntroPath);
                }

                // Save preferences
                SharedPreferences.Editor editor = artemisPrefs.edit();
                editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, videoTitle);
                editor.commit();

                // Save metadata
                try {
                    MetadataEditor metadataEditor = MetadataEditor.createFrom(new File(mediaPath));
                    Map<String, MetaValue> meta = metadataEditor.getKeyedMeta();

                    for(String key: newMeta.keySet()) {
                        meta.put(key, MetaValue.createString(newMeta.get(key)));
                    }

                    metadataEditor.save(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.loadingIndicatorContainer).setVisibility(View.GONE);
                        finish();
                    }
                });
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public Bitmap getBitmapFromView(View view) {
        View rootView = getWindow().getDecorView().getRootView();
        Bitmap returnedBitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public String saveMetadataIntroPicture(Bitmap bitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss.S",
                Locale.getDefault());
        String metadataImageTitle = sdf.format(Calendar.getInstance().getTime());
        String filePath = ArtemisFileUtils.Companion.newFile(this, metadataImageTitle + ".jpg").getAbsolutePath();
        Log.v(logTag, "Saving file: " + filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(logTag, "Picture file could not be created");
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        return filePath;
    }

    public String saveMetadataIntroVideo(String metadataImageIntroPath, String videoSize) {
//        String videoIntroPath = metadataImageIntroPath.substring(0, metadataImageIntroPath.lastIndexOf(".")) + ".mp4";
        File testVideo = ArtemisFileUtils.Companion.newFile(getApplicationContext(), "metadataVideo.mp4");
        String videoIntroPath = testVideo.getPath();

        String[] cmd = {"-f", "lavfi",
                "-i", "anullsrc=channel_layout=stereo:sample_rate=44100",
                "-loop", "1", "-i", metadataImageIntroPath,
                "-pix_fmt", "yuv420p", "-t", "2",
                "-vf", "scale=" + videoSize + ",setsar=1:1,fps=fps=24",
                "-b:v", "5000k",
                videoIntroPath};
        int rc = FFmpeg.execute(cmd);


        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }

        File file = new File(metadataImageIntroPath);
        if (file.exists()) {
            file.delete();
        }

        return videoIntroPath;
    }

    public void insertIntroToVideo(String metadataVideoIntroPath) {
        String videoFileNameMetadata = mediaPath.substring(0, mediaPath.lastIndexOf("."));
        String formatString = mediaPath.substring(mediaPath.lastIndexOf("."));
        videoFileNameMetadata = videoFileNameMetadata + "_metadata" + formatString;

        String[] cmd = {"-i", metadataVideoIntroPath, "-i", mediaPath,
                "-filter_complex", "[0:v][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]",
                "-map", "[v]", "-map", "[a]",
                "-b:v", "5000k",
                videoFileNameMetadata};
        int rc = FFmpeg.execute(cmd);

        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
            File file = new File(mediaPath);
            if (file.exists()) {
                file.delete();
            }
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
        File file = new File(metadataVideoIntroPath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void onBackPressed() {
        deleteVideoAndBack();
    }

    public void deleteVideoAndBack() {
        File mediaFile = new File(mediaPath);
        new AlertDialog.Builder(SaveVideoMetadataActivity.this)
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
