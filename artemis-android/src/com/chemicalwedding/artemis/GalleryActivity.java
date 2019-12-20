package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
        mAdapter.setRecyclerItemListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(Integer position) {
                Intent pictureFulllScreenIntent = new Intent(GalleryActivity.this, PictureFullScreenActivity.class);
                pictureFulllScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                bundle.putString("fullScreenPhotoPath", photoList.get(position).getPath());
                pictureFulllScreenIntent.putExtras(bundle);
                startActivity(pictureFulllScreenIntent);
                recyclerView.setAdapter(mAdapter);
            }
        });
        mAdapter.setOnGalleryCheckboxItemListener(new GalleryPhotoCheckboxClickListener() {
            @Override
            public void onCheckboxClick(Integer position) {
                updateSelectedPhotosCounter();
            }
        });
        recyclerView.setAdapter(mAdapter);

        setupButtons();
        loadGalleryData();
    }

    private void setupButtons() {
        final Button selectImagesButton = findViewById(R.id.select_images_galleryselector);
        final Button selectAllButton = findViewById(R.id.select_all_galleryselector);
        final Button selectNoneButton = findViewById(R.id.select_none_galleryselector);
        final ImageButton deletePhotosButton = findViewById(R.id.delete_photos_galleryselector);
        final TextView counterTextView = findViewById(R.id.counter_galleryselector);
        selectAllButton.setAlpha(0.5f);
        selectAllButton.setEnabled(false);
        selectNoneButton.setAlpha(0.5f);
        selectNoneButton.setEnabled(false);
        deletePhotosButton.setAlpha(0.5f);
        deletePhotosButton.setEnabled(false);
        counterTextView.setAlpha(0.5f);
        counterTextView.setEnabled(false);
        
        selectImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagesButton.setSelected(!selectImagesButton.isSelected());

                if (selectImagesButton.isSelected()) {
                    selectAllButton.setAlpha(1.0f);
                    selectNoneButton.setAlpha(1.0f);
                    deletePhotosButton.setAlpha(1.0f);
                    counterTextView.setAlpha(1.0f);
                    selectAllButton.setEnabled(true);
                    selectNoneButton.setEnabled(true);
                    deletePhotosButton.setEnabled(true);
                    counterTextView.setEnabled(true);
                    mAdapter.canSelectPhotos = true;
                }else {
                    selectAllButton.setAlpha(0.5f);
                    selectNoneButton.setAlpha(0.5f);
                    deletePhotosButton.setAlpha(0.5f);
                    counterTextView.setAlpha(0.5f);
                    selectAllButton.setEnabled(false);
                    selectNoneButton.setEnabled(false);
                    deletePhotosButton.setEnabled(false);
                    counterTextView.setEnabled(false);
                    mAdapter.canSelectPhotos = false;
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.selectedPhotos.clear();
                for(int i=0; i<=photoList.size() - 1; i++){
                    mAdapter.selectedPhotos.add(i);
                }
                Log.i("bixlabs", "All pressed: " + mAdapter.selectedPhotos.toString());
                mAdapter.notifyDataSetChanged();
                updateSelectedPhotosCounter();
            }
        });

        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.selectedPhotos.clear();
                Log.i("bixlabs", "None pressed: " + mAdapter.selectedPhotos.toString());
                mAdapter.notifyDataSetChanged();
                updateSelectedPhotosCounter();
            }
        });

        deletePhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GalleryActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete photos")
                        .setMessage("Are you sure you want to delete selected photos?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i=0; i<=mAdapter.selectedPhotos.size() - 1; i++){
                                    File fileToDetele = new File(photoList.get(i).getPath());
                                    fileToDetele.delete();
                                }
                                mAdapter.selectedPhotos.clear();
                                updateSelectedPhotosCounter();
                                loadGalleryData();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }

    private void updateSelectedPhotosCounter() {
        final TextView selectedPhotosCounter = findViewById(R.id.counter_galleryselector);
        selectedPhotosCounter.setText(String.valueOf(mAdapter.selectedPhotos.size()));
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

                photoList.clear();
                File directory = new File(folder);
                File[] files = directory.listFiles();
                Log.d("bixlabs", "Folder size: "+ files.length);
                for (int i = 0; i < files.length; i++)
                {
                    Log.d("bixlabs", "File name: "  + files[i].getAbsolutePath());
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