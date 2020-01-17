package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.chemicalwedding.artemis.database.MediaType;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import org.jcodec.containers.mp4.boxes.MetaValue;
import org.jcodec.movtool.MetadataEditor;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SaveVideoMetadataActivity extends Activity {

    protected static boolean gpsEnabled = false, sensorEnabled = false;

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

    private void saveVideoMetadata(){
        HashMap<String, String> newMeta = new HashMap<>();

        String videoTitle = ((EditText) findViewById(R.id.videoTitle)).getText().toString();
        String videoNotes = ((EditText) findViewById(R.id.videoNotes)).getText().toString();
        String videoContactName = ((EditText) findViewById(R.id.videoContactName)).getText().toString();
        String videoContactEmail = ((EditText) findViewById(R.id.videoContactEmail)).getText().toString();

        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, videoTitle);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, videoNotes);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME, videoContactName);
        newMeta.put(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL, videoContactEmail);


        boolean shouldSaveCameraInformation = ((ToggleButton) findViewById(R.id.cameraDetailsToggle)).isChecked();
        if(shouldSaveCameraInformation){
            newMeta.put(ExifInterface.TAG_MAKE, metadata.get(ExifInterface.TAG_MAKE) );
            newMeta.put(ExifInterface.TAG_MODEL, metadata.get(ExifInterface.TAG_MODEL));
        }

        boolean shouldSaveLensInfo = ((ToggleButton) findViewById(R.id.lensDetailsToggle)).isChecked();
        if(shouldSaveLensInfo){
            newMeta.put(ExifInterface.TAG_FOCAL_LENGTH, metadata.get(ExifInterface.TAG_FOCAL_LENGTH));
            newMeta.put(ExifInterface.TAG_APERTURE, metadata.get(ExifInterface.TAG_APERTURE));
        }

        boolean shouldSaveGpsInformation = ((ToggleButton) findViewById(R.id.gpsCoordinatesToggle)).isChecked();
        if(shouldSaveGpsInformation){
            metadata.put(ExifInterface.TAG_GPS_LATITUDE, metadata.get(ExifInterface.TAG_GPS_LATITUDE));
            metadata.put(ExifInterface.TAG_GPS_LATITUDE_REF, metadata.get(ExifInterface.TAG_GPS_LATITUDE_REF));
            metadata.put(ExifInterface.TAG_GPS_LONGITUDE, metadata.get(ExifInterface.TAG_GPS_LONGITUDE));
            metadata.put(ExifInterface.TAG_GPS_LONGITUDE_REF, metadata.get(ExifInterface.TAG_GPS_LONGITUDE_REF));
        }

        boolean shouldSaveExposureTime = ((ToggleButton) findViewById(R.id.exposure_toggle)).isChecked();
        if(shouldSaveExposureTime){
            newMeta.put(ExifInterface.TAG_EXPOSURE_TIME, metadata.get(ExifInterface.TAG_EXPOSURE_TIME));
        }

        try {
            MetadataEditor editor = MetadataEditor.createFrom(new File(mediaPath));
            Map<String, MetaValue> meta = editor.getKeyedMeta();

            for(String key: newMeta.keySet()) {
                meta.put(key, MetaValue.createString(newMeta.get(key)));
            }

            editor.save(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
