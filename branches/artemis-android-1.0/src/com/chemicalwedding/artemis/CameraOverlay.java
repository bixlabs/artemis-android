package com.chemicalwedding.artemis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CameraOverlay extends View {

	private Paint _paint = new Paint();
	private DashPathEffect _boxLineEffect = new DashPathEffect(new float[] {
			10, 5, 5, 5 }, 2.0f);
	private ArtemisMath _artemisMath = ArtemisMath.getInstance();
	private int screenWidth, screenHeight;
	protected static boolean lockBoxEnabled = false;

	public CameraOverlay(Context context, AttributeSet attr) {
		super(context, attr);

		screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		screenHeight = context.getResources().getDisplayMetrics().heightPixels;
		if (screenHeight > screenWidth) {
			// swap!
			screenWidth = getContext().getResources().getDisplayMetrics().heightPixels;
			screenHeight = getContext().getResources().getDisplayMetrics().widthPixels;
		}
		
		_paint.setAntiAlias(true);
		_paint.setTypeface(Typeface.DEFAULT_BOLD);
		setWillNotDraw(false);

		if (Build.VERSION.SDK_INT >= 14)
			setLayerType(LAYER_TYPE_HARDWARE, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (ArtemisRectF lensRect : _artemisMath.get_currentLensBoxes()) {
			_paint.setStrokeWidth(1f);
			// black line underneath
			if (lensRect.isSolid()) {
				_paint.setStyle(Style.FILL);
				_paint.setColor(Color.BLACK);
				canvas.drawRect(lensRect, _paint);
			} else if (!_artemisMath.isFullscreen()){
				if (Color.RED == lensRect.getColor()) {
					_paint.setStrokeWidth(2f);
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
		if (_artemisMath.getCurrentGreenBox() != null) {
			_paint.setStrokeWidth(2f);
			_paint.setStyle(Style.STROKE);
			_paint.setColor(Color.BLACK);
			_paint.setPathEffect(null);
			canvas.drawRect(_artemisMath.getCurrentGreenBox(), _paint);
			_paint.setColor(_artemisMath.getCurrentGreenBox().getColor());
			_paint.setPathEffect(_boxLineEffect);
			canvas.drawRect(_artemisMath.getCurrentGreenBox(), _paint);
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
		// if (!_artemisMath.isFullscreen()) {
		this.postInvalidate();
		if (ArtemisActivity._previewSideBar != null) {
			ArtemisActivity._previewSideBar.postInvalidate();
		}
		// }
		ArtemisActivity._lensFocalLengthText.setText(_artemisMath
				.get_selectedLensFocalLength());
	}

}
