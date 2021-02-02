package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chemicalwedding.artemis.database.MediaFile;
import com.chemicalwedding.artemis.database.MediaType;
import com.chemicalwedding.artemis.utils.ArtemisFileUtils;
import com.chemicalwedding.artemis.utils.FileUtils;
import com.chemicalwedding.artemis.utils.PdfUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
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

        mAdapter = new GalleryAdapter(mediaList, this.getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GalleryGridSpacingItemDecoration(4, 50, true));
        mAdapter.setRecyclerItemListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(Integer position) {
                if(!mAdapter.canSelectFiles) {
                    Intent mediaFulllScreenIntent = new Intent(GalleryActivity.this, MediaFullScreenActivity.class);
                    mediaFulllScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Bundle bundle = new Bundle();
                    bundle.putString("fullScreenMediaPath", mediaList.get(position).getPath());
                    bundle.putString("fullScreenMediaType", mediaList.get(position).getMediaType().toString());

                    mediaFulllScreenIntent.putExtras(bundle);
                    startActivity(mediaFulllScreenIntent);
                    recyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.checkItem(position);
                    mAdapter.mOnGalleryCheckboxItemListener.onCheckboxClick(position);
                }
            }
        });
        mAdapter.setOnGalleryCheckboxItemListener(new GalleryPhotoCheckboxClickListener() {
            @Override
            public void onCheckboxClick(Integer position) {
                final ImageButton deletePhotosButton = findViewById(R.id.delete_photos_galleryselector);
                final ImageButton shareFilesButton = findViewById(R.id.share_files);
                if (mAdapter.selectedFiles.size() > 0) {
                    deletePhotosButton.setAlpha(1.0f);
                    deletePhotosButton.setEnabled(true);
                    shareFilesButton.setAlpha(1.0f);
                    shareFilesButton.setEnabled(true);
                } else {
                    deletePhotosButton.setAlpha(0.5f);
                    deletePhotosButton.setEnabled(false);
                    shareFilesButton.setAlpha(0.5f);
                    shareFilesButton.setEnabled(false);
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
        final ImageButton shareFilesButton = findViewById(R.id.share_files);
        final TextView counterTextView = findViewById(R.id.counter_galleryselector);
        final ImageView backButton = findViewById(R.id.back_button);
        selectAllButton.setAlpha(0.5f);
        selectAllButton.setEnabled(false);
        selectNoneButton.setAlpha(0.5f);
        selectNoneButton.setEnabled(false);
        deletePhotosButton.setAlpha(0.5f);
        deletePhotosButton.setEnabled(false);
        shareFilesButton.setAlpha(0.5f);
        shareFilesButton.setEnabled(false);
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
                        shareFilesButton.setAlpha(1.0f);
                        shareFilesButton.setEnabled(true);
                    }
                    mAdapter.canSelectFiles = true;
                }else {
                    selectAllButton.setAlpha(0.5f);
                    selectNoneButton.setAlpha(0.5f);
                    deletePhotosButton.setAlpha(0.5f);
                    shareFilesButton.setAlpha(0.5f);
                    counterTextView.setAlpha(0.5f);
                    selectAllButton.setEnabled(false);
                    selectNoneButton.setEnabled(false);
                    deletePhotosButton.setEnabled(false);
                    shareFilesButton.setEnabled(false);
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
                    mAdapter.checkItem(i);
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
                                for(MediaFile file : mAdapter.selectedFiles){
                                    File fileToDetele = new File(file.getPath());
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

        shareFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                builder.setTitle(R.string.export_items)
                        .setItems(R.array.export_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        shareFiles();
                                        break;
                                    case 1:
                                        PdfUtils.exportImagesAsPdf(GalleryActivity.this, new ArrayList<>(mAdapter.selectedFiles));
                                        break;
                                }
                            }
                        });
                builder.create().show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryActivity.this.finish();
            }
        });

    }

    private void shareFiles() {
        Log.e("GalleryActivity", "share button has been pressed");
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for(MediaFile mediaFile : mAdapter.selectedFiles) {
            File fileToShare = new File(mediaFile.getPath());
            Uri uri = FileProvider.getUriForFile(GalleryActivity.this, BuildConfig.APPLICATION_ID + ".provider",fileToShare);

            imageUris.add(uri);
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share images"));

    }

    private void updateSelectedPhotosCounter() {
        final TextView selectedPhotosCounter = findViewById(R.id.counter_galleryselector);
        selectedPhotosCounter.setText(String.valueOf(mAdapter.selectedFiles.size()));
    }

    private List<File> loadFilesFromExternalStorageIfConfigured() {
        if(!ArtemisFileUtils.Companion.hasExternalDir()) {
            return new ArrayList<>();
        }

        String path = ArtemisFileUtils.Companion.ensureSaveDir(this);
        File directory = new File(path);
        File[] files = directory.listFiles();
        return Arrays.asList(files);
    }

    private List<File> loadFilesFromInternalStorage() {
        String folder = ArtemisFileUtils.Companion.ensureArtemisDir(this);
        File directory = new File(folder);
        File[] files = directory.listFiles();
        if(files != null) {
            return Arrays.asList(files);
        } else {
            return new ArrayList<File>();
        }
    }

    private void loadGalleryData() {
<<<<<<< HEAD
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
=======
                SharedPreferences artemisPrefs = getApplication()
                        .getSharedPreferences(
                            ArtemisPreferences.class.getSimpleName(),
                            MODE_PRIVATE);

                Comparator<File> comparator = new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return Long.compare(o2.lastModified(), o1.lastModified());
                    }
                };

                mediaList.clear();
                List<File> filesFromInternalStorage = loadFilesFromInternalStorage();
//                List<File> filesFromExternalStorage = loadFilesFromExternalStorageIfConfigured();
                List<File> files = new ArrayList<>();
                files.addAll(filesFromInternalStorage);
//                files.addAll(filesFromExternalStorage);

                Collections.sort(files, comparator);
                Log.d("bixlabs", "Folder size: "+ files.size());

                for (File file : files) {
<<<<<<< HEAD
<<<<<<< HEAD
                    Log.d("bixlabs", "File name: " + file.getAbsolutePath());
>>>>>>> 8bb9eb3 (fixes file permissions management)
                    MediaType type = MediaType.PHOTO;
                    String mimeType = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
                    if (mimeType != null && mimeType.startsWith("video")) {
                        type = MediaType.VIDEO;
=======
=======
>>>>>>> d36402c (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
                    if(!filterFile(file)){
                        Log.d("bixlabs", "File name: " + file.getAbsolutePath());
                        MediaType type = MediaType.PHOTO;
                        String mimeType = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
                        if (mimeType != null && mimeType.startsWith("video")) {
                            type = MediaType.VIDEO;
                        }
                        MediaFile mediaFile = new MediaFile(file.getName(), file.getAbsolutePath(), new Date(file.lastModified()), type);
                        mediaList.add(mediaFile);
<<<<<<< HEAD
>>>>>>> 5e1520f (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
=======
>>>>>>> d36402c (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
                    }
                }
<<<<<<< HEAD
        }
        Collections.sort(mediaList, new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile mediaFile, MediaFile t1) {
                return t1.getDate().compareTo(mediaFile.getDate());
            }
        });
        mAdapter.notifyDataSetChanged();
=======

                mAdapter.notifyDataSetChanged();
>>>>>>> 8bb9eb3 (fixes file permissions management)
    }

    public boolean filterFile(File file) {
        return file.getName().contains("model") || file.getName().contains("frameline");
    }

    public boolean filterFile(File file) {
        return file.getName().contains("model")
                || file.getName().contains("frameline")
                || file.isDirectory()
                || file.getName().contains(".pdf");
    }

}
