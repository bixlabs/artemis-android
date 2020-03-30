package com.chemicalwedding.artemis;

import android.app.Activity;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

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

        loadVideoDetailsSettings();

        Button cancelButton = findViewById(R.id.cancelVideoMetadata);
        Button saveButton = findViewById(R.id.saveVideoMetadata);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File mediaFile = new File(mediaPath);
                if(mediaFile.exists()) {
                    mediaFile.delete();
                }

                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVideoMetadata();
                finish();
            }
        });
    }

    private void loadVideoDetailsSettings() {
        SharedPreferences artemisPrefs = getSharedPreferences(
                ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);

        ((EditText) findViewById(R.id.videoTitle))
                .setText(artemisPrefs.getString(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, ""));
        ((EditText) findViewById(R.id.videoNotes)).setText(artemisPrefs
                .getString(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, ""));
        ((EditText) findViewById(R.id.videoContactName)).setText(artemisPrefs
                .getString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME, ""));
        ((EditText) findViewById(R.id.videoContactEmail)).setText(artemisPrefs
                .getString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL, ""));
        ((ToggleButton) findViewById(R.id.cameraDetailsToggle))
                .setChecked(artemisPrefs.getBoolean(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS,
                        true));
        ((ToggleButton) findViewById(R.id.lensDetailsToggle))
                .setChecked(artemisPrefs
                        .getBoolean(
                                ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS,
                                true));
        ((ToggleButton) findViewById(R.id.gpsCoordinatesToggle))
                .setChecked(artemisPrefs.getBoolean(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION, true));
        ((ToggleButton) findViewById(R.id.exposure_toggle))
                .setChecked(artemisPrefs.getBoolean(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_EXPOSURE, true));
    }

    private void saveVideoMetadata(){
        HashMap<String, String> newMeta = new HashMap<>();

        String videoTitle = ((EditText) findViewById(R.id.videoTitle)).getText().toString();
        String videoNotes = ((EditText) findViewById(R.id.videoNotes)).getText().toString();
        String videoContactName = ((EditText) findViewById(R.id.videoContactName)).getText().toString();
        String videoContactEmail = ((EditText) findViewById(R.id.videoContactEmail)).getText().toString();

        // Metadata
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, videoTitle);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, videoNotes);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME, videoContactName);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL, videoContactEmail);

        // Video intro
        TextView videoTitleTextView = findViewById(R.id.titleVideoMetadata);
        videoTitleTextView.setText(videoTitle);

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

        boolean shouldSaveCameraInformation = ((ToggleButton) findViewById(R.id.cameraDetailsToggle)).isChecked();
        if(shouldSaveCameraInformation){
            // Metadata
            newMeta.put(ExifInterface.TAG_MAKE, metadata.get(ExifInterface.TAG_MAKE) );
            newMeta.put(ExifInterface.TAG_MODEL, metadata.get(ExifInterface.TAG_MODEL));

            // Video intro
            TextView cameraInfoTextView = findViewById(R.id.cameraInformationVideoMetadata);
            cameraInfoTextView.setText(ArtemisActivity._cameraDetailsText.getText().toString());
        }

        boolean shouldSaveLensInfo = ((ToggleButton) findViewById(R.id.lensDetailsToggle)).isChecked();
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

        boolean shouldSaveGpsInformation = ((ToggleButton) findViewById(R.id.gpsCoordinatesToggle)).isChecked();
        if(shouldSaveGpsInformation){
            // Metadata
            metadata.put(ExifInterface.TAG_GPS_LATITUDE, metadata.get(ExifInterface.TAG_GPS_LATITUDE));
            metadata.put(ExifInterface.TAG_GPS_LATITUDE_REF, metadata.get(ExifInterface.TAG_GPS_LATITUDE_REF));
            metadata.put(ExifInterface.TAG_GPS_LONGITUDE, metadata.get(ExifInterface.TAG_GPS_LONGITUDE));
            metadata.put(ExifInterface.TAG_GPS_LONGITUDE_REF, metadata.get(ExifInterface.TAG_GPS_LONGITUDE_REF));

            // Video intro
            String[] gpsDetailsAndLocation = ArtemisActivity.getGPSLocationDetailStrings(this);
            TextView locationTextView = findViewById(R.id.locationVideoMetadata);
            locationTextView.setText(gpsDetailsAndLocation[0]);
        }

        boolean shouldSaveExposureTime = ((ToggleButton) findViewById(R.id.exposure_toggle)).isChecked();
        if(shouldSaveExposureTime){
            // Metadata
            newMeta.put(ExifInterface.TAG_EXPOSURE_TIME, metadata.get(ExifInterface.TAG_EXPOSURE_TIME));

            // Video intro
            String exposureSeconds = "Exposure time: " + metadata.get(ExifInterface.TAG_EXPOSURE_TIME);
            TextView exposureTextView = findViewById(R.id.exposureVideoMetadata);
            exposureTextView.setText(exposureSeconds);
        }

        if (!videoTitleTextView.getText().equals("")) {
            // Add 2 secs clip with metadata info
            View pictureMetadataView = findViewById(R.id.videoMetadataIntro);
            Log.i("MetadataView height", String.valueOf(pictureMetadataView.getHeight()));
            Log.i("MetadataView width", String.valueOf(pictureMetadataView.getWidth()));
            Bitmap blankBmp = getBitmapFromView(pictureMetadataView);
            String metadataImageIntroPath = saveMetadataIntroPicture(blankBmp);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mediaPath);
            int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            retriever.release();
            String videoSize = videoWidth + ":" + videoHeight;
            String metadataVideoIntroPath = saveMetadataIntroVideo(metadataImageIntroPath, videoSize);

            insertIntroToVideo(metadataVideoIntroPath);
        }

        // Save preferences
        SharedPreferences.Editor editor = getApplication().getSharedPreferences(
                ArtemisPreferences.class.getSimpleName(),
                Context.MODE_PRIVATE).edit();
        editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, videoTitle);
        editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, videoNotes);
        editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME, videoContactName);
        editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL, videoContactEmail);
        editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS, shouldSaveCameraInformation);
        editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS, shouldSaveLensInfo);
        editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION, shouldSaveGpsInformation);
        editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_EXPOSURE, shouldSaveExposureTime);
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
        String filePath = ArtemisActivity.savePictureFolder + "/" + metadataImageTitle
                + ".jpg";
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
        String videoIntroPath = metadataImageIntroPath.substring(0, metadataImageIntroPath.lastIndexOf(".")) + ".mp4";

        String[] cmd = {"-f", "lavfi",
                "-i", "anullsrc=channel_layout=stereo:sample_rate=44100",
                "-loop", "1", "-i", metadataImageIntroPath,
                "-pix_fmt", "yuv420p", "-t", "2",
                "-vf", "scale=" + videoSize + ",setsar=1:1,fps=fps=25", videoIntroPath};
        int rc = FFmpeg.execute(cmd);


        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
            File file = new File(metadataImageIntroPath);
            if (file.exists()) {
                file.delete();
            }
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }

        return videoIntroPath;
    }

    public void insertIntroToVideo(String metadataVideoIntroPath) {
        String videoFileNameMetadata = mediaPath.substring(0, mediaPath.lastIndexOf("."));
        String formatString = mediaPath.substring(mediaPath.lastIndexOf("."));
        videoFileNameMetadata = videoFileNameMetadata + "_metadata" + formatString;

        String[] cmd = {"-i", metadataVideoIntroPath, "-i", mediaPath,
                "-filter_complex", "[0:v][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]",
                "-map", "[v]", "-map", "[a]", videoFileNameMetadata};
        int rc = FFmpeg.execute(cmd);

        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
            File file = new File(mediaPath);
            if (file.exists()) {
                file.delete();
            }
            file = new File(metadataVideoIntroPath);
            if (file.exists()) {
                file.delete();
            }
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
    }

}
