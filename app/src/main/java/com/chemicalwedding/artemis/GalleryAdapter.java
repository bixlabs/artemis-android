package com.chemicalwedding.artemis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.Size;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chemicalwedding.artemis.database.MediaFile;
import com.chemicalwedding.artemis.database.MediaType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

interface GalleryPhotoCheckboxClickListener {
    void onCheckboxClick(Integer position);
}

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder>{

    private List<MediaFile> fileList;
    private Context context;

    public GalleryAdapter(List<MediaFile> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }
    public Boolean canSelectFiles = false;
    public RecyclerItemClickListener mOnRecyclerItemListener;
    public GalleryPhotoCheckboxClickListener mOnGalleryCheckboxItemListener;
    public Set<MediaFile> selectedFiles = new HashSet<>();

    public void setRecyclerItemListener(RecyclerItemClickListener listener) {
        mOnRecyclerItemListener = listener;
    }

    public void setOnGalleryCheckboxItemListener(GalleryPhotoCheckboxClickListener listener) {
        mOnGalleryCheckboxItemListener = listener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_gallery_list_row, parent, false);

        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (canSelectFiles) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        View.OnClickListener selectionListener = v -> {
            checkItem(position);
            Log.i("bixlabs", "Selected files: " + selectedFiles.toString());
        };
        holder.checkBox.setOnClickListener(selectionListener);
        holder.checkBox.setChecked(selectedFiles.contains(fileList.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRecyclerItemListener.onItemClick(position);
            }
        });

        MediaFile media = fileList.get(position);
        File mediaFile = new File(media.getPath());
        if(mediaFile.exists()){
            Uri uri = Uri.fromFile(new File(media.getPath()));
            Glide.with(context)
                .load(uri)
                .thumbnail(0.1f)
                .into(holder.imageView);

            if (media.getMediaType() == MediaType.PHOTO) {
                holder.movieIconContainer.setVisibility(View.INVISIBLE);
            } else {
                holder.movieIconContainer.setVisibility(View.VISIBLE);
            }

            try {
                // Test retrieving exif data
                // We can also get TAG_FOCAL_LENGTH but as Rational (x/y)
                ExifInterface ex = new ExifInterface(media.getPath());
                String userMessage = ex.getAttribute(ExifInterface.TAG_USER_COMMENT);
                if (userMessage != null) {
                    Log.i("bixlabs", userMessage);
                }

                Date date = fileList.get(position).getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy 'at' h:mm aa", Locale.getDefault());
                holder.name.setText(sdf.format(date));
            }catch (IOException ioe) {
                Log.e("GalleryAdapter", "Could not open image for reading EXIF data");
            }
        }
    }

    public void checkItem(int position) {
        if (selectedFiles.contains(fileList.get(position))) {
            selectedFiles.remove(fileList.get(position));
        } else {
            selectedFiles.add(fileList.get(position));
        }
        mOnGalleryCheckboxItemListener.onCheckboxClick(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;
        public ImageView movieIcon;
        public ConstraintLayout movieIconContainer;
        public CheckBox checkBox;

        public PhotoViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.photo_row_name);
            imageView = view.findViewById(R.id.photo_row_imageView);
            checkBox = view.findViewById(R.id.photo_row_radioCheckBox);
            movieIcon = view.findViewById(R.id.photo_row_movie_icon);
            movieIconContainer = view.findViewById(R.id.photo_row_movie_icon_container);
        }
    }

    /**
     * Retrieve video frame image from given video path
     *
     * @param p_videoPath
     *            the video file path
     *
     * @return Bitmap - the bitmap is video frame image
     *
     * @throws Throwable
     */
    @SuppressLint("NewApi")
    public static Bitmap retriveVideoFrameFromVideo(String p_videoPath)
            throws Throwable
    {
        Bitmap m_bitmap = null;
        MediaMetadataRetriever m_mediaMetadataRetriever = null;
        try
        {
            m_mediaMetadataRetriever = new MediaMetadataRetriever();
            m_mediaMetadataRetriever.setDataSource(p_videoPath);
            m_bitmap = m_mediaMetadataRetriever.getFrameAtTime();
        }
        catch (Exception m_e)
        {
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String p_videoPath)"
                            + m_e.getMessage());
        }
        finally
        {
            if (m_mediaMetadataRetriever != null)
            {
                m_mediaMetadataRetriever.release();
            }
        }
        return m_bitmap;
    }
}