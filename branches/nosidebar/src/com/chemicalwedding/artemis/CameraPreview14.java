package com.chemicalwedding.artemis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CameraPreview14 extends ViewGroup {

	private Camera mCamera;
	private TextureView mTextureView;
	private static final String logTag = "CameraPreview";

	private ArtemisMath _artemisMath = ArtemisMath.getInstance();
	private int frameBufSize;
	protected volatile byte[] frameBuffer;
	protected volatile int[] tempFrameBitmap; // holds decoded RGB 565 image
	protected volatile ByteBuffer currentFrame;

	protected float scaleFactor = 1f, zoomCenterX, zoomCenterY;
	protected float digital = 1f;
	private int totalScreenHeight;
	private int totalScreenWidth;
	protected static boolean widthAndHeightSwapped = false;

	protected int previewHeight, previewWidth, scaledPreviewWidth,
			scaledPreviewHeight, requestedWidthDiff = 0;;
	protected Bitmap bitmapToSave;
	protected float diffScaleFactor = 0f, diffZoomCenterX = 0f,
			diffZoomCenterY = 0f;

	protected Bitmap arrowLeft;
	protected Bitmap arrowRight;
	protected Bitmap arrowTop;
	protected Bitmap arrowBottom;
	private boolean isTablet = false;
	private ArtemisApplication artemisApplication;
	private final float pixelDensity;
	private final String degreeSymbolFromStringsXML;

	public CameraPreview14(Context context, AttributeSet attr) {
		super(context, attr);

		artemisApplication = ((ArtemisApplication) (((Activity) context)
				.getApplication()));

		mTextureView = new MyTextureView(context);
		addView(mTextureView);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		totalScreenWidth = size.x;
		totalScreenHeight = size.y;
		if (totalScreenHeight > totalScreenWidth) {
			// swap!
			totalScreenWidth = size.y;
			totalScreenHeight = size.x;
		}

		int screensize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		if (screensize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screensize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			isTablet = true;
		}

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		pixelDensity = metrics.density;

		degreeSymbolFromStringsXML = context.getString(R.string.degree_symbol);
	}

	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void enablePreviewCallback() {
	}

	public void disablePreviewCallback() {
	}

	public void startConversionThread() {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(logTag, "CameraPreview Surface Destroyed.");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	}

	private void initPreviewSizes(Camera.Parameters parameters) {
		ArtemisSettingsActivity.supportedPreviewSizes = parameters
				.getSupportedPreviewSizes();
		Iterator<Size> sizeIter = ArtemisSettingsActivity.supportedPreviewSizes
				.iterator();
		while (sizeIter.hasNext()) {
			Size size = sizeIter.next();
			double ratio = (double) size.width / size.height;
			if (size.width < 200) {
				sizeIter.remove();
			}
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		// final double ASPECT_TOLERANCE = 0.3;
		// double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetWidth = w;
		// keep it under 800 pixel max when selecting automatically
		// (For tablets... any bigger os too much at the moment)
		// TODO: this might be unnecessary in the future
		if (Build.VERSION.SDK_INT >= 11 && targetWidth > 640) {
			targetWidth = 640;
		}

		// Try to find an size match aspect ratio and size
		// for (Size size : sizes) {
		// double ratio = (double) size.width / size.height;
		// if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
		// continue;
		// if (Math.abs(size.width - targetWidth) < minDiff) {
		// optimalSize = size;
		// minDiff = Math.abs(size.width - targetWidth);
		// }
		// }

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.width - targetWidth) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.width - targetWidth);
				}
			}
		}
		return optimalSize;
	}

	public void takePicture() {
		if (ArtemisSettingsActivity.autoFocusBeforePictureTake) {
			mCamera.autoFocus(new AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					artemisApplication.postDelayedOnWorkerThread(
							new Runnable() {

								@Override
								public void run() {
									mCamera.setOneShotPreviewCallback(new TakePicturePreviewCallback());
								}
							}, 100);
				}

			});
		} else {
			artemisApplication.postDelayedOnWorkerThread(new Runnable() {

				@Override
				public void run() {
					mCamera.setOneShotPreviewCallback(new TakePicturePreviewCallback());
				}
			}, 100);
		}
	}

	class TakePicturePreviewCallback implements PreviewCallback {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			decodeYUV420SP2(tempFrameBitmap, data, previewWidth, previewHeight);

			RectF selectedRect = _artemisMath.getSelectedLensBox();
			RectF greenRect = _artemisMath.getCurrentGreenBox();

			bitmapToSave = Bitmap.createBitmap(tempFrameBitmap, previewWidth,
					previewHeight, Bitmap.Config.RGB_565);
			
			final int imageHeight = 610;
			
			if (scaleFactor >= 1) {

				bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
						scaledPreviewWidth, scaledPreviewHeight,
						ArtemisSettingsActivity.smoothImagesEnabled);

				bitmapToSave = Bitmap.createBitmap(bitmapToSave,
						(int) (selectedRect.left), (int) (selectedRect.top),
						(int) (selectedRect.width()),
						(int) (selectedRect.height()));

				float ratio = selectedRect.width() / selectedRect.height();
				bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
						(int) (imageHeight * ratio), imageHeight,
						ArtemisSettingsActivity.smoothImagesEnabled);
			}
			// We need to scale down
			else {
				Log.d(logTag, "Scale down on save");
				float ratio = greenRect.width() / greenRect.height();
				int newwidth = (int) (imageHeight * ratio);
				Bitmap canvasBitmap = Bitmap.createBitmap(newwidth, imageHeight,
						Bitmap.Config.RGB_565);
				Canvas c = new Canvas(canvasBitmap);
				Paint p = new Paint();

				float width = newwidth * _artemisMath.horizViewAngle
						/ _artemisMath.selectedLensAngleData[0];
				float previewRatio = (float) previewHeight / previewWidth;
				bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
						(int) (width), (int) (width * previewRatio),
						ArtemisSettingsActivity.smoothImagesEnabled);

				int xpos = (newwidth - bitmapToSave.getWidth()) / 2;
				int ypos = (imageHeight - bitmapToSave.getHeight()) / 2;
				c.drawBitmap(bitmapToSave, xpos, ypos, p);
				c.drawBitmap(arrowLeft, null, new Rect(xpos - 300, ypos, xpos,
						ypos + bitmapToSave.getHeight()), p);
				c.drawBitmap(
						arrowRight,
						null,
						new Rect(xpos + bitmapToSave.getWidth(), ypos, xpos
								+ bitmapToSave.getWidth() + 300, bitmapToSave
								.getHeight() + ypos), p);
				c.drawBitmap(arrowTop, null, new Rect(xpos, ypos - 300, xpos
						+ bitmapToSave.getWidth(), ypos), p);
				c.drawBitmap(
						arrowBottom,
						null,
						new Rect(xpos, ypos + bitmapToSave.getHeight(), xpos
								+ bitmapToSave.getWidth(), ypos
								+ bitmapToSave.getHeight() + 300), p);

				bitmapToSave = canvasBitmap;
			}

			if (!ArtemisSettingsActivity.quickshotEnabled) {
				ArtemisActivity.pictureSavePreview.setImageBitmap(bitmapToSave);
				ArtemisActivity.viewFlipper.setInAnimation(null);
				ArtemisActivity.viewFlipper.setDisplayedChild(3);
			} else {
				final Toast toast = Toast.makeText(getContext(), getContext()
						.getString(R.string.image_saved_success),
						Toast.LENGTH_LONG);
				toast.setDuration(2500);
				toast.show();
				new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... params) {
						renderPictureDetailsAndSave();
						return "";
					}

					@Override
					protected void onPostExecute(String result) {
						toast.cancel();
						getContext()
								.sendBroadcast(
										new Intent(
												Intent.ACTION_MEDIA_MOUNTED,
												Uri.parse("file://"
														+ Environment
																.getExternalStorageDirectory())));
					}
				}.execute(new String[] {});

				enablePreviewCallback();
			}
		}
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
				return true;
			}

			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {
				// Invoked every time there's a new Camera preview frame
			}

		}

	}
	
	private Camera openFrontFacingCameraGingerbread() 
	{
	    int cameraCount = 0;
	    Camera cam = null;
	    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    cameraCount = Camera.getNumberOfCameras();
	    for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
	        Camera.getCameraInfo( camIdx, cameraInfo );
	        if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK || cameraCount == 1 ) {
	            try {
	                cam = Camera.open( camIdx );
	            } catch (RuntimeException e) {
	                Log.e(logTag, "Camera failed to open: " + e.getLocalizedMessage());
	            }
	        }
	    }

	    return cam;
	}

	public void openCamera() {
		
		mCamera = openFrontFacingCameraGingerbread();
		
		Camera.Parameters parameters = mCamera.getParameters();
		SharedPreferences artemisPrefs = getContext().getApplicationContext()
				.getSharedPreferences(ArtemisPreferences.class.getSimpleName(),
						Context.MODE_PRIVATE);

		// init camera preferences for artemis settings
		int min = parameters.getMinExposureCompensation();
		int max = parameters.getMaxExposureCompensation();
		Log.v(logTag, "Camera Exposure min: " + min + " max: " + max);
		ArtemisSettingsActivity.supportedExposureLevels = new ArrayList<Integer>();
		for (int i = max; i >= min; i--) {
			ArtemisSettingsActivity.supportedExposureLevels.add(i);
		}
		ArtemisSettingsActivity.supportedWhiteBalance = parameters
				.getSupportedWhiteBalance();
		ArtemisSettingsActivity.supportedFlashModes = parameters
				.getSupportedFlashModes();
		if (ArtemisSettingsActivity.supportedFlashModes != null) {
			Log.v(logTag, "Supported Flash modes: ");
			for (String flashMode : ArtemisSettingsActivity.supportedFlashModes) {
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
		mCamera.setParameters(parameters);

		if (ArtemisSettingsActivity.supportedPreviewSizes == null) {
			initPreviewSizes(parameters);
		}
		Log.v(logTag, "Preview sizes supported: "
				+ ArtemisSettingsActivity.supportedPreviewSizes.size());
		for (Size size : ArtemisSettingsActivity.supportedPreviewSizes) {
			Log.v(logTag, size.width + "x" + size.height);
		}
		int selectedPreviewIndex = artemisPrefs.getInt(
				ArtemisPreferences.PREVIEW_SELECTION, -1);
		if (selectedPreviewIndex < 0) {
			Log.v(logTag, "automatically selecting best preview size");
			ArtemisSettingsActivity.previewSize = getOptimalPreviewSize(
					ArtemisSettingsActivity.supportedPreviewSizes,
					totalScreenWidth, totalScreenHeight);
		} else
			ArtemisSettingsActivity.previewSize = ArtemisSettingsActivity.supportedPreviewSizes
					.get(selectedPreviewIndex);

		Log.v(logTag, "Preview size selected: "
				+ ArtemisSettingsActivity.previewSize.width + "x"
				+ ArtemisSettingsActivity.previewSize.height);
		parameters.setPreviewSize(ArtemisSettingsActivity.previewSize.width,
				ArtemisSettingsActivity.previewSize.height);
		previewWidth = ArtemisSettingsActivity.previewSize.width;
		previewHeight = ArtemisSettingsActivity.previewSize.height;
		try {
			ArtemisSettingsActivity.deviceHAngle = parameters
					.getHorizontalViewAngle();
		} catch (Exception e) {
			ArtemisSettingsActivity.deviceHAngle = 51.2f;// workaround for bug
															// in certain
			// Android
			// devices that crash on getting
			// angle
		} finally {
			if (ArtemisSettingsActivity.deviceHAngle == 0f) {
				ArtemisSettingsActivity.deviceHAngle = 51.2f;
			}
		}

		try {
			ArtemisSettingsActivity.deviceVAngle = parameters
					.getVerticalViewAngle();
		} catch (Exception e) {
			ArtemisSettingsActivity.deviceVAngle = 39.4f; // workaround for bug
															// in certain
			// Android
			// devices that crash on getting
			// angle
		} finally {
			if (ArtemisSettingsActivity.deviceVAngle == 0f) {
				ArtemisSettingsActivity.deviceVAngle = 39.4f;
			}
		}

		ArtemisSettingsActivity.effectiveHAngle = artemisPrefs.getFloat(
				ArtemisPreferences.CAMERA_LENS_H_ANGLE,
				ArtemisSettingsActivity.deviceHAngle);
		ArtemisSettingsActivity.effectiveVAngle = artemisPrefs.getFloat(
				ArtemisPreferences.CAMERA_LENS_V_ANGLE,
				ArtemisSettingsActivity.deviceVAngle);

		// handle auto focus setup
		if (parameters.getSupportedFocusModes().contains(
				Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			ArtemisSettingsActivity.autoFocusBeforePictureTake = artemisPrefs
					.getBoolean(ArtemisPreferences.AUTO_FOCUS_ON_PICTURE, false);
		} else if (parameters.getSupportedFocusModes().contains(
				Camera.Parameters.FOCUS_MODE_AUTO)) {
			parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			ArtemisSettingsActivity.autoFocusBeforePictureTake = artemisPrefs
					.getBoolean(ArtemisPreferences.AUTO_FOCUS_ON_PICTURE, false);
		}

		Log.v(logTag,
				"Current preview format: " + parameters.getPreviewFormat());
		// Get the supported zoom ratios
		// ArtemisSettingsActivity.supportedZoomRatios =
		// parameters.getZoomRatios();
		// if (ArtemisSettingsActivity.supportedZoomRatios != null) {
		// Log.v(logTag,
		// "Zoom ratios supported: " + supportedZoomRatios.size());
		// for (Integer zoomRatios : supportedZoomRatios) {
		// Log.v(logTag, zoomRatios + "");
		// }
		// }

		// init artemis math unit
		float pixelDensityScale = getContext().getResources()
				.getDisplayMetrics().density;

//		if (!_artemisMath.isInitializedFirstTime()) {
			_artemisMath.setDeviceSpecificDetails(totalScreenWidth,
					totalScreenHeight, scaledPreviewWidth, scaledPreviewHeight,
					pixelDensityScale, ArtemisSettingsActivity.effectiveHAngle,
					ArtemisSettingsActivity.effectiveVAngle, isTablet);

			_artemisMath.calculateLargestLens();
			_artemisMath.calculateRectBoxesAndLabelsForLenses();
			_artemisMath.selectFirstMeaningFullLens();
			_artemisMath.resetTouchToCenter(); // now with green box
			_artemisMath.calculateRectBoxesAndLabelsForLenses();
			_artemisMath.setInitializedFirstTime(true);
			// this.postInvalidate();
//		}

		ArtemisActivity._lensFocalLengthText.setText(_artemisMath
				.get_selectedLensFocalLength());

		if (_artemisMath.isFullscreen()) {
			calculateZoom();
		}
		ArtemisActivity.reconfigureNextAndPreviousLensButtons();

		mCamera.setParameters(parameters);

		// initialize the buffer used for drawing the fullscreen bitmap and
		// enable preview callback
		PixelFormat p = new PixelFormat();
		PixelFormat.getPixelFormatInfo(parameters.getPreviewFormat(), p);
		frameBufSize = (previewWidth * previewHeight * p.bitsPerPixel) / 8;
		if (frameBuffer == null || frameBuffer.length != frameBufSize)
			frameBuffer = new byte[frameBufSize];
		tempFrameBitmap = new int[frameBufSize];

		try {
			mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
			mCamera.startPreview();
		} catch (IOException ioe) {
			// Something bad happened
		}

		// requestLayout();
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

	public void restartPreview() {
		try {
			mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
		} catch (IOException e) {
			e.printStackTrace();
		}
		disablePreviewCallback();
		enablePreviewCallback();
		mCamera.startPreview();
	}

	public void calculateZoom() {
		// Calculate the zoom scale and translation
		Matrix transform = new Matrix();
		if (_artemisMath.isFullscreen()) {
			this.scaleFactor = _artemisMath.calculateFullscreenZoomRatio();

			if (scaleFactor > 1f) {
				transform.setRectToRect(
						(RectF) _artemisMath.getSelectedLensBox(),
						(RectF) _artemisMath.getCurrentGreenBox(),
						Matrix.ScaleToFit.CENTER);
			} else {
				transform.postTranslate(_artemisMath.getCurrentGreenBox().left
						- (scaledPreviewWidth - _artemisMath
								.getCurrentGreenBox().width()) / 2f,
						_artemisMath.getCurrentGreenBox().top
								- (scaledPreviewHeight - _artemisMath
										.getCurrentGreenBox().height()) / 2f);
				transform.postScale(scaleFactor, scaleFactor, _artemisMath
						.getCurrentGreenBox().centerX(), _artemisMath
						.getCurrentGreenBox().centerY());
			}

		} else {
			this.scaleFactor = 1f;
		}

		Log.v(logTag, "Zoom ScaleFactor: " + scaleFactor);
		Log.v(logTag, "Selected Lens: " + _artemisMath.getSelectedLensBox());

		mTextureView.setTransform(transform);
	}

	public void savePicture(Bitmap bitmap, boolean writeEXIFlocationInfo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
		String title = sdf.format(Calendar.getInstance().getTime());
		String filePath = ArtemisActivity.savePictureFolder + "/" + title
				+ ".jpg";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			bitmap.compress(CompressFormat.JPEG, 80, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e(logTag, "Picture file could not be created");
		} catch (IOException e) {
			e.printStackTrace();
		}
		bitmap.recycle();

		if (writeEXIFlocationInfo
				&& ArtemisActivity.pictureSaveLocation != null) {
			try {
				ExifInterface ex = new ExifInterface(filePath);
				String latString = makeLatLongString(ArtemisActivity.pictureSaveLocation
						.getLatitude());
				String latRefString = makeLatStringRef(ArtemisActivity.pictureSaveLocation
						.getLatitude());
				String longString = makeLatLongString(ArtemisActivity.pictureSaveLocation
						.getLongitude());
				String longRefString = makeLonStringRef(ArtemisActivity.pictureSaveLocation
						.getLongitude());
				ex.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latString);
				ex.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
						latRefString);
				ex.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longString);
				ex.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
						longRefString);
				ex.saveAttributes();

			} catch (IOException e) {
				Log.e(logTag, "Could not open image for writing EXIF data");
			}
		}

	}

	public static String makeLatLongString(double d) {
		d = Math.abs(d);

		int degrees = (int) d;

		double remainder = d - degrees;
		int minutes = (int) (remainder * 60D);
		// really seconds * 1000
		int seconds = (int) (((remainder * 60D) - minutes) * 60D * 1000D);

		String retVal = degrees + "/1," + minutes + "/1," + seconds + "/1000";
		return retVal;
	}

	public static String makeLatStringRef(double lat) {
		return lat >= 0D ? "N" : "S";
	}

	public static String makeLonStringRef(double lon) {
		return lon >= 0D ? "E" : "W";
	}

	public void renderPictureDetailsAndSave() {
		int sideborder = 10;
		// add a larger border in 4:3
		if ((float) bitmapToSave.getWidth() / bitmapToSave.getHeight() < 1.4) {
			sideborder = 259; // to match 16:9 for 4:3
		}
		Bitmap blankBmp = Bitmap.createBitmap(bitmapToSave.getWidth()
				+ sideborder, 686, Bitmap.Config.RGB_565);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(blankBmp);
		canvas.drawColor(Color.WHITE);
		int xcord = (blankBmp.getWidth() - bitmapToSave.getWidth()) / 2;
		canvas.drawBitmap(bitmapToSave, xcord, 5, paint);

		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		paint.setSubpixelText(true);
		paint.setTypeface(Typeface.DEFAULT_BOLD);

		String fltext = ArtemisActivity._lensFocalLengthText.getText()
				.toString();
		float fl = Float.parseFloat(fltext);
		int xRef = 0;
		if (fltext.length() < 3) {
			xRef = 160;
		} else if (fltext.length() >= 3 && fl < 100) {
			xRef = 170;
		} else if (fltext.length() >= 3 && fl >= 100) {
			xRef = 220;
		}

		SharedPreferences artemisPrefs = getContext().getApplicationContext()
				.getSharedPreferences(ArtemisPreferences.class.getSimpleName(),
						Context.MODE_PRIVATE);

		String description = artemisPrefs.getString(
				ArtemisPreferences.SAVE_PICTURE_SHOW_DESCRIPTION, "");
		boolean showCameraDetails = artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS, true);
		boolean showLensDetails = artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS, true);
		boolean showGpsDetails = artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_DETAILS, true);
		boolean showGpsLocationString = artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION, true);
		boolean showLensViewAngles = artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_VIEW_ANGLES, true);
		boolean showDateTime = artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_DATE_TIME, true);
		boolean showHeading = artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_HEADING, true);
		boolean showTiltRoll = artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_TILT_ROLL, true);

		paint.setTextSize(18);
		if (description.length() > 0) {
			canvas.drawText(description, 10, blankBmp.getHeight() - 50, paint);
		}
		paint.setTextSize(14);
		if (showCameraDetails) {
			canvas.drawText(ArtemisActivity._cameraDetailsText.getText()
					.toString(), 10, blankBmp.getHeight() - 30, paint);
		}
		paint.setTypeface(null);
		final int centerTextX = blankBmp.getWidth() / 2 - 30;
		if (showGpsDetails || showGpsLocationString) {
			String[] gpsDetailsAndLocation = ArtemisActivity
					.getGPSLocationDetailStrings(getContext());
			if (showGpsDetails && gpsDetailsAndLocation.length > 0) {
				canvas.drawText(gpsDetailsAndLocation[0], 10,
						blankBmp.getHeight() - 10, paint);
			}
			if (showGpsLocationString && gpsDetailsAndLocation.length > 1) {
				paint.setTextAlign(Align.CENTER);
				paint.setTextScaleX(0.87f);
				canvas.drawText(gpsDetailsAndLocation[1], centerTextX,
						blankBmp.getHeight() - 10, paint);
				paint.setTextAlign(Align.LEFT);
				paint.setTextScaleX(1f);
			}
		}

		paint.setTextAlign(Align.CENTER);
		if (showHeading && showTiltRoll) {
			canvas.drawText(ArtemisActivity.pictureSaveHeadingTiltString,
					centerTextX, blankBmp.getHeight() - 50, paint);
		} else if (showHeading) {

			if (ArtemisActivity.headingDisplaySelection == 1) {
				canvas.drawText(ArtemisActivity.pictureSaveHeadingTiltString,
						centerTextX, blankBmp.getHeight() - 50, paint);
			} else if (ArtemisActivity.headingDisplaySelection == 2) {
				canvas.drawText(ArtemisActivity.pictureSaveHeadingTiltString
						.substring(0,
								ArtemisActivity.pictureSaveHeadingTiltString
										.indexOf('|')), centerTextX, blankBmp
						.getHeight() - 50, paint);
			}
		} else if (showTiltRoll) {
			if (ArtemisActivity.headingDisplaySelection == 2) {
				canvas.drawText(ArtemisActivity.pictureSaveHeadingTiltString
						.substring(ArtemisActivity.pictureSaveHeadingTiltString
								.indexOf('|'),
								ArtemisActivity.pictureSaveHeadingTiltString
										.length() - 1), centerTextX, blankBmp
						.getHeight() - 50, paint);
			}
		}
		paint.setTextAlign(Align.LEFT);

		if (showLensViewAngles) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(1);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(18f);
			String hAngle = getResources().getString(R.string.hangle_text)
					+ " " + nf.format(_artemisMath.selectedLensAngleData[0])
					+ degreeSymbolFromStringsXML;
			String vAngle = getResources().getString(R.string.vangle_text)
					+ " " + nf.format(_artemisMath.selectedLensAngleData[1])
					+ degreeSymbolFromStringsXML;
			int xadjustHAngle = hAngle.length() < 14 ? 140 : 150;
			int xadjustVAngle = vAngle.length() < 14 ? 140 : 150;
			int xadjust = xadjustHAngle >= xadjustVAngle ? xadjustHAngle
					: xadjustVAngle;
			canvas.drawText(hAngle, blankBmp.getWidth() - xRef - xadjust,
					blankBmp.getHeight() - 32, paint);
			canvas.drawText(vAngle, blankBmp.getWidth() - xRef - xadjust,
					blankBmp.getHeight() - 10, paint);
		}

		if (showDateTime) {
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(10.5f);
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa | MM/dd/yyyy");
			canvas.drawText(sdf.format(new Date()), blankBmp.getWidth() - xRef
					- 140, blankBmp.getHeight() - 56, paint);
		}

		if (showLensDetails) {
			String lensMake = ArtemisActivity._lensMakeText.getText()
					.toString();
			paint.setTextSize(50);
			canvas.drawText(fltext + "mm", blankBmp.getWidth() - xRef,
					blankBmp.getHeight() - 10, paint);

			paint.setTextSize(12);
			if (lensMake.length() > 28) {
				paint.setTextSize(10);
				paint.setTextScaleX(0.9f);
			}
			paint.setTextAlign(Align.CENTER);
			canvas.drawText(lensMake, blankBmp.getWidth() - xRef + 72,
					blankBmp.getHeight() - 56, paint);

			paint.setStrokeWidth(4);
			canvas.drawLine(blankBmp.getWidth() - xRef - 13,
					blankBmp.getHeight() - 68, blankBmp.getWidth() - xRef - 13,
					blankBmp.getHeight() - 3, paint);
		}

		savePicture(blankBmp, showGpsDetails);
	}

	public void clearFullScreenTempFrame() {
		// tempFrameBitmap = new int[frameBufSize];
	}

	final static public void decodeYUV420SP2(int[] rgb, byte[] yuv420sp,
			int width, int height) {
		final int frameSize = width * height;

		if (rgb != null) {
			for (int j = 0, yp = 0; j < height; j++) {
				int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
				for (int i = 0; i < width; i++, yp++) {
					int y = (0xff & ((int) yuv420sp[yp])) - 16;
					if (y < 0)
						y = 0;
					if ((i & 1) == 0) {
						v = (0xff & yuv420sp[uvp++]) - 128;
						u = (0xff & yuv420sp[uvp++]) - 128;
					}

					int y1192 = 1192 * y;
					int r = (y1192 + 1634 * v);
					int g = (y1192 - 833 * v - 400 * u);
					int b = (y1192 + 2066 * u);

					if (r < 0)
						r = 0;
					else if (r > 262143)
						r = 262143;
					if (g < 0)
						g = 0;
					else if (g > 262143)
						g = 262143;
					if (b < 0)
						b = 0;
					else if (b > 262143)
						b = 262143;

					rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
							| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
				}
			}
		}
	}

	/**
	 * Set no digital zoom
	 */
	public void resetDigitalZoomLevel() {
		Camera.Parameters parms = mCamera.getParameters();
		parms.setZoom(0);
		mCamera.setParameters(parms);
	}

	/**
	 * Set white balance
	 */
	public void setWhiteBalance(String whiteBalance) {
		Camera.Parameters parms = mCamera.getParameters();
		parms.setWhiteBalance(whiteBalance);
		mCamera.setParameters(parms);
	}

	/**
	 * Set exposure compensation
	 */
	public void setExposureCompensation(Integer exposureIndex) {
		Camera.Parameters parms = mCamera.getParameters();
		parms.setExposureCompensation(exposureIndex);
		mCamera.setParameters(parms);
	}

	public void setFlash(boolean isEnabled) {
		Camera.Parameters parms = mCamera.getParameters();
		if (isEnabled) {
			parms.setFlashMode("torch");
		} else {
			parms.setFlashMode("off");
		}
		mCamera.setParameters(parms);
	}

	public void setPreviewSize(Size size) {
		Camera.Parameters parms = mCamera.getParameters();
		mCamera.stopPreview();
		ArtemisSettingsActivity.previewSize = size;
		parms.setPreviewSize(size.width, size.height);
		mCamera.setParameters(parms);
		restartPreview();
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (getChildCount() > 0 && changed) {
			Log.v(logTag, "** Laying out Camera Preview");

			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;

			if (ArtemisSettingsActivity.supportedPreviewSizes == null
					&& mCamera != null) {
				initPreviewSizes(mCamera.getParameters());
			}
			if (ArtemisSettingsActivity.previewSize == null
					&& ArtemisSettingsActivity.supportedPreviewSizes != null) {
				Log.v(logTag, "Preview size is null");

				SharedPreferences artemisPrefs = getContext()
						.getApplicationContext().getSharedPreferences(
								ArtemisPreferences.class.getSimpleName(),
								Context.MODE_PRIVATE);

				int selectedPreviewIndex = artemisPrefs.getInt(
						ArtemisPreferences.PREVIEW_SELECTION, -1);
				if (selectedPreviewIndex < 0) {
					Log.v(logTag, "automatically selecting best preview size");
					ArtemisSettingsActivity.previewSize = getOptimalPreviewSize(
							ArtemisSettingsActivity.supportedPreviewSizes,
							totalScreenWidth, totalScreenHeight);
				} else
					ArtemisSettingsActivity.previewSize = ArtemisSettingsActivity.supportedPreviewSizes
							.get(selectedPreviewIndex);
			}

			if (ArtemisSettingsActivity.previewSize != null) {
				previewWidth = ArtemisSettingsActivity.previewSize.width;
				previewHeight = ArtemisSettingsActivity.previewSize.height;
			}

			Log.v(logTag, "Preview width: " + previewWidth + " height:"
					+ previewHeight);

			// Hack to give tablets more width
			final float REQUESTED_WIDTH_RATIO = 1f;
			int requestedWidth = (int) (totalScreenWidth * REQUESTED_WIDTH_RATIO);
			if (previewWidth > requestedWidth) {
				scaledPreviewWidth = requestedWidth;
				scaledPreviewHeight = (int) (previewHeight * ((float) scaledPreviewWidth / previewWidth));
			} else if (previewWidth < requestedWidth) {
				scaledPreviewWidth = requestedWidth;
				scaledPreviewHeight = (int) (previewHeight * ((float) scaledPreviewWidth / previewWidth));
			} else {
				scaledPreviewWidth = previewWidth;
				scaledPreviewHeight = previewHeight;
			}

			int requestedHeight = (int) (totalScreenHeight * 0.91f);
			if (scaledPreviewHeight < requestedHeight) {
				Log.v(logTag, "Height is too large!");
				scaledPreviewHeight = requestedHeight;
				scaledPreviewWidth = (int) (previewWidth * ((float) scaledPreviewHeight / previewHeight));
				requestedWidthDiff = scaledPreviewWidth - requestedWidth;
			}
			Log.v(logTag, "Scaled preview width: " + scaledPreviewWidth
					+ " scaled height:" + scaledPreviewHeight);

			child.layout(0, 0, scaledPreviewWidth, scaledPreviewHeight);

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
