package com.chemicalwedding.artemis;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.chemicalwedding.artemis.LongPressButton.ClickBoolean;
import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.chemicalwedding.artemis.database.CustomCamera;

public class CustomCameraCalibrationActivity extends Activity implements
		SurfaceTextureListener {
	private static final float WALL_DISTANCE = 1000f;

	private float chipWidth;
	private float chipHeight;
	private float aspectRatio, squeezeRatio, focalLength;
	private float largestViewableFocalLength;
	private String cameraName;
	private TextView chipWidthView, chipHeightView;

	private NumberFormat chipSizeFormat;
	private CustomCameraPreview customCameraPreview;

	private ArtemisDatabaseHelper mDBHelper;
	private Handler mUiHandler = new Handler();
	private ClickBoolean nextClick, nextFineClick, prevClick, prevFineClick;
	protected static final long lensRepeatSpeed = 35;
	private Camera mCamera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.calibrate_custom_camera);

		mDBHelper = new ArtemisDatabaseHelper(this);

		customCameraPreview = (CustomCameraPreview) findViewById(R.id.customCameraPreview);

		aspectRatio = getIntent().getFloatExtra("ratio", 1.78f);
		squeezeRatio = getIntent().getFloatExtra("squeeze", 1f);
		focalLength = getIntent().getFloatExtra("focalLength", 50);
		cameraName = getIntent().getStringExtra("name");
		if (cameraName == null || cameraName.length() == 0) {
			cameraName = getString(R.string.untitle_custom_cam);
		}

		NumberFormat flFormat = NumberFormat.getInstance();
		flFormat.setMaximumIntegerDigits(4);
		flFormat.setMaximumFractionDigits(1);
		((Button) findViewById(R.id.calibrateFocalLength)).setText(flFormat
				.format(focalLength) + "mm");

		chipSizeFormat = NumberFormat.getInstance();
		chipSizeFormat.setMaximumFractionDigits(6);
		chipSizeFormat.setMinimumFractionDigits(2);

		chipWidthView = (TextView) findViewById(R.id.actualChipWidth);
		chipHeightView = (TextView) findViewById(R.id.actualChipHeight);

		chipWidth = 24.96f;
		calculateChipDimensions();
		createOrangeBox();

		Log.v("CustomCameraCalibration", String.format(
				"Initial chip width: %f Height: %f LargestMM: %f", chipWidth,
				chipHeight, largestViewableFocalLength));

		mUiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				scalePreview();
			}
		}, 10);

		bindViewEvents();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		TextureView textureView = new TextureView(this);
		textureView.setSurfaceTextureListener(this);
		customCameraPreview.setTextureView(textureView);
		customCameraPreview.addView(textureView);
	}

	private final Runnable prevRunnable = new Runnable() {
		public void run() {
			if (prevClick.isDown()) {
				previous();
				mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
						+ lensRepeatSpeed);

			}
		}
	};

	private final Runnable prevFineRunnable = new Runnable() {
		public void run() {
			if (prevFineClick.isDown()) {
				previousFine();
				mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
						+ lensRepeatSpeed);

			}
		}
	};

	private final Runnable nextRunnable = new Runnable() {
		public void run() {
			if (nextClick.isDown()) {
				next();
				mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
						+ lensRepeatSpeed);
			}
		}
	};

	private final Runnable nextFineRunnable = new Runnable() {
		public void run() {
			if (nextFineClick.isDown()) {
				nextFine();
				mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
						+ lensRepeatSpeed);

			}
		}
	};

	protected void previous() {
		chipWidth += 1.0f;
		calculateChipDimensions();
		scalePreview();
	}

	protected void previousFine() {
		chipWidth += 0.2f;
		calculateChipDimensions();
		scalePreview();
	}

	protected void next() {
		chipWidth -= 1.0f;
		calculateChipDimensions();
		scalePreview();
	}

	protected void nextFine() {
		chipWidth -= 0.2f;
		calculateChipDimensions();
		scalePreview();
	}

	private void bindViewEvents() {
		LongPressButton prevButton = (LongPressButton) findViewById(R.id.prevButton);
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				previous();
			}
		});
		prevButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				prevClick.setDown(true);
				mUiHandler.post(prevRunnable);
				// if (isHapticFeedbackEnabled) {
				// buzz(_lensFocalLengthText);
				// }
				return true;
			}
		});

		LongPressButton finePrevButton = (LongPressButton) findViewById(R.id.finePrevButton);
		finePrevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				previousFine();
			}
		});
		finePrevButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				prevFineClick.setDown(true);
				mUiHandler.post(prevFineRunnable);
				// if (isHapticFeedbackEnabled) {
				// buzz(_lensFocalLengthText);
				// }
				return true;
			}
		});

		LongPressButton nextButton = (LongPressButton) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				next();
			}
		});
		nextButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				nextClick.setDown(true);
				mUiHandler.post(nextRunnable);
				// if (isHapticFeedbackEnabled) {
				// buzz(_lensFocalLengthText);
				// }
				return true;
			}
		});

		LongPressButton fineNextButton = (LongPressButton) findViewById(R.id.fineNextButton);
		fineNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextFine();
			}
		});
		fineNextButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				nextFineClick.setDown(true);
				mUiHandler.post(nextFineRunnable);
				// if (isHapticFeedbackEnabled) {
				// buzz(_lensFocalLengthText);
				// }
				return true;
			}
		});

		nextClick = nextButton.getClickBoolean();
		prevClick = prevButton.getClickBoolean();
		nextFineClick = fineNextButton.getClickBoolean();
		prevFineClick = finePrevButton.getClickBoolean();

		findViewById(R.id.calibrateSaveButton).setOnClickListener(
				addCustomCameraClickListener);

		findViewById(R.id.calibrateFocalLength).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
	}

	public float[] calculateViewingAngle(float lensFocalLength) {
		float Hfraction = (chipWidth * squeezeRatio) / (2 * lensFocalLength);
		float Vfraction = (chipHeight * 1) / (2 * lensFocalLength);
		float HviewingAngle = (float) (2 * (Math
				.toDegrees(Math.atan(Hfraction))));
		float VviewingAngle = (float) (2 * (Math
				.toDegrees(Math.atan(Vfraction))));
		float HStandard = calculateWidthAndHeightLens(HviewingAngle);
		float VStandard = calculateWidthAndHeightLens(VviewingAngle);
		float hprop = (HStandard / ArtemisMath.deviceHorizontalWidth);
		float vprop = (VStandard / ArtemisMath.deviceVerticalWidth);
		float[] angles = { HviewingAngle, VviewingAngle, HStandard, VStandard,
				hprop, vprop };
		return angles;
	}

	protected void scalePreview() {

		float[] angles = calculateViewingAngle(focalLength);

		float hprop = angles[4];
		// float vprop = angles[5];

		float hWidth = angles[2];
		// float vHeight = angles[3];
		// float myprop = hWidth / vHeight;

		// float hwidth = (ArtemisMath.scaledPreviewWidth * angles[0] /
		// ArtemisMath.horizViewAngle);
		// ArtemisRectF currentGreenBox = ArtemisMath.getInstance()
		// .getCurrentGreenBox();
		// if (myprop < 1.4f) {
		// hwidth *= currentGreenBox.width() / ArtemisMath.scaledPreviewWidth;
		// }

		customCameraPreview.scaleFactor = 1 / hprop;

		Log.v("CustomCameraCalibration", String.format(
				"CustomCam %f hWidth: %f hangle: %f",
				customCameraPreview.scaleFactor, hWidth, angles[0]));
		customCameraPreview.calculateZoom(false);
	}

	private void createOrangeBox() {
		DisplayMetrics metrics = CustomCameraCalibrationActivity.this
				.getResources().getDisplayMetrics();
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;

		int lowerMargin = (int) (screenHeight * 0.885f);
		int topMargin = (int) (screenHeight * 0.05f);

		float myprop = aspectRatio;
		int maximumWidth = (int) ((lowerMargin - topMargin) * myprop);

		// center 4:3 view
		int xmin = (screenWidth - maximumWidth) / 2;
		int xmax = xmin + maximumWidth;
		ArtemisRectF greenBox = new ArtemisRectF("", xmin, topMargin, xmax,
				lowerMargin);
		greenBox.setColor(ArtemisMath.orangeBoxColor);

		ArrayList<ArtemisRectF> boxes = ArtemisMath.getInstance()
				.get_currentLensBoxes();
		boxes.clear();
		ArtemisMath.getInstance().setCurrentGreenBox(greenBox);

		ArtemisRectF blackBox1 = new ArtemisRectF(null, 0, 0, xmin,
				screenHeight);
		ArtemisRectF blackBox2 = new ArtemisRectF(null, xmax, 0, screenWidth,
				screenHeight);

		ArtemisRectF blackBox3 = new ArtemisRectF(null, 0, 0, screenWidth,
				topMargin);
		blackBox3.setColor(Color.BLACK);

		ArtemisRectF blackBox4 = new ArtemisRectF(null, 0, lowerMargin,
				screenWidth, screenHeight);
		blackBox4.setColor(Color.BLACK);

		boxes.add(blackBox1);
		boxes.add(blackBox2);
		boxes.add(blackBox3);
		boxes.add(blackBox4);

	}

	private static float calculateWidthAndHeightLens(float angle) {
		float updown = (float) (WALL_DISTANCE * 2 * (Math.tan(Math
				.toRadians(angle / 2))));
		return updown;
	}

	protected void calculateChipDimensions() {
		chipHeight = chipWidth / aspectRatio;
		updateChipTextViews();
	}

	private void updateChipTextViews() {
		chipWidthView.setText(chipSizeFormat.format(chipWidth));
		chipHeightView.setText(chipSizeFormat.format(chipHeight));
	}

	private final OnClickListener addCustomCameraClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				// insert the new camera
				CustomCamera customCam = new CustomCamera();
				customCam.setSensorheight(chipHeight);
				customCam.setSensorwidth(chipWidth);
				customCam.setName(cameraName);
				customCam.setSqueeze(squeezeRatio);
				mDBHelper.insertCustomCamera(customCam);
				Intent intent = new Intent();
				intent.putExtra("newcustomcam", true);
				setResult(Activity.RESULT_OK, intent);
				finish();
			} catch (Exception e) {
				e.printStackTrace();
				AlertDialog errorDialog = new AlertDialog.Builder(
						CustomCameraCalibrationActivity.this)
						.setTitle(R.string.custom_camera_error)
						.setMessage(R.string.custom_camera_error_message)
						.setNegativeButton(R.string.ok, null).create();
				errorDialog.show();
			}
		}

	};

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		// Set the background
		if (ArtemisActivity.arrowBackgroundImage == null) {
			Options o = new Options();
			o.inSampleSize = 2;
			ArtemisActivity.arrowBackgroundImage = BitmapFactory
					.decodeResource(getResources(), R.drawable.arrows, o);
		}

		mCamera = android.hardware.Camera.open();

		try {
			mCamera.setPreviewTexture(surface);
		} catch (IOException t) {
		}

		customCameraPreview.openCamera(mCamera);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// Invoked every time there's a new Camera preview frame
	}
}
