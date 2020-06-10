package com.chemicalwedding.artemis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("NewApi")
public class CameraAngleDetailView extends View {

	private Paint _paint = new Paint();
	private ArtemisMath _artemisMath = ArtemisMath.getInstance();
	private NumberFormat numberFormat = new DecimalFormat("###.#");

	private float redAngleCircleRadius;
	private float circleCenterX;
	private float circleCenterY;
	private float HVangleX;
	private float HVangleY;
	private float angleValStringX;
	private float angleValStringY;
	final private String hAngleString, vAngleString;
	final private String degreeSymbol;
	private RectF redCircleArcRect;
	private Float HVAngleVal;
	private String angleString;
	private float angleDatas[];
	private String HVAngleText;
	private float whiteCircleRadius;
	private float HVAngleTextSize;
	private float angleValTextSize;
	private float vertCircleOffsetX;
	private float vertCircleOffsetY;
	int mHeight, mWidth;

	public CameraAngleDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);

		hAngleString = context.getString(R.string.hangle_text).toUpperCase(Locale.getDefault());
		vAngleString = context.getString(R.string.vangle_text).toUpperCase(Locale.getDefault());
		degreeSymbol = context.getString(R.string.degree_symbol);

		_paint.setAntiAlias(true);
		_paint.setTypeface(Typeface.DEFAULT_BOLD);
		_paint.setStyle(Style.FILL_AND_STROKE);
		_paint.setTextAlign(Align.CENTER);
		_paint.setColor(getResources().getColor(R.color.orangeArtemisText));
		setWillNotDraw(false);
		setClickable(true);

		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(LAYER_TYPE_HARDWARE, null);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		HVAngleText = _artemisMath.isHAngleMode() ? hAngleString : vAngleString;
		angleDatas = _artemisMath.getSelectedLensAngleData();
		angleString = "";
		HVAngleVal = null;
		if (angleDatas != null) {
			HVAngleVal = _artemisMath.isHAngleMode() ? angleDatas[0]
					: angleDatas[1];
			angleString = numberFormat.format(HVAngleVal) + degreeSymbol;

			_paint.setTextSize(angleValTextSize);
			canvas.drawText(angleString, angleValStringX, angleValStringY,
					_paint);

			_paint.setTextSize(HVAngleTextSize);
			canvas.drawText(HVAngleText, HVangleX, HVangleY, _paint);

			if (_artemisMath.isHAngleMode()) {
				canvas.rotate(-90, circleCenterX, circleCenterY);
			} else {
				canvas.translate(vertCircleOffsetX, vertCircleOffsetY);
			}

			_paint.setColor(getResources().getColor(R.color.gray));
			canvas.drawArc(redCircleArcRect, -HVAngleVal / 2, HVAngleVal, true,
					_paint);
			canvas.drawCircle(circleCenterX, circleCenterY, whiteCircleRadius,
					_paint);
			_paint.setColor(getResources().getColor(R.color.orangeArtemisText));
//			canvas.restore();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;

		redAngleCircleRadius = 0.55f * mHeight;
		circleCenterX = 0.7f * mWidth;
		circleCenterY = 0.7f * mHeight;
		vertCircleOffsetX = -0.1f * mWidth;
		vertCircleOffsetY = -0.2f * mHeight;

		whiteCircleRadius = 0.07f * mHeight;
		angleValStringX = 0.40f * mWidth;
		angleValStringY = 0.45f * mHeight;
		angleValTextSize = 0.45f * mHeight;
		HVangleX = 0.40f * mWidth;
		HVangleY = 0.78f * mHeight;
		HVAngleTextSize = 0.3f * mHeight;

		redCircleArcRect = new RectF(circleCenterX - redAngleCircleRadius,
				circleCenterY - redAngleCircleRadius, circleCenterX
						+ redAngleCircleRadius, circleCenterY
						+ redAngleCircleRadius);

	}
}
