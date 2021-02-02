package com.chemicalwedding.artemis.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.chemicalwedding.artemis.BuildConfig;
import com.chemicalwedding.artemis.GalleryActivity;
import com.chemicalwedding.artemis.R;
import com.chemicalwedding.artemis.database.MediaFile;
import com.chemicalwedding.artemis.database.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PdfUtils {

    private static final int widthA4 = 842;
    private static final int heigthA4 = 595;

    public static void sharePdf(Activity activity, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(intent);
    }

    public static  void exportImagesAsPdf(Activity activity, List<MediaFile> selectedFiles) {
        PdfDocument document = new PdfDocument();
        int totalPages = (selectedFiles.size() / 4);
        int lastPageImages = (selectedFiles.size() % 4);
        totalPages += lastPageImages > 0 ? 1 : 0;

        for(int i = 1; i <= totalPages; i++) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(widthA4, heigthA4, i)
                    .create();
            PdfDocument.Page page = getPage(document, pageInfo, selectedFiles, i, activity);
            document.finishPage(page);
        }

        String artemisDirPath = ArtemisFileUtils.Companion.ensureArtemisDir(activity);
        File pdfFile = new File(artemisDirPath, "Artemis Pro" + ".pdf");

        try {
            if(pdfFile.exists()) {
                pdfFile.delete();
            }
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            document.writeTo(outputStream);
            PdfUtils.sharePdf(activity, pdfFile);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        document.close();
    }

    private static PdfDocument.Page getPage(PdfDocument document, PdfDocument.PageInfo pageInfo, List<MediaFile> files, int pageNumber, Context context) {
        PdfDocument.Page page = document.startPage(pageInfo);
//        if (pageInfo.getPageNumber() == 1) {
//            drawTitle("Title");
//        }

        // Pos 1
        int topMargin = (int)(heigthA4 * .15);
        int left1 = (int) (widthA4 * .10);
        int rigth1 = (int) ((widthA4 / 2) - (widthA4 * .05));
        int bottom1 = (int)((heigthA4 / 2) - (heigthA4 * .05));
        int rigth2 = (int) (widthA4 - (widthA4 * .10));
        int left2 = (int) ((widthA4 / 2) + (widthA4 * .05));
        int top2 = (int) ((heigthA4 / 2) + (heigthA4 * .025));
        int bottom2 = (int) (heigthA4 - (heigthA4 * .2));
        Canvas canvas = page.getCanvas();

        int index1 = (pageNumber * 4) -4;
        if(index1 >= 0 && index1 < files.size()) {
            File file = new File(files.get(index1).getPath());
            Bitmap bitmap = getBitmap(file, files.get(index1).getMediaType());
            Rect rect = new Rect();
            rect.top = topMargin;
            rect.left = left1;
            rect.right = rigth1;
            rect.bottom = bottom1;
            if(bitmap != null)
                canvas.drawBitmap(bitmap, null, rect, null);
        }

        // Pos 2
        int index2 = (pageNumber * 4) -3;
        if(index2 >= 0 && index2 < files.size()) {
            File file2 = new File(files.get(index2).getPath());
            Bitmap bitmap2 = getBitmap(file2, files.get(index2).getMediaType());
            Rect rect2 = new Rect();
            rect2.top = topMargin;
            rect2.left = left2;
            rect2.right = rigth2;
            rect2.bottom = bottom1;
            if(bitmap2 != null)
                canvas.drawBitmap(bitmap2, null, rect2, null);
        }

        // Pos 3
        int index3 = (pageNumber * 4) -2;
        if(index3 >= 0 && index3 < files.size()) {
            File file3 = new File(files.get(index3).getPath());
            Bitmap bitmap3 = getBitmap(file3, files.get(index3).getMediaType());
            Rect rect3 = new Rect();
            rect3.top = top2;
            rect3.left = left1;
            rect3.right = rigth1;
            rect3.bottom = bottom2;
            if(bitmap3 != null)
                canvas.drawBitmap(bitmap3, null, rect3, null);
        }

        // Pos 4
        int index4 = (pageNumber * 4) -1;
        if(index4 >= 0 && index4 < files.size()) {
            File file4 = new File(files.get(index4).getPath());
            Bitmap bitmap4 = getBitmap(file4, files.get(index4).getMediaType());
            Rect rect4 = new Rect();
            rect4.top = top2;
            rect4.left = left2;
            rect4.right = rigth2;
            rect4.bottom = bottom2;
            if(bitmap4 != null)
                canvas.drawBitmap(bitmap4, null, rect4, null);
        }

        drawFooter(page, context);

        return page;
    }

    private static void drawFooter(PdfDocument.Page page, Context context) {
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.artemis_logo);
        int y = (int) (heigthA4 - (heigthA4 * .08));
        int w = (widthA4 / 2) - 12;
        Rect rect = new Rect();
        rect.top = y;
        rect.bottom = y + 25;
        rect.left = w;
        rect.right = w + 25;

        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(logo, null, rect, null);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(8);
        canvas.drawText("Artemis Pro", w + 40, y + 15, paint);
    }


    private static Bitmap getBitmap(File mediaFile, MediaType mediaType) {
        if(mediaFile.exists()){
            if (mediaType == MediaType.PHOTO) {
                Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
                return myBitmap;
            } else {
                try {
                    Bitmap bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(mediaFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    return bitmapThumbnail;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
