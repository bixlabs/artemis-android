package com.chemicalwedding.artemis;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chemicalwedding.artemis.database.Photo;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GalleryGridSpacingItemDecoration(4, 50, true));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent pictureFulllScreenIntent = new Intent(GalleryActivity.this, PictureFullScreenActivity.class);
                        pictureFulllScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Bundle bundle = new Bundle();
                        bundle.putString("fullScreenPhotoPath", photoList.get(position).getPath());
                        pictureFulllScreenIntent.putExtras(bundle);
                        startActivity(pictureFulllScreenIntent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // long press item listener
                    }
                })
        );

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
                    Photo photo = new Photo(files[i].getName(), files[i].getAbsolutePath(), new Date(files[i].lastModified()));
                    photoList.add(photo);
                }
                Collections.sort(photoList, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo photo, Photo t1) {
                        return t1.getDate().compareTo(photo.getDate());
                    }
                });
                mAdapter.notifyDataSetChanged();
    }

}