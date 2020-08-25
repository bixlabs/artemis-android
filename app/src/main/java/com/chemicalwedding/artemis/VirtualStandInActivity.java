package com.chemicalwedding.artemis;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import com.veinhorn.scrollgalleryview.builder.GallerySettings;

import static com.veinhorn.scrollgalleryview.loader.picasso.dsl.DSL.image;
import static com.veinhorn.scrollgalleryview.loader.picasso.dsl.DSL.video;


public class VirtualStandInActivity extends AppCompatActivity {

    private ArtemisDatabaseHelper databaseHelper;
    private ImageView peopleButton;
    private ImageView vehiclesButton;
    private ScrollGalleryView galleryView;
    private static final String TAG = VirtualStandInActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_stand_in);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        databaseHelper = new ArtemisDatabaseHelper(this);

        galleryView = ScrollGalleryView
                .from((ScrollGalleryView) findViewById(R.id.scrollGalleryView))
                .settings(
                        GallerySettings
                                .from(getSupportFragmentManager())
                                .thumbnailSize(100)
                                .enableZoom(true)
                                .build()
                )
                .add(image("file:///android_asset/m_01.jpg"))
                .add(image("file:///android_asset/m_02.jpg"))
                .add(image("file:///android_asset/m_03.jpg"))
                .add(image("file:///android_asset/m_04.jpg"))
                .add(image("file:///android_asset/m_05.jpg"))
                .add(image("file:///android_asset/m_06.jpg"))
                .add(image("file:///android_asset/m_07.jpg"))
                .add(image("file:///android_asset/m_08.jpg"))
                .add(image("file:///android_asset/m_09.jpg"))
                .add(image("file:///android_asset/m_10.jpg"))
                .add(image("file:///android_asset/m_11.jpg"))
                .add(image("file:///android_asset/m_12.jpg"))
                .add(image("file:///android_asset/m_13.jpg"))
                .add(image("file:///android_asset/m_14.jpg"))
                .add(image("file:///android_asset/m_15.jpg"))
                .add(image("file:///android_asset/m_16.jpg"))
                .add(image("file:///android_asset/m_17.jpg"))
                .add(image("file:///android_asset/m_18.jpg"))
                .add(image("file:///android_asset/m_19.jpg"))
                .add(image("file:///android_asset/m_20.jpg"))
                .add(image("file:///android_asset/m_21.jpg"))
                .add(image("file:///android_asset/m_22.jpg"))
                .add(image("file:///android_asset/m_23.jpg"))
                .add(image("file:///android_asset/m_24.jpg"))
                .add(image("file:///android_asset/m_25.jpg"))
                .add(image("file:///android_asset/m_26.jpg"))
                .add(image("file:///android_asset/m_27.jpg"))
                .add(image("file:///android_asset/m_28.jpg"))
                .add(image("file:///android_asset/m_29.jpg"))
                .add(image("file:///android_asset/m_30.jpg"))
                .onImageClickListener(new ScrollGalleryView.OnImageClickListener() {
                    @Override
                    public void onClick(int position) {
                        Log.w(TAG, "position: " + position);
                    }
                })
                .build();
    }

}
