package com.chemicalwedding.artemis;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.chemicalwedding.artemis.database.Photo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends Activity {

    private List<Photo> photoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GalleryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_gallery);
        recyclerView = findViewById(R.id.gallery_recycler_view);

        mAdapter = new GalleryAdapter(photoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        loadGalleryData();
    }

    private void loadGalleryData() {
                SharedPreferences artemisPrefs = getApplication()
                        .getSharedPreferences(
                            ArtemisPreferences.class.getSimpleName(),
                            MODE_PRIVATE);
                String prefix = Environment.getExternalStorageDirectory().getAbsolutePath();
                String folder = prefix
                        + "/"
                        + artemisPrefs.getString(
                        ArtemisPreferences.SAVE_PICTURE_FOLDER,
                        getString(R.string.artemis_save_location_default));

                File directory = new File(folder);
                File[] files = directory.listFiles();
                Log.d("bixlabs", "Folder size: "+ files.length);
                for (int i = 0; i < files.length; i++)
                {
                    Log.d("bixlabs", "FileName:" + files[i].getAbsolutePath());
                    Photo photo = new Photo(files[i].getName(), files[i].getAbsolutePath());
                    photoList.add(photo);
                }
                mAdapter.notifyDataSetChanged();
    }

}