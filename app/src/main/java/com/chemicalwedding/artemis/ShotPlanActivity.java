package com.chemicalwedding.artemis;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.chemicalwedding.artemis.database.MediaType;
import com.chemicalwedding.artemis.model.Shotplan;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.Date;

public class ShotPlanActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int CHANGE_LOCATION_REQUEST = 1001;
    private GoogleMap mMap;
    String path;
    MediaType mediaType;
    Shotplan shotplan;
    private ArtemisDatabaseHelper _artemisDbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shot_plan_activity_layout);

        _artemisDbHelper = new ArtemisDatabaseHelper(this);

        ImageView fullScreenImageView = findViewById(R.id.image_view);

        Bundle bundle = this.getIntent().getExtras();
        String mediaPath = bundle.getString("fullScreenMediaPath");
        String mediaTypeString = bundle.getString("fullScreenMediaType");
        path = mediaPath;

        mediaType = MediaType.valueOf(mediaTypeString);
        File mediaFile = new  File(mediaPath);
        if(mediaFile.exists()){
            if (mediaType == MediaType.PHOTO) {
                String filePath = mediaFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                fullScreenImageView.setMinimumHeight(bitmap.getHeight());
                fullScreenImageView.invalidate();
                Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
                fullScreenImageView.setImageBitmap(myBitmap);
            } else {
                try {
                    Bitmap bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(mediaFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    fullScreenImageView.setImageBitmap(bitmapThumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        shotplan = loadShotPlanForPath(mediaPath);

        ((EditText) findViewById(R.id.txt_title)).setText(shotplan.getTitle());
        ((EditText) findViewById(R.id.txt_notes)).setText(shotplan.getNotes());
        ((TextView) findViewById(R.id.txt_latitude)).setText(String.format("%.2f", shotplan.getLatitude()));
        ((TextView) findViewById(R.id.txt_longitude)).setText(String.format("%.2f", shotplan.getLongitude()));
        ((TextView) findViewById(R.id.shot_plan_title)).setText(shotplan.getLens());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.map_overlay_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mediaFulllScreenIntent = new Intent(ShotPlanActivity.this, MapActivity.class);
                mediaFulllScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", shotplan.getLatitude());
                bundle.putDouble("longitude", shotplan.getLongitude());

                mediaFulllScreenIntent.putExtras(bundle);
                startActivityForResult(mediaFulllScreenIntent, CHANGE_LOCATION_REQUEST);
            }
        });

        findViewById(R.id.shot_plan_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShotPlan();
                finish();
            }
        });
    }

    private void saveShotPlan() {
        EditText txtTitle = findViewById(R.id.txt_title);
        EditText txtNotes = findViewById(R.id.txt_notes);

        shotplan.setTitle(txtTitle.getText().toString());
        shotplan.setNotes(txtNotes.getText().toString());

        if(shotplan.getId() == 0){
            _artemisDbHelper.insertShotplan(shotplan);
        } else {
            _artemisDbHelper.updateShotplan(shotplan);
        }
    }

    public Shotplan loadShotPlanForPath(String path) {
        try {
            return _artemisDbHelper.getShotplanByPath(path);
        } catch(Exception ex) {
            return new Shotplan(
                    0,
                    path,
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    0.0
            );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(shotplan.getLatitude(), shotplan.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHANGE_LOCATION_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                updateShotplanLocation(latitude, longitude);
            }
        }
    }

    private void updateShotplanLocation(double latitude, double longitude) {
        shotplan.setLatitude(latitude);
        shotplan.setLongitude(longitude);

        ((TextView) findViewById(R.id.txt_latitude)).setText(String.format("%.2f", shotplan.getLatitude()));
        ((TextView) findViewById(R.id.txt_longitude)).setText(String.format("%.2f", shotplan.getLongitude()));

        mMap.clear();
        LatLng location = new LatLng(shotplan.getLatitude(), shotplan.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
