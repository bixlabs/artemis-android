package com.chemicalwedding.artemis.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {

    private static final String MODEL_FILE_NAME = "model.png";

    public static File saveBitmapAsTemporalPng(Context context, Bitmap bitmap, String name) throws IOException {
        String fileName = name;
        if(name == null || name.isEmpty()) {
            fileName = FileUtils.getRandomFileName("png");
        }
        File tmpFile = ArtemisFileUtils.Companion.newFile(context, fileName);

        FileOutputStream out = new FileOutputStream(tmpFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
        return tmpFile;
    }

    public static File getModelFile(Context context) {
        File modelFile = ArtemisFileUtils.Companion.newFile(context, MODEL_FILE_NAME);
        return modelFile;
    }

    public static Bitmap getModelBitmap(Context context){
        File modelFile = getModelFile(context);

        if(modelFile != null && modelFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(modelFile.getPath());
            return bitmap;
        }

        return null;
    }
}
