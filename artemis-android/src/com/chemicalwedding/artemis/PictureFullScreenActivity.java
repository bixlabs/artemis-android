package com.chemicalwedding.artemis;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class PictureFullScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_full_screen);

        Bundle bundle = this.getIntent().getExtras();
        String imagePath = bundle.getString("fullScreenPhotoPath");
        File imgFile = new  File(imagePath);
        if(imgFile.exists()){
            ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            fullScreenImageView.setImageBitmap(myBitmap);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
