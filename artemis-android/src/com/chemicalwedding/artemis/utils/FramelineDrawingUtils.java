package com.chemicalwedding.artemis.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.chemicalwedding.artemis.ArtemisActivity;
import com.chemicalwedding.artemis.CameraOverlay;
import com.chemicalwedding.artemis.R;
import com.chemicalwedding.artemis.model.Frameline;

import java.io.File;
import java.util.List;

public class FramelineDrawingUtils {
    public static Bitmap createBitmapFromFramelines(int width, int height, Context context) {

        List<Frameline> appliedFramelines = ArtemisActivity.appliedFramelines;

        if (appliedFramelines != null) {
            Bitmap framelinesBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas();
            c.setBitmap(framelinesBitmap);

            for (Frameline frameline : appliedFramelines) {
                RectF rect = new RectF();
                rect.top = 0;
                rect.bottom = c.getHeight();
                rect.left = 0;
                rect.right = c.getWidth();
                double rate = frameline.getRate().getRate();
                int framelineScale = frameline.getScale();
                int verticalOffsetPercentage = frameline.getVerticalOffset();
                int horizontalOffsetPercentage = frameline.getHorizontalOffset();
                int stroke = frameline.getLineWidth();
                boolean dottedLine = frameline.isDotted();
                int color = frameline.getColor();
                int framelineType = frameline.getFramelineType();
                int centerMarkerType = frameline.getCenterMarkerType();
                int centerMarkerLineWidth = frameline.getCenterMarkerLineWidth();
                int shadingColorId = frameline.getShading() == 0 ? R.color.shading_0
                        : frameline.getShading() == 1 ? R.color.shadin_25
                        : frameline.getShading() == 2 ? R.color.shadin_50
                        : frameline.getShading() == 3 ? R.color.shadin_75
                        : R.color.shadin_100;
                int backgroundColor = context.getResources().getColor(shadingColorId);
                CameraOverlay.drawFrameline(c, rect, framelineScale, verticalOffsetPercentage, horizontalOffsetPercentage, stroke, dottedLine, color, framelineType, centerMarkerType, centerMarkerLineWidth, backgroundColor);
            }

            return framelinesBitmap;
        } else {
            return null;
        }
    }
}
