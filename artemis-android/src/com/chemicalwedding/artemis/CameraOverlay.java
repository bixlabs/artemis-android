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

import com.chemicalwedding.artemis.model.Frameline;

import java.util.List;

public class CameraOverlay extends View {

	private Paint _paint = new Paint();
	private DashPathEffect _boxLineEffect = new DashPathEffect(new float[] {
			10, 5, 5, 5 }, 2.0f);
	private ArtemisMath _artemisMath = ArtemisMath.getInstance();
	// private int screenWidth, screenHeight;
	protected static boolean lockBoxEnabled = true;

	protected Frameline currentFrameline;

	public CameraOverlay(Context context, AttributeSet attr) {
		super(context, attr);

		// screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		// screenHeight =
		// context.getResources().getDisplayMetrics().heightPixels;
		// if (screenHeight > screenWidth) {
		// // swap!
		// screenWidth =
		// getContext().getResources().getDisplayMetrics().heightPixels;
		// screenHeight =
		// getContext().getResources().getDisplayMetrics().widthPixels;
		// }

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
				if (lensRect.getFocalLengthString() != null
						&& lensRect.getFocalLengthString().length() > 2) {
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
					if (lensRect.getFocalLengthString() != null) {
						canvas.drawText(lensRect.getFocalLengthString(),
								lensRect.right - 13, lensRect.bottom - 5,
								_paint);
					}
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
		ArtemisRectF currentGreenBox = _artemisMath.getOutsideBox();
		if (currentGreenBox != null) {
			_paint.setStrokeWidth(2f);
			_paint.setStyle(Style.STROKE);
			_paint.setColor(Color.BLACK);
			_paint.setPathEffect(null);
			canvas.drawRect(currentGreenBox, _paint);
			_paint.setColor(_artemisMath.getOutsideBox().getColor());
			_paint.setPathEffect(_boxLineEffect);
			canvas.drawRect(_artemisMath.getOutsideBox(), _paint);

			if(currentFrameline != null && _artemisMath.isFullscreen()) {
				double rate = currentFrameline.getRate().getRate();
				int scale = currentFrameline.getScale();
				int verticalOffsetPercentage = currentFrameline.getVerticalOffset();
				int horizontalOffsetPercentage = currentFrameline.getHorizontalOffset();
				int stroke = currentFrameline.getLineWidth();
				boolean dottedLine = currentFrameline.isDotted();
				int color = currentFrameline.getColor();
				int framelineType = currentFrameline.getFramelineType();
				int centerMarkerType = currentFrameline.getCenterMarkerType();
				int centerMarkerLineWidth = currentFrameline.getCenterMarkerLineWidth();
				int shadingColorId = currentFrameline.getShading() == 0 ? R.color.shading_0
						: currentFrameline.getShading() == 1 ? R.color.shadin_25
						: currentFrameline.getShading() == 2 ? R.color.shadin_50
						: currentFrameline.getShading() == 3 ? R.color.shadin_75
						: R.color.shadin_100;
				int backgroundColor = getResources().getColor(shadingColorId);
				drawFrameline(canvas, currentGreenBox, scale, verticalOffsetPercentage, horizontalOffsetPercentage, stroke, dottedLine, color, framelineType, centerMarkerType, centerMarkerLineWidth, backgroundColor);
			} else if(_artemisMath.isFullscreen() && ArtemisActivity.appliedFramelines != null) {
				List<Frameline> framelines = ArtemisActivity.appliedFramelines;

				for(Frameline frameline : framelines) {
					double rate = frameline.getRate().getRate();
					int scale = frameline.getScale();
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
					int backgroundColor = getResources().getColor(shadingColorId);
					drawFrameline(canvas, currentGreenBox, scale, verticalOffsetPercentage, horizontalOffsetPercentage, stroke, dottedLine, color, framelineType, centerMarkerType, centerMarkerLineWidth, backgroundColor);
				}
			}

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






	// TODO - review this frameline drawing
	public static void drawFrameline(Canvas canvas, RectF gap, int scale, int verticalOffsetPercentage, int horizontalOffsetPercentage, int stroke, boolean dottedLine, int color, int framelineType, int centerMarkerType, int centerMarkerLineWidth, int backgroundColor) {
		Paint frameOutsidePaint = new Paint();
		frameOutsidePaint.setAntiAlias(true);
		frameOutsidePaint.setTypeface(Typeface.DEFAULT_BOLD);
		frameOutsidePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		frameOutsidePaint.setTextAlign(Paint.Align.CENTER);
		frameOutsidePaint.setColor(backgroundColor);

		float canvasWidth = canvas.getWidth();
		float canvasHeight = canvas.getHeight();

		float gapWidth = gap.right - gap.left;
		float gapHeight = gap.bottom - gap.top;

		float scaleddWidth = gapWidth * scale / 100;
		float scaledHeight = gapHeight * scale / 100;

		float horizontal = (gapWidth - scaleddWidth) / 2;
		float vertical = (gapHeight - scaledHeight) / 2;

		float verticalOffset = (gapWidth * -verticalOffsetPercentage) / 100;
		float horizontalOffset = (gapHeight * -horizontalOffsetPercentage) / 100;

//        RectF above = new RectF(0, 0, canvasWidth,  vertical + verticalOffset - stroke);
		RectF above = new RectF(0, 0, canvasWidth, gap.top + vertical + verticalOffset - stroke);
		RectF left = new RectF(0,
				gap.top + vertical + verticalOffset - stroke,
				gap.left + horizontal + horizontalOffset - stroke,
				gap.bottom - vertical + verticalOffset + stroke);
		RectF right = new RectF(gap.right - horizontal + horizontalOffset + stroke,
				gap.top + vertical + verticalOffset - stroke,
				canvasWidth,
				gap.bottom - vertical + verticalOffset + stroke);
		RectF bottom = new RectF(0, gap.bottom - vertical + verticalOffset + stroke, canvasWidth, canvasHeight);

		canvas.drawRect(above, frameOutsidePaint);
		canvas.drawRect(left, frameOutsidePaint);
		canvas.drawRect(right, frameOutsidePaint);
		canvas.drawRect(bottom, frameOutsidePaint);

		drawFrameline(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, stroke, dottedLine, color, framelineType);
		drawCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, color, centerMarkerType, centerMarkerLineWidth);

	}

	private static void drawCenterMarker(Canvas canvas, RectF gap, float horizontal, float vertical, float verticalOffset, float horizontalOffset, int color, int centerMarkerType, int centerMarkerLineWidth) {
		if(centerMarkerType == 1) {
			Paint paint = new Paint();
			paint.setColor(color);
			paint.setStyle(Paint.Style.FILL);
			drawSmallSquareCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, paint);
		} else if (centerMarkerType == 2) {
			Paint paint = new Paint();
			paint.setColor(color);
			paint.setStyle(Paint.Style.FILL);
			drawLargeSquareCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, paint);
		} else if(centerMarkerType == 3) {
			drawLargeCrossCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, color, centerMarkerLineWidth);
		} else if(centerMarkerType == 4) {
			drawSmallCrossCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, color, centerMarkerLineWidth);
		}

	}

	private static void drawSmallCrossCenterMarker(Canvas canvas, RectF gap, float horizontal, float vertical, float verticalOffset, float horizontalOffset, int color, int centerMarkerLineWidth) {
		int centerSpace = 10;
		int crossSize = 15;

		drawCrossCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, color, centerMarkerLineWidth, centerSpace, crossSize);
	}

	private  static void drawLargeCrossCenterMarker(Canvas canvas, RectF gap, float horizontal, float vertical, float verticalOffset, float horizontalOffset, int color, int centerMarkerLineWidth) {
		int centerSpace = 15;
		int crossSize = 30;

		drawCrossCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, color, centerMarkerLineWidth, centerSpace, crossSize);
	}

	private static void drawCrossCenterMarker(Canvas canvas, RectF gap, float horizontal, float vertical, float verticalOffset, float horizontalOffset, int color, int centerMarkerLineWidth, int centerSpace, int crossSize) {
		int baseStroke = 5;
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(baseStroke * 2 * centerMarkerLineWidth);

		float x0 = gap.left + horizontal + horizontalOffset;
		float x1 = gap.right - horizontal + horizontalOffset;
		float centerX = (x1 + x0) / 2;

		float y0 = gap.top + vertical + verticalOffset;
		float y1 = gap.bottom - vertical + verticalOffset;
		float centerY = (y1 + y0) / 2;

		canvas.drawLine(centerX, centerY - crossSize - centerSpace, centerX, centerY - centerSpace, paint);
		canvas.drawLine(centerX, centerY + crossSize + centerSpace, centerX, centerY + centerSpace, paint);

		canvas.drawLine(centerX - crossSize - centerSpace, centerY, centerX - centerSpace, centerY, paint);
		canvas.drawLine(centerX + centerSpace, centerY, centerX + crossSize + centerSpace, centerY, paint);
	}

	private static void drawLargeSquareCenterMarker(Canvas canvas, RectF gap, float horizontal, float vertical, float verticalOffset, float horizontalOffset, Paint paint) {
		int squareSize = 30;
		drawSquareCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, paint, squareSize);
	}

	private static void drawSmallSquareCenterMarker(Canvas canvas, RectF gap, float horizontal, float vertical, float verticalOffset, float horizontalOffset, Paint paint) {
		int squareSize = 15;
		drawSquareCenterMarker(canvas, gap, horizontal, vertical, verticalOffset, horizontalOffset, paint, squareSize);
	}

	private static void drawSquareCenterMarker(Canvas canvas, RectF gap, float horizontal, float vertical, float verticalOffset, float horizontalOffset, Paint paint, int squareSize) {
		float x0 = gap.left + horizontal + horizontalOffset;
		float x1 = gap.right - horizontal + horizontalOffset;
		float centerX = (x1 + x0) / 2;

		float y0 = gap.top + vertical + verticalOffset;
		float y1 = gap.bottom - vertical + verticalOffset;
		float centerY = (y1 + y0) / 2;

		float squareX0 = centerX - squareSize;
		float squareY0 = centerY - squareSize;
		float squareX1 = centerX + squareSize;
		float squareY1 = centerY + squareSize;

		canvas.drawRect(squareX0, squareY0, squareX1, squareY1, paint);
	}

	private static void drawFrameline(Canvas canvas, RectF gap, float horizontal, float vertical, float verticalOffset, float horizontalOffset, int stroke, boolean dottedLine, int color, int framelineType) {

		Paint framelinePaint = getFramelinePaint(color, dottedLine, stroke);
		Paint framelineBackgroundPaint = getFramelineBackgroundPaint(stroke);

		if(dottedLine) {
			drawFrameline(canvas, gap, framelineBackgroundPaint, horizontal, vertical, horizontalOffset, verticalOffset, framelineType);
		}

		drawFrameline(canvas, gap, framelinePaint, horizontal, vertical, horizontalOffset, verticalOffset, framelineType);
	}

	public static void drawFrameline(Canvas canvas, RectF gap, Paint paint, float horizontal, float vertical, float horizontalOffset, float verticalOffset, int framelineType) {
		if(framelineType == 1) {
			drawRectangularFrameline(canvas, gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
		} else if (framelineType == 2) {
			drawVerticalLinesFrameline(canvas, gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
		} else if(framelineType == 3) {
			drawHorizontalLinesFrameline(canvas, gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
		} else if(framelineType == 4) {
			drawCornersFrameline(canvas, gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
		} else if(framelineType == 5) {
			drawVerticalCorners(canvas,  gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
		} else if (framelineType == 6) {
			drawHorizontalCorners(canvas,  gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
		}
	}

	private static void drawCornersFrameline(Canvas canvas, RectF gap, Paint paint, float horizontal, float vertical, float horizontalOffset, float verticalOffset) {
		drawHorizontalCorners(canvas, gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
		drawVerticalCorners(canvas, gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
	}

	private static void drawHorizontalLinesFrameline(Canvas canvas, RectF gap, Paint paint, float horizontal, float vertical, float horizontalOffset, float verticalOffset) {
		canvas.drawLine(gap.left + horizontal + horizontalOffset,
				gap.top + vertical + verticalOffset,
				gap.right - horizontal + horizontalOffset,
				gap.top + vertical + verticalOffset,
				paint
		);

		canvas.drawLine(gap.left + horizontal + horizontalOffset,
				gap.bottom - vertical + verticalOffset,
				gap.right - horizontal + horizontalOffset,
				gap.bottom - vertical + verticalOffset,
				paint
		);


		drawVerticalCorners(canvas, gap, paint, horizontal, vertical, horizontalOffset, verticalOffset);
	}

	private static void drawVerticalCorners(Canvas canvas, RectF gap, Paint paint, float horizontal, float vertical, float horizontalOffset, float verticalOffset) {
		float verticalCornerDistance = (canvas.getHeight() - (2 * vertical)) * 10 / 100;
		// TOP
		canvas.drawLine(
				gap.left + horizontal + horizontalOffset,
				gap.top + vertical + verticalOffset,
				gap.left + horizontal + horizontalOffset,
				gap.top + vertical + verticalOffset + verticalCornerDistance,
				paint
		);

		canvas.drawLine(
				gap.right - horizontal + horizontalOffset,
				gap.top + vertical + verticalOffset,
				gap.right - horizontal + horizontalOffset,
				gap.top + vertical + verticalOffset + verticalCornerDistance,
				paint
		);

		// BOTTOM
		canvas.drawLine(
				gap.left + horizontal + horizontalOffset,
				gap.bottom - vertical + verticalOffset,
				gap.left + horizontal + horizontalOffset,
				gap.bottom - vertical + verticalOffset - verticalCornerDistance,
				paint
		);
		canvas.drawLine(
				gap.right - horizontal + horizontalOffset,
				gap.bottom - vertical + verticalOffset,
				gap.right - horizontal + horizontalOffset,
				gap.bottom - vertical + verticalOffset - verticalCornerDistance,
				paint
		);
	}

	public static void drawRectangularFrameline(Canvas canvas, RectF gap, Paint paint, float horizontal, float vertical, float horizontalOffset, float verticalOffset){
		RectF frameline = new RectF(gap.left + horizontal + horizontalOffset,
				gap.top + vertical + verticalOffset,
				gap.right - horizontal + horizontalOffset,
				gap.bottom - vertical + verticalOffset);
		canvas.drawRect(frameline, paint);
	}

	public static void drawVerticalLinesFrameline(Canvas canvas, RectF gap, Paint paint, float horizontal, float vertical, float horizontalOffste, float verticalOffset) {
		canvas.drawLine(
				gap.left + horizontal + horizontalOffste,
				gap.top + vertical + verticalOffset,
				gap.left + horizontal + horizontalOffste,
				gap.bottom - vertical + verticalOffset,
				paint);

		canvas.drawLine(
				gap.right - horizontal + horizontalOffste,
				gap.top + vertical + verticalOffset,
				gap.right - horizontal + horizontalOffste,
				gap.bottom - vertical + verticalOffset,
				paint);

		drawHorizontalCorners(canvas, gap, paint, horizontal, vertical, horizontalOffste, verticalOffset);
	}

	private static void drawHorizontalCorners(Canvas canvas, RectF gap, Paint paint, float horizontal, float vertical, float horizontalOffste, float verticalOffset) {
		float horizontalCornerDistance = ((gap.right - gap.left) - (2 * horizontal)) * 10 / 100;
		// Top
		canvas.drawLine(
				gap.left + horizontal + horizontalOffste,
				gap.top + vertical + verticalOffset,
				gap.left + horizontal + horizontalOffste + horizontalCornerDistance,
				gap.top + vertical + verticalOffset,
				paint
		);
		canvas.drawLine(
				gap.right - horizontal + horizontalOffste,
				gap.top + vertical + verticalOffset,
				gap.right - horizontal + horizontalOffste - horizontalCornerDistance,
				gap.top + vertical + verticalOffset,
				paint
		);

		// Bottom
		canvas.drawLine(
				gap.left + horizontal + horizontalOffste,
				gap.bottom - vertical + verticalOffset,
				gap.left + horizontal + horizontalOffste + horizontalCornerDistance,
				gap.bottom - vertical + verticalOffset,
				paint
		);
		canvas.drawLine(
				gap.right - horizontal + horizontalOffste,
				gap.bottom - vertical + verticalOffset,
				gap.right - horizontal + horizontalOffste - horizontalCornerDistance,
				gap.bottom - vertical + verticalOffset,
				paint
		);

	}

	public static Paint getFramelinePaint(int color , boolean dottedLine, int stroke) {
		Paint framelinePaint = new Paint();
		framelinePaint.setAntiAlias(true);
		framelinePaint.setColor(color);
		framelinePaint.setStyle(Paint.Style.STROKE);
		int baseStroke = 2;
		framelinePaint.setStrokeWidth( baseStroke * stroke);
		if(dottedLine) {
			framelinePaint.setPathEffect(new DashPathEffect(new float[] {20f, 15f}, 0f));
		}
		return framelinePaint;
	}

	public static Paint getFramelineBackgroundPaint(int stroke) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		int baseStroke = 2;
		paint.setStrokeWidth(baseStroke * stroke);
		return paint;
	}
}
