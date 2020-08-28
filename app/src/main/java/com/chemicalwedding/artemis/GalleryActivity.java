package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.MediaFile;
import com.chemicalwedding.artemis.database.MediaType;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GalleryActivity extends Activity {

    private List<MediaFile> mediaList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GalleryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_gallery);

        recyclerView = findViewById(R.id.gallery_recycler_view);

        mAdapter = new GalleryAdapter(mediaList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GalleryGridSpacingItemDecoration(4, 50, true));
        mAdapter.setRecyclerItemListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(Integer position) {
                Intent mediaFulllScreenIntent = new Intent(GalleryActivity.this, MediaFullScreenActivity.class);
                mediaFulllScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                bundle.putString("fullScreenMediaPath", mediaList.get(position).getPath());
                bundle.putString("fullScreenMediaType", mediaList.get(position).getMediaType().toString());

                mediaFulllScreenIntent.putExtras(bundle);
                startActivity(mediaFulllScreenIntent);
                recyclerView.setAdapter(mAdapter);
            }
        });
        mAdapter.setOnGalleryCheckboxItemListener(new GalleryPhotoCheckboxClickListener() {
            @Override
            public void onCheckboxClick(Integer position) {
                final ImageButton deletePhotosButton = findViewById(R.id.delete_photos_galleryselector);
                if (mAdapter.selectedFiles.size() > 0) {
                    deletePhotosButton.setAlpha(1.0f);
                    deletePhotosButton.setEnabled(true);
                } else {
                    deletePhotosButton.setAlpha(0.5f);
                    deletePhotosButton.setEnabled(false);
                }
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
                    counterTextView.setAlpha(1.0f);
                    selectAllButton.setEnabled(true);
                    selectNoneButton.setEnabled(true);
                    counterTextView.setEnabled(true);
                    if (mAdapter.selectedFiles.size() > 0) {
                        deletePhotosButton.setAlpha(1.0f);
                        deletePhotosButton.setEnabled(true);
                    }
                    mAdapter.canSelectFiles = true;
                }else {
                    selectAllButton.setAlpha(0.5f);
                    selectNoneButton.setAlpha(0.5f);
                    deletePhotosButton.setAlpha(0.5f);
                    counterTextView.setAlpha(0.5f);
                    selectAllButton.setEnabled(false);
                    selectNoneButton.setEnabled(false);
                    deletePhotosButton.setEnabled(false);
                    counterTextView.setEnabled(false);
                    mAdapter.canSelectFiles = false;
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.selectedFiles.clear();
                for(int i = 0; i<= mediaList.size() - 1; i++){
                    mAdapter.selectedFiles.add(i);
                }
                Log.i("bixlabs", "All pressed: " + mAdapter.selectedFiles.toString());
                mAdapter.notifyDataSetChanged();
                updateSelectedPhotosCounter();
            }
        });

        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.selectedFiles.clear();
                Log.i("bixlabs", "None pressed: " + mAdapter.selectedFiles.toString());
                mAdapter.notifyDataSetChanged();
                updateSelectedPhotosCounter();
            }
        });

        deletePhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GalleryActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete files")
                        .setMessage("Are you sure you want to delete selected files?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i = 0; i<=mAdapter.selectedFiles.size() - 1; i++){
                                    File fileToDetele = new File(mediaList.get(i).getPath());
                                    fileToDetele.delete();
                                }
                                mAdapter.selectedFiles.clear();
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
        selectedPhotosCounter.setText(String.valueOf(mAdapter.selectedFiles.size()));
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

        mediaList.clear();
        File directory = new File(folder);
        File[] files = directory.listFiles();
//                Log.d("bixlabs", "Folder size: "+ files.length);
         if (files != null){
                for (int i = 0; i < files.length; i++) {
                    Log.d("bixlabs", "File name: " + files[i].getAbsolutePath());
                    MediaType type = MediaType.PHOTO;
                    String mimeType = URLConnection.guessContentTypeFromName(files[i].getAbsolutePath());
                    if (mimeType != null && mimeType.startsWith("video")) {
                        type = MediaType.VIDEO;
                    }
                    MediaFile mediaFile = new MediaFile(files[i].getName(), files[i].getAbsolutePath(), new Date(files[i].lastModified()), type);
                    mediaList.add(mediaFile);
                }
        }
        Collections.sort(mediaList, new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile mediaFile, MediaFile t1) {
                return t1.getDate().compareTo(mediaFile.getDate());
            }
        });
        mAdapter.notifyDataSetChanged();
    }

}