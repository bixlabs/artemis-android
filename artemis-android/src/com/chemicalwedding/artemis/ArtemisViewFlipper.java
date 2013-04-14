package com.chemicalwedding.artemis;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class ArtemisViewFlipper extends ViewFlipper {

	public ArtemisViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDetachedFromWindow() {
	    try {
	        super.onDetachedFromWindow();
	    }
	    catch (IllegalArgumentException e) {
	        stopFlipping();
	    }
	}
}
