package com.chemicalwedding.artemis;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

public class CustomCameraPreview extends ViewGroup {

	private Camera mCamera;
	private TextureView mTextureView;
	private static final String logTag = "CustomCameraPreview";
	private int totalScreenWidth;
	float scaleFactor = 1;

	private ArtemisMath _artemisMath = ArtemisMath.getInstance();

	public CustomCameraPreview(Context context, AttributeSet attr) {
		super(context, attr);

		totalScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

		mTextureView = new MyTextureView(context);
		addView(mTextureView);
	}

	class MyTextureView extends TextureView {

		private CameraTextureListener mTextureListener;

		public MyTextureView(Context context) {
			super(context);

			mTextureListener = new CameraTextureListener();
			this.setSurfaceTextureListener(mTextureListener);
		}

		public class CameraTextureListener implements
				TextureView.SurfaceTextureListener {
			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface,
					int width, int height) {
				// Set the background

				Options o = new Options();
				o.inSampleSize = 2;
				ArtemisActivity.arrowBackgroundImage = BitmapFactory
						.decodeResource(getResources(), R.drawable.arrows, o);

				openCamera();
			}

			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
					int width, int height) {
				// Ignored, Camera does all the work for us
			}

			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				releaseCamera();

				ArtemisActivity.arrowBackgroundImage.recycle();
				ArtemisActivity.arrowBackgroundImage = null;

				return true;
			}

			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {
				// Invoked every time there's a new Camera preview frame
			}

		}

	}

	private Camera openFrontFacingCameraGingerbread() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK
					|| cameraCount == 1) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
					Log.e(logTag,
							"Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}

		return cam;
	}

	public void calculateZoom(boolean shouldCalcScaleFactor) {
		// Calculate the zoom scale and translation
		Matrix transform = new Matrix();

		transform.postTranslate(_artemisMath.getCurrentGreenBox().left
				- (ArtemisMath.scaledPreviewWidth - _artemisMath
						.getCurrentGreenBox().width()) / 2f,
				_artemisMath.getCurrentGreenBox().top
						- (ArtemisMath.scaledPreviewHeight - _artemisMath
								.getCurrentGreenBox().height()) / 2f);
		transform.postScale(scaleFactor, scaleFactor, _artemisMath
				.getCurrentGreenBox().centerX(), _artemisMath
				.getCurrentGreenBox().centerY());
		Log.v(logTag, "Zoom ScaleFactor: " + scaleFactor);

		mTextureView.setTransform(transform);
	}

	public void openCamera() {

		mCamera = openFrontFacingCameraGingerbread();

		if (mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			SharedPreferences artemisPrefs = getContext()
					.getApplicationContext().getSharedPreferences(
							ArtemisPreferences.class.getSimpleName(),
							Context.MODE_PRIVATE);

			// init camera preferences for artemis settings
			int min = parameters.getMinExposureCompensation();
			int max = parameters.getMaxExposureCompensation();
			Log.v(logTag, "Camera Exposure min: " + min + " max: " + max);
			if (CameraPreview14.supportedFlashModes != null) {
				Log.v(logTag, "Supported Flash modes: ");
				for (String flashMode : CameraPreview14.supportedFlashModes) {
					Log.v(logTag, flashMode);
				}

				boolean flashEnabled = artemisPrefs.getBoolean(
						ArtemisPreferences.FLASH_ENABLED, false);
				if (flashEnabled) {
					parameters.setFlashMode("torch");
				} else
					parameters.setFlashMode("off");

			}
			int exposureLevelIndex = artemisPrefs.getInt(
					ArtemisPreferences.SELECTED_EXPOSURE_LEVEL, 0);
			if (exposureLevelIndex > 0) {
				parameters.setExposureCompensation(exposureLevelIndex);
			}
			String whiteBalance = artemisPrefs.getString(
					ArtemisPreferences.SELECTED_WHITE_BALANCE, "");
			if (whiteBalance.length() > 0) {
				parameters.setWhiteBalance(whiteBalance);
			}

			Log.v(logTag, "Preview size selected: "
					+ CameraPreview14.previewSize.width + "x"
					+ CameraPreview14.previewSize.height);
			parameters.setPreviewSize(CameraPreview14.previewSize.width,
					CameraPreview14.previewSize.height);
			mCamera.setParameters(parameters);

			try {
				mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
				mCamera.startPreview();
			} catch (IOException ioe) {
				// Something bad happened
			}
		}
	}

	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	public void setArtemisMath(ArtemisMath artemisMath) {
		_artemisMath = artemisMath;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (getChildCount() > 0 && changed) {
			Log.v(logTag, "** Laying out Camera Preview");

			final View child = getChildAt(0);

			final int previewWidth = CameraPreview14.previewSize.width;
			final int previewHeight = CameraPreview14.previewSize.height;

			Log.v(logTag, "Preview width: " + previewWidth + " height:"
					+ previewHeight);

			final float REQUESTED_WIDTH_RATIO = 1f;
			int requestedWidth = (int) (totalScreenWidth * REQUESTED_WIDTH_RATIO);
			int scaledPreviewWidth = requestedWidth;

			final float previewRatio = (float) previewWidth / previewHeight;

			int scaledPreviewHeight = Math.round(requestedWidth / previewRatio);

			Log.v(logTag, "Scaled preview width: " + scaledPreviewWidth
					+ " scaled height:" + scaledPreviewHeight);

			child.layout(0, 5, scaledPreviewWidth, scaledPreviewHeight + 5);

			Log.v(logTag, "Scaled Preview width: " + scaledPreviewWidth
					+ " height:" + scaledPreviewHeight);

			Log.v(logTag, "** Finished layout of Camera Preview");
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

}
