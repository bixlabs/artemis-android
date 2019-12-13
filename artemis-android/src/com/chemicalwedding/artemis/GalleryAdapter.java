package com.chemicalwedding.artemis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.Photo;

import java.io.File;
import java.util.List;

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
        holder.name.setText(photoList.get(position).getName());

        File imgFile = new  File(photoList.get(position).getPath());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageView.setImageBitmap(myBitmap);
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