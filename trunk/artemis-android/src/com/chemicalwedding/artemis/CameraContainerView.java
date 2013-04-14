package com.chemicalwedding.artemis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CameraContainerView extends LinearLayout {

	public CameraContainerView(Context context, AttributeSet attr) {
		super(context, attr);

		setWillNotDraw(false);
	}

	private Rect bgRect = new Rect();

	@Override
	protected void onDraw(Canvas canvas) {
		if (ArtemisActivity.arrowBackgroundImage != null) {
			if (ArtemisMath.getInstance().getCurrentGreenBox() != null) {
				canvas.drawBitmap(ArtemisActivity.arrowBackgroundImage, null,
						ArtemisMath.getInstance().getCurrentGreenBox(), null);
			} else {
				bgRect.right = canvas.getWidth();
				bgRect.bottom = canvas.getHeight();
				canvas.drawBitmap(ArtemisActivity.arrowBackgroundImage, null,
						bgRect, null);
			}
		}
		super.onDraw(canvas);
	}
}
