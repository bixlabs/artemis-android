package com.chemicalwedding.artemis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.Photo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder>{

    private List<Photo> photoList;

    public GalleryAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_gallery_list_row, parent, false);

        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
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

        public PhotoViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.photo_row_name);
            imageView = view.findViewById(R.id.photo_row_imageView);
        }
    }
}