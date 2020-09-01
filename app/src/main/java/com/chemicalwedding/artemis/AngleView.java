package com.chemicalwedding.artemis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("NewApi")
public class AngleView extends View {

    private Paint _paint = new Paint();
    ArtemisMath _artemisMath = ArtemisMath.getInstance();
    float angle;

    private float circleCenterX;
    private float circleCenterY;
    private float redAngleCircleRadius;
    private float whiteCircleRadius;
    private RectF redCircleArcRect;


    int mHeight, mWidth;

    public AngleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        _paint.setAntiAlias(true);
        _paint.setColor(Color.GRAY);
        setWillNotDraw(false);
        setClickable(false);

        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(angle != 0) {
            canvas.rotate(-90, circleCenterX, circleCenterY);
            canvas.drawArc(redCircleArcRect, -angle / 2, angle, true,
                    _paint);
            canvas.drawCircle(circleCenterX, circleCenterY, whiteCircleRadius, _paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;

        redAngleCircleRadius = 0.55f * mHeight;
        circleCenterX = 0.5f * mWidth;
        circleCenterY = 0.7f * mHeight;

        whiteCircleRadius = 0.07f * mHeight;
        redCircleArcRect = new RectF(circleCenterX - redAngleCircleRadius,
                circleCenterY - redAngleCircleRadius, circleCenterX
                + redAngleCircleRadius, circleCenterY
                + redAngleCircleRadius);

    }
}
