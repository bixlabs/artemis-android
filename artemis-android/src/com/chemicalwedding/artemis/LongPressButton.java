package com.chemicalwedding.artemis;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

public class LongPressButton extends ImageView {

	private ClickBoolean clickDown = new ClickBoolean();

	public LongPressButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LongPressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LongPressButton(Context context) {
		super(context);
	}
	
	public ClickBoolean getClickBoolean() {
		return clickDown;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		cancelLongpressIfRequired(event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		cancelLongpressIfRequired(event);
		return super.onTrackballEvent(event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
				|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
			cancelLongpress();
		}
		return super.onKeyUp(keyCode, event);
	}

	private void cancelLongpressIfRequired(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			cancelLongpress();
		}
	}

	private void cancelLongpress() {
		clickDown.setDown(false);
	}

	class ClickBoolean {
		private boolean mDown;

		public void setDown(boolean val) {
			mDown = val;
		}

		public boolean isDown() {
			return mDown == true;
		}
	}
}