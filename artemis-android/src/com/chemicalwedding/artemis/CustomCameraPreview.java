package com.chemicalwedding.artemis;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
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

	}

	public void setTextureView(TextureView view) {
		mTextureView = view;
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

	public void openCamera(Camera camera) {

		mCamera = camera;

		if (mCamera != null) {

			Camera.Parameters parameters = mCamera.getParameters();
			SharedPreferences artemisPrefs = getContext()
					.getApplicationContext().getSharedPreferences(
							ArtemisPreferences.class.getSimpleName(),
							Context.MODE_PRIVATE);

			// init camera preferences for artemis settings
			int min = parameters.getMinExposureCompensation();
			int max = parameters.getMaxExposureCompensation();
			CameraPreview21.exposureStep = parameters
					.getExposureCompensationStep();
			Log.v(logTag, "Camera Exposure min: " + min + " max: " + max);
			CameraPreview21.supportedExposureLevels = new ArrayList<Integer>();
			int defaultExposure = 0;
			for (int i = min; i <= max; i++) {
				CameraPreview21.supportedExposureLevels.add(i);
			}
			if (CameraPreview21.blackAndWhitePreview) {
				Log.i(logTag, "BLACK AND WHITE ON");
				parameters.setColorEffect(Parameters.EFFECT_MONO);
			} else {
				parameters.setColorEffect(Parameters.EFFECT_NONE);
			}

			CameraPreview21.supportedWhiteBalance = parameters
					.getSupportedWhiteBalance();
			CameraPreview21.supportedFlashModes = parameters
					.getSupportedFlashModes();

			if (CameraPreview21.supportedFlashModes != null) {
				Log.v(logTag, "Supported Flash modes: ");
				for (String flashMode : CameraPreview21.supportedFlashModes) {
					Log.v(logTag, flashMode);
				}

				boolean flashEnabled = artemisPrefs.getBoolean(
						ArtemisPreferences.FLASH_ENABLED, false);
				if (flashEnabled) {
					parameters.setFlashMode("torch");
				} else
					parameters.setFlashMode("off");

			}

			int exposureLevelIndex = artemisPrefs
					.getInt(ArtemisPreferences.SELECTED_EXPOSURE_LEVEL,
							defaultExposure);
			Log.v(logTag, String.format(
					"Selected exposure compensation index: %d",
					exposureLevelIndex));
			parameters.setExposureCompensation(exposureLevelIndex);

			String whiteBalance = artemisPrefs.getString(
					ArtemisPreferences.SELECTED_WHITE_BALANCE, "");
			if (whiteBalance.length() > 0) {
				parameters.setWhiteBalance(whiteBalance);
			}

			CameraPreview21.supportedFocusModes = parameters
					.getSupportedFocusModes();
			if (CameraPreview21.supportedFocusModes != null) {
				String focusMode = artemisPrefs.getString(
						ArtemisPreferences.SELECTED_FOCUS_MODE, "");
				if (focusMode.length() == 0
						&& CameraPreview21.supportedFocusModes
								.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
					parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
				} else {
					parameters.setFocusMode(focusMode);
				}
			}
			CameraPreview21.autoFocusBeforePictureTake = artemisPrefs
					.getBoolean(ArtemisPreferences.AUTO_FOCUS_ON_PICTURE, false);

			CameraPreview21.supportedSceneModes = parameters
					.getSupportedSceneModes();
			if (CameraPreview21.supportedSceneModes != null) {
				parameters.setSceneMode(artemisPrefs.getString(
						ArtemisPreferences.SELECTED_SCENE_MODE, "auto"));
			}

			parameters.setPreviewSize(CameraPreview21.previewSize.getWidth(),
                    CameraPreview21.previewSize.getHeight());
			// parameters.setPictureSize(pictureSize.width, pictureSize.height);

			try {
				CameraPreview21.deviceHAngle = parameters
						.getHorizontalViewAngle();
			} catch (Exception e) {
				CameraPreview21.deviceHAngle = 51.2f;// workaround for
				// bug
				// in certain
				// Android
				// devices that crash on getting
				// angle
			} finally {
				if (CameraPreview21.deviceHAngle < 1f) {
					CameraPreview21.deviceHAngle = 51.2f;
				}
			}

			try {
				CameraPreview21.deviceVAngle = parameters
						.getVerticalViewAngle();
			} catch (Exception e) {
				CameraPreview21.deviceVAngle = 39.4f; // workaround for
				// bug
				// in certain
				// Android
				// devices that crash on getting
				// angle
			} finally {
				if (CameraPreview21.deviceVAngle < 1f) {
					CameraPreview21.deviceVAngle = 39.4f;
				}
			}

			if (!artemisPrefs.getBoolean(
					getContext().getString(
							R.string.preference_key_automaticlensangles), true)) {
				CameraPreview21.effectiveHAngle = artemisPrefs.getFloat(
						ArtemisPreferences.CAMERA_LENS_H_ANGLE,
						CameraPreview21.deviceHAngle);
				CameraPreview21.effectiveVAngle = artemisPrefs.getFloat(
						ArtemisPreferences.CAMERA_LENS_V_ANGLE,
						CameraPreview21.deviceVAngle);
			} else {
				CameraPreview21.effectiveHAngle = CameraPreview21.deviceHAngle;
				CameraPreview21.effectiveVAngle = CameraPreview21.deviceVAngle;
			}

			mCamera.setParameters(parameters);

			// Start the preview
			mCamera.startPreview();

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

			final int previewWidth = CameraPreview21.previewSize.getWidth();
			final int previewHeight = CameraPreview21.previewSize.getHeight();

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
