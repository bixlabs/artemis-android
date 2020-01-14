package com.chemicalwedding.artemis;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Rational;
import android.view.View;
import android.widget.Button;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class SaveVideoMetadataActivity extends Activity {


    private Integer lastPictureISOValue_;

    private Long lastPictureExposureTime_;

    private Float lastPictureLensAperture_;

    private static ArtemisMath _artemisMath = ArtemisMath.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_video_metadata);

        Button cancelButton = findViewById(R.id.cancelVideoMetadata);
        Button saveButton = findViewById(R.id.saveVideoMetadata);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Save video metadata here
//        try {
//            MetadataEditor editor = MetadataEditor.createFrom(new File(videoFileName));
//            Map<String, MetaValue> meta = editor.getKeyedMeta();
//
//            Map<String, String> cameraMeta = buildMetadataAttributes();
//
//            for(String key: cameraMeta.keySet()) {
//                meta.put(key, MetaValue.createString(cameraMeta.get(key)));
//            }
//
//            editor.save(false);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
            }
        });
    }
    private Map<String, String> buildMetadataAttributes() {

        SharedPreferences artemisPrefs = getApplicationContext()
                .getSharedPreferences(ArtemisPreferences.class.getSimpleName(),
                        MODE_PRIVATE);

        boolean showGpsCoordinates = artemisPrefs.getBoolean(
                ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION, true);
        boolean showGpsAddress = artemisPrefs.getBoolean(
                ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_ADDRESS, true);

        boolean showGps = showGpsCoordinates || showGpsAddress;

        Map<String, String> metadata = new HashMap<>();

        if (showGps && ArtemisActivity.pictureSaveLocation != null) {
            String latString = makeLatLongString(ArtemisActivity.pictureSaveLocation
                    .getLatitude());
            String latRefString = makeLatStringRef(ArtemisActivity.pictureSaveLocation
                    .getLatitude());
            String longString = makeLatLongString(ArtemisActivity.pictureSaveLocation
                    .getLongitude());
            String longRefString = makeLonStringRef(ArtemisActivity.pictureSaveLocation
                    .getLongitude());

            metadata.put(ExifInterface.TAG_GPS_LATITUDE, latString);
            metadata.put(ExifInterface.TAG_GPS_LATITUDE_REF, latRefString);
            metadata.put(ExifInterface.TAG_GPS_LONGITUDE, longString);
            metadata.put(ExifInterface.TAG_GPS_LONGITUDE_REF, longRefString);
        }

        metadata.put("title", artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, ""));
        metadata.put("author", artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME, ""));
        metadata.put("contact", artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL, ""));
        metadata.put("notes", artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, ""));
        metadata.put("sunrise and sunset", artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_SUNRISE_AND_SUNSET, ""));

        String focalLength = Rational
                .parseRational(_artemisMath.get_selectedLensFocalLength() + "/1")
                .toString();
        metadata.put(ExifInterface.TAG_FOCAL_LENGTH, focalLength);
        metadata.put(ExifInterface.TAG_MODEL, ArtemisActivity._lensMakeText.getText().toString());
        metadata.put(ExifInterface.TAG_MAKE, ArtemisActivity._cameraDetailsText.getText().toString());

        NumberFormat numFormat = NumberFormat.getInstance();
        if (this.lastPictureISOValue_ != null) {
            metadata.put(ExifInterface.TAG_ISO, this.lastPictureISOValue_.toString());
        }
        if (this.lastPictureExposureTime_ != null) {
            String exposureSeconds = numFormat.format(this.lastPictureExposureTime_ / 1000000000f);
            metadata.put(ExifInterface.TAG_EXPOSURE_TIME, exposureSeconds);
        }
        if (this.lastPictureLensAperture_ != null) {
            metadata.put(ExifInterface.TAG_APERTURE, numFormat.format(this.lastPictureLensAperture_));
        }

        return metadata;
    }

    public static String makeLatLongString(double d) {
        d = Math.abs(d);

        int degrees = (int) d;

        double remainder = d - degrees;
        int minutes = (int) (remainder * 60D);
        // really seconds * 1000
        int seconds = (int) (((remainder * 60D) - minutes) * 60D * 1000D);

        String retVal = degrees + "/1," + minutes + "/1," + seconds + "/1000";
        return retVal;
    }

    public static String makeLatStringRef(double lat) {
        return lat >= 0D ? "N" : "S";
    }

    public static String makeLonStringRef(double lon) {
        return lon >= 0D ? "E" : "W";
    }
}
