package com.chemicalwedding.artemis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomCameraOverlay extends View {

	private Paint _paint = new Paint();
	private DashPathEffect _boxLineEffect = new DashPathEffect(new float[] {
			10, 5, 5, 5 }, 2.0f);
	private ArtemisMath _artemisMath = ArtemisMath.getInstance();

	public CustomCameraOverlay(Context context, AttributeSet attr) {
		super(context, attr);
		setWillNotDraw(false);
	}

	RectF lensFocalLengthBox = new RectF(0, 0, 0, 0);

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (ArtemisRectF lensRect : _artemisMath.get_currentLensBoxes()) {
			_paint.setStrokeWidth(1f);
			_paint.setPathEffect(null);

			// black boxes
			_paint.setStyle(Style.FILL);
			_paint.setColor(Color.BLACK);
			canvas.drawRect(lensRect, _paint);
		}
		ArtemisRectF currentGreenBox = _artemisMath.getCurrentGreenBox();
		if (currentGreenBox != null) {
			_paint.setStrokeWidth(2f);
			_paint.setStyle(Style.STROKE);
			_paint.setColor(Color.BLACK);
			_paint.setPathEffect(null);
			canvas.drawRect(currentGreenBox, _paint);
			_paint.setColor(_artemisMath.getCurrentGreenBox().getColor());
			_paint.setPathEffect(_boxLineEffect);
			canvas.drawRect(_artemisMath.getCurrentGreenBox(), _paint);
		}
	}
}
