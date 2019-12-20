package com.chemicalwedding.artemis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.chemicalwedding.artemis.database.Photo;
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

    private List<Photo> photoList;

    public GalleryAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }
    public Boolean canSelectPhotos = false;
    public RecyclerItemClickListener mOnRecyclerItemListener;
    public GalleryPhotoCheckboxClickListener mOnGalleryCheckboxItemListener;
    public Set<Integer> selectedPhotos = new HashSet<>();

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
        if (canSelectPhotos) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        holder.checkBox.setChecked(selectedPhotos.contains(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedPhotos.add(position);
                }else {
                    selectedPhotos.remove(position);
                }
                mOnGalleryCheckboxItemListener.onCheckboxClick(position);
                Log.i("bixlabs", "Selected photos: " + selectedPhotos.toString());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRecyclerItemListener.onItemClick(position);
            }
        });

        String imgPath = photoList.get(position).getPath();
        File imgFile = new File(imgPath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageView.setImageBitmap(myBitmap);

            try {
                // Test retrieving exif data
                // We can also get TAG_FOCAL_LENGTH but as Rational (x/y)
                ExifInterface ex = new ExifInterface(imgPath);
                String userMessage = ex.getAttribute(ExifInterface.TAG_USER_COMMENT);
                if (userMessage != null) {
                    Log.i("bixlabs", userMessage);
                }

                Date date = photoList.get(position).getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy 'at' h:mm aa", Locale.getDefault());
                holder.name.setText(sdf.format(date));
            }catch (IOException ioe) {
                Log.e("GalleryAdapter", "Could not open image for reading EXIF data");
            }
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;
        public CheckBox checkBox;

        public PhotoViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.photo_row_name);
            imageView = view.findViewById(R.id.photo_row_imageView);
            checkBox = view.findViewById(R.id.photo_row_radioCheckBox);
        }
    }
}