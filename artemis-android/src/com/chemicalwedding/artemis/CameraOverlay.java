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

public class CameraOverlay extends View {

	private Paint _paint = new Paint();
	private DashPathEffect _boxLineEffect = new DashPathEffect(new float[] {
			10, 5, 5, 5 }, 2.0f);
	private ArtemisMath _artemisMath = ArtemisMath.getInstance();
//	private int screenWidth, screenHeight;
	protected static boolean lockBoxEnabled = false;
	
	public CameraOverlay(Context context, AttributeSet attr) {
		super(context, attr);

//		screenWidth = context.getResources().getDisplayMetrics().widthPixels;
//		screenHeight = context.getResources().getDisplayMetrics().heightPixels;
//		if (screenHeight > screenWidth) {
//			// swap!
//			screenWidth = getContext().getResources().getDisplayMetrics().heightPixels;
//			screenHeight = getContext().getResources().getDisplayMetrics().widthPixels;
//		}

		// _paint.setAntiAlias(true);
		// _paint.setTypeface(Typeface.DEFAULT_BOLD);
		_paint.setTextAlign(Align.CENTER);
		_paint.setTextSize(14f);
		setWillNotDraw(false);

		// if (Build.VERSION.SDK_INT >= 14)
		setLayerType(LAYER_TYPE_HARDWARE, null);
	}

	RectF lensFocalLengthBox = new RectF(0, 0, 0, 0);

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (ArtemisRectF lensRect : _artemisMath.get_currentLensBoxes()) {
			_paint.setStrokeWidth(1f);
			_paint.setPathEffect(null);

			// black line underneath
			if (lensRect.isSolid()) {
				_paint.setStyle(Style.FILL);
				_paint.setColor(Color.BLACK);
				canvas.drawRect(lensRect, _paint);
			} else if (!_artemisMath.isFullscreen()) {
				if (Color.RED == lensRect.getColor()) {
					_paint.setStrokeWidth(2f);
				}
				_paint.setStyle(Style.FILL);
				_paint.setColor(Color.RED);
				if (lensRect.getFocalLengthString().length() > 2) {
					lensFocalLengthBox.set(lensRect.right - 34,
							lensRect.bottom - 20, lensRect.right - 1,
							lensRect.bottom - 1);
					canvas.drawRect(lensFocalLengthBox, _paint);
					_paint.setColor(Color.WHITE);
					canvas.drawText(lensRect.getFocalLengthString(),
							lensRect.right - 17, lensRect.bottom - 5, _paint);
				} else {
					lensFocalLengthBox.set(lensRect.right - 26,
							lensRect.bottom - 20, lensRect.right - 1,
							lensRect.bottom - 1);
					canvas.drawRect(lensFocalLengthBox, _paint);
					_paint.setColor(Color.WHITE);
					canvas.drawText(lensRect.getFocalLengthString(),
							lensRect.right - 13, lensRect.bottom - 5, _paint);
				}

				_paint.setStyle(Style.STROKE);
				_paint.setColor(Color.BLACK);
				_paint.setPathEffect(null);
				canvas.drawRect(lensRect, _paint);
				_paint.setColor(lensRect.getColor());
				_paint.setPathEffect(_boxLineEffect);

				canvas.drawRect(lensRect, _paint);

			}
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
			
			if (!_artemisMath.isFullscreen()) {
				_paint.setPathEffect(null);
				_paint.setStyle(Style.FILL);
				lensFocalLengthBox.set(currentGreenBox.right - 30,
						currentGreenBox.bottom - 20, currentGreenBox.right - 1,
						currentGreenBox.bottom - 1);
				canvas.drawRect(lensFocalLengthBox, _paint);
				_paint.setStrokeWidth(1f);
				_paint.setTypeface(Typeface.DEFAULT);
				_paint.setColor(Color.BLACK);
				canvas.drawText(currentGreenBox.getFocalLengthString(),
						currentGreenBox.right - 15, currentGreenBox.bottom - 5,
						_paint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		if (!lockBoxEnabled && !_artemisMath.isFullscreen()
				&& _artemisMath.getSelectedCamera() != null
				&& _artemisMath.getSelectedLenses() != null) {
			float touchX = event.getX();
			float touchY = event.getY();

			if (touchX > _artemisMath.get_minTouchX()
					&& touchX < _artemisMath.get_maxTouchX()
					&& touchY > _artemisMath.get_minTouchY()
					&& touchY < _artemisMath.get_maxTouchY()) {

				_artemisMath.setTouchX(event.getX());
				_artemisMath.setTouchY(event.getY());

				refreshLensBoxesAndLabelsForLenses();
			}
		}
		return true;
	}

	public void refreshLensBoxesAndLabelsForLenses() {
		_artemisMath.calculateRectBoxesAndLabelsForLenses();
		this.postInvalidate();
		ArtemisActivity._lensFocalLengthText.setText(_artemisMath
				.get_selectedLensFocalLength());
	}

}
