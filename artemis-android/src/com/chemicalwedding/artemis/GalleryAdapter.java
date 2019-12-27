package com.chemicalwedding.artemis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

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

    public GalleryAdapter(List<MediaFile> fileList) {
        this.fileList = fileList;
    }
    public Boolean canSelectFiles = false;
    public RecyclerItemClickListener mOnRecyclerItemListener;
    public GalleryPhotoCheckboxClickListener mOnGalleryCheckboxItemListener;
    public Set<Integer> selectedFiles = new HashSet<>();

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

        holder.checkBox.setChecked(selectedFiles.contains(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedFiles.add(position);
                }else {
                    selectedFiles.remove(position);
                }
                mOnGalleryCheckboxItemListener.onCheckboxClick(position);
                Log.i("bixlabs", "Selected files: " + selectedFiles.toString());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRecyclerItemListener.onItemClick(position);
            }
        });

        MediaFile media = fileList.get(position);
        File mediaFile = new File(media.getPath());
        if(mediaFile.exists()){
            if (media.getMediaType() == MediaType.PHOTO) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
                holder.imageView.setImageBitmap(imageBitmap);
                holder.movieIconContainer.setVisibility(View.INVISIBLE);
            } else {
                Bitmap videoPreviewBitmap = ThumbnailUtils.createVideoThumbnail(mediaFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                holder.imageView.setImageBitmap(videoPreviewBitmap);
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
}