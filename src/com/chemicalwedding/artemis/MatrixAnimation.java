package com.chemicalwedding.artemis;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class MatrixAnimation extends Animation {

	private Matrix startMatrix;
	private float mScaleX, mScaleY, mTranslateX, mTranslateY;
	private float endScaleX, endScaleY, endTranslateX, endTranslateY;
	private float buf[];

	public MatrixAnimation() {
	}

	public MatrixAnimation(Matrix initialTransform) {
		buf = new float[9];
		startMatrix = initialTransform;
		startMatrix.getValues(buf);
		mScaleX = buf[Matrix.MSCALE_X];
		mScaleY = buf[Matrix.MSCALE_Y];
		mTranslateX = buf[Matrix.MTRANS_X];
		mTranslateY = buf[Matrix.MTRANS_Y];

	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		final Matrix matrix = t.getMatrix();
		
		matrix.getValues(buf);
		endScaleX = buf[Matrix.MSCALE_X];
		endScaleY = buf[Matrix.MSCALE_Y];
		endTranslateX = buf[Matrix.MTRANS_X];
		endTranslateY = buf[Matrix.MTRANS_Y];
//
		matrix.setTranslate(LinearInterpolate(mTranslateX, endTranslateX, interpolatedTime), LinearInterpolate(mTranslateY, endTranslateY, interpolatedTime));
		matrix.setScale(LinearInterpolate(mScaleX, endScaleX, interpolatedTime), LinearInterpolate(mScaleY, endScaleY, interpolatedTime));
		
		
		//matrix.setScale(1+1*interpolatedTime, 1+1*interpolatedTime);
	}

	float LinearInterpolate(float y1, float y2, float mu) {
		return (y1 * (1 - mu) + y2 * mu);
	}
}
