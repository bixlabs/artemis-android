package com.chemicalwedding.artemis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PreviewSideBar extends ImageView {

	private Paint _paint = new Paint();
	private ArtemisMath _artemisMath = ArtemisMath.getInstance();

	public PreviewSideBar(Context context, AttributeSet attr) {
		super(context, attr);

		_paint.setStyle(Paint.Style.STROKE);
		_paint.setAntiAlias(true);
		_paint.setTextSize(14f);
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for (ArtemisRectFTextLabel lensLabel : _artemisMath
				.get_currentLensTextLabels()) {
			_paint.setColor(lensLabel.getTextColor());
			canvas.drawText(lensLabel.getLabelText(), lensLabel.getPositionX(),
					lensLabel.getPositionY(), _paint);
		}
	}
}
