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
import java.util.concurrent.locks.ReentrantLock;

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
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

	private SurfaceHolder _cameraHolder;
	private Camera _camera;
	private static final String logTag = "CameraPreview";

	private Paint _paint = new Paint();
	private ArtemisMath _artemisMath = ArtemisMath.getInstance();
	private int frameBufSize;
	protected volatile byte[] frameBuffer;
	protected volatile int[] tempFrameBitmap; // holds decoded RGB 565 image
	protected volatile ByteBuffer currentFrame;

	protected float scaleFactor = 1f, zoomCenterX, zoomCenterY;
	protected float digital = 1f;
	// public float manualScale = 1f;
	private int totalScreenHeight;
	private int totalScreenWidth;
	protected static boolean widthAndHeightSwapped = false;

	protected int previewHeight, previewWidth, scaledPreviewWidth,
			scaledPreviewHeight, requestedWidthDiff = 0;;
	protected Bitmap bitmapToSave;
	protected float diffScaleFactor = 0f, diffZoomCenterX = 0f,
			diffZoomCenterY = 0f;
	protected boolean FILTER_BITMAP = true;
	protected ConversionWorker conversionThread;

	protected Bitmap arrowLeft;
	protected Bitmap arrowRight;
	protected Bitmap arrowTop;
	protected Bitmap arrowBottom;
	private boolean isTablet = false;
	private ArtemisApplication artemisApplication;

	private final String degreeSymbolFromStringsXML;

	public CameraPreview(Context context, AttributeSet attr) {
		super(context, attr);

		artemisApplication = ((ArtemisApplication) (((Activity) context)
				.getApplication()));

		SurfaceView surfaceView = new MySurfaceView(context);
		surfaceView.setWillNotDraw(false);
		addView(surfaceView);

		totalScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		totalScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
		if (totalScreenHeight > totalScreenWidth) {
			// swap!
			totalScreenWidth = getContext().getResources().getDisplayMetrics().heightPixels;
			totalScreenHeight = getContext().getResources().getDisplayMetrics().widthPixels;
		}

		int screensize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		if (screensize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screensize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			isTablet = true;
		}

		_cameraHolder = surfaceView.getHolder();
		_cameraHolder.addCallback(this);
		_cameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		_cameraHolder.setKeepScreenOn(true);

		degreeSymbolFromStringsXML = context.getString(R.string.degree_symbol);
	}

	
	public void openCamera() {
		if (_camera == null) {
			_camera = Camera.open();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		openCamera();
		if (_camera != null) {
			try {
				_camera.setPreviewDisplay(holder);
			} catch (IOException exception) {
				_camera.release();
				_camera = null;
			}
		}
	}

	public void enablePreviewCallback() {
		_camera.addCallbackBuffer(frameBuffer);
		_camera.setPreviewCallbackWithBuffer(fullscreenPreviewCallback);
	}

	public void disablePreviewCallback() {
		// _camera.setPreviewCallbackWithBuffer(null);
	}

	private PreviewCallback fullscreenPreviewCallback = new PreviewCallback() {

		// int fcount = 0;
		// Date start = new Date();

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (data != null && conversionThread != null
					&& _artemisMath.isFullscreen()) {
				conversionThread.nextFrame(data);

				// ++fcount;
				// if (fcount % 100 == 0) {
				// double ms = (new Date()).getTime() - start.getTime();
				// Log.i("PreviewCallback", "fps:" + fcount / (ms / 1000.0));
				// start = new Date();
				// fcount = 0;
				// }
			}
			if (conversionThread != null)
				camera.addCallbackBuffer(data);
		}
	};

	private ReentrantLock frameLock = new ReentrantLock();

	public void startConversionThread() {
		if (conversionThread == null)
			conversionThread = new ConversionWorker();
	}

	final class ConversionWorker extends Thread {
		int fcount = 0;
		Date start = new Date();
		private volatile boolean running = true;
		private boolean newFrame = false;

		public ConversionWorker() {
			// setDaemon(true);
			setName("ConversionWorker");

			setPriority(8);
			start();
		}

		@Override
		public synchronized void run() {
			while (running) {
				while (!newFrame && running) {
					// protect against spurious wakeups
					try {
						wait();// wait for next frame
					} catch (InterruptedException e) {
					}
				}
				if (running) {
					newFrame = false;

					frameLock.lock();
					try {
						decodeYUV420SP2(tempFrameBitmap, currentFrame.array(),
								previewWidth, previewHeight);
						postInvalidate();
					} finally {
						frameLock.unlock();
					}
					yield();
				}

				++fcount;
				if (fcount % 50 == 0) {
					double ms = (new Date()).getTime() - start.getTime();
					Log.i("ConversionWorker", "fps:" + fcount / (ms / 1000.0));
					start = new Date();
					fcount = 0;
				}
			}
		}

		final void nextFrame(byte[] frame) {
			if (this.getState() == Thread.State.WAITING && currentFrame != null) {
				// ready for a new frame
				currentFrame.position(0);
				currentFrame.put(frame);
				currentFrame.position(0);
				newFrame = true;

				// notify the thread to process the frame
				synchronized (this) {
					this.notify();
				}
			}
		}

		public synchronized void stopWorker() {
			running = false;
			this.notify();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(logTag, "CameraPreview Surface Destroyed.");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (_camera != null) {
			Camera.Parameters parameters = _camera.getParameters();

			SharedPreferences artemisPrefs = getContext()
					.getApplicationContext().getSharedPreferences(
							ArtemisPreferences.class.getSimpleName(),
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
			_camera.setParameters(parameters);

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
			parameters.setPreviewSize(
					ArtemisSettingsActivity.previewSize.width,
					ArtemisSettingsActivity.previewSize.height);
			previewWidth = ArtemisSettingsActivity.previewSize.width;
			previewHeight = ArtemisSettingsActivity.previewSize.height;
			try {
				ArtemisSettingsActivity.deviceHAngle = parameters
						.getHorizontalViewAngle();
			} catch (Exception e) {
				ArtemisSettingsActivity.deviceHAngle = 51.2f;// workaround for
																// bug in
																// certain
																// Android
				// devices that crash on getting angle
			}
			try {
				ArtemisSettingsActivity.deviceVAngle = parameters
						.getVerticalViewAngle();
			} catch (Exception e) {
				ArtemisSettingsActivity.deviceVAngle = 39.4f; // workaround for
																// bug in
																// certain
																// Android
				// devices that crash on getting angle
			}
			ArtemisSettingsActivity.effectiveHAngle = artemisPrefs.getFloat(
					ArtemisPreferences.CAMERA_LENS_H_ANGLE,
					ArtemisSettingsActivity.deviceHAngle);
			ArtemisSettingsActivity.effectiveVAngle = artemisPrefs.getFloat(
					ArtemisPreferences.CAMERA_LENS_V_ANGLE,
					ArtemisSettingsActivity.deviceVAngle);

			// handle auto focus setup
			if (parameters.getSupportedFocusModes().contains(
					Camera.Parameters.FOCUS_MODE_AUTO)) {
				parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
				ArtemisSettingsActivity.autoFocusBeforePictureTake = artemisPrefs
						.getBoolean(ArtemisPreferences.AUTO_FOCUS_ON_PICTURE,
								false);
			}

			Log.v(logTag,
					"Current preview format: " + parameters.getPreviewFormat());
			// Get the supported zoom ratios
			// supportedZoomRatios = parameters.getZoomRatios();
			// if (supportedZoomRatios != null) {
			// Log.v(logTag,
			// "Zoom ratios supported: " + supportedZoomRatios.size());
			// for (Integer zoomRatios : supportedZoomRatios) {
			// Log.v(logTag, zoomRatios + "");
			// }
			// }

			// init artemis math unit
			float pixelDensityScale = getContext().getResources()
					.getDisplayMetrics().density;

			if (!_artemisMath.isInitializedFirstTime()) {
				_artemisMath.setDeviceSpecificDetails(totalScreenWidth,
						totalScreenHeight, scaledPreviewWidth,
						scaledPreviewHeight, pixelDensityScale,
						ArtemisSettingsActivity.effectiveHAngle,
						ArtemisSettingsActivity.effectiveVAngle, isTablet);

				_artemisMath.calculateLargestLens();
				_artemisMath.calculateRectBoxesAndLabelsForLenses();
				_artemisMath.selectFirstMeaningFullLens();
				_artemisMath.resetTouchToCenter(); // now with green box
				_artemisMath.calculateRectBoxesAndLabelsForLenses();
				_artemisMath.setInitializedFirstTime(true);
				requestLayout();
				this.postInvalidate();
			}

			ArtemisActivity._lensFocalLengthText.setText(_artemisMath
					.get_selectedLensFocalLength());

			if (_artemisMath.isFullscreen()) {
				calculateZoom();
			}
			ArtemisActivity.reconfigureNextAndPreviousLensButtons();

			// initialize the buffer used for drawing the fullscreen bitmap and
			// enable preview callback
			PixelFormat p = new PixelFormat();
			PixelFormat.getPixelFormatInfo(parameters.getPreviewFormat(), p);
			frameBufSize = (previewWidth * previewHeight * p.bitsPerPixel) / 8;
			if (frameBuffer == null || frameBuffer.length != frameBufSize)
				frameBuffer = new byte[frameBufSize];
			tempFrameBitmap = new int[frameBufSize];
			currentFrame = ByteBuffer.allocate(frameBufSize);
			enablePreviewCallback();

			_camera.setParameters(parameters);

			// requestLayout();

			_camera.startPreview();
		}
	}

	private void initPreviewSizes(Camera.Parameters parameters) {
		ArtemisSettingsActivity.supportedPreviewSizes = parameters
				.getSupportedPreviewSizes();
		Iterator<Size> sizeIter = ArtemisSettingsActivity.supportedPreviewSizes
				.iterator();
		while (sizeIter.hasNext()) {
			Size size = sizeIter.next();
			double ratio = (double) size.width / size.height;
			if (ratio > 1.4 || size.width < 200) {
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

			_camera.autoFocus(new AutoFocusCallback() {

				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					artemisApplication.postDelayedOnWorkerThread(
							new Runnable() {

								@Override
								public void run() {
									_camera.setOneShotPreviewCallback(new TakePicturePreviewCallback());
								}
							}, 100);

				}

			});
		} else {
			artemisApplication.postDelayedOnWorkerThread(new Runnable() {
				@Override
				public void run() {
					_camera.setOneShotPreviewCallback(new TakePicturePreviewCallback());
				}
			}, 100);
		}
	}

	class TakePicturePreviewCallback implements PreviewCallback {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			decodeYUV420SP2(tempFrameBitmap, data, previewWidth, previewHeight);
			camera.addCallbackBuffer(data);

			RectF selectedRect = _artemisMath.getSelectedLensBox();
			RectF greenRect = _artemisMath.getCurrentGreenBox();

			bitmapToSave = Bitmap.createBitmap(tempFrameBitmap, previewWidth,
					previewHeight, Bitmap.Config.RGB_565);
			System.gc();

			if (selectedRect.left >= greenRect.left
					&& selectedRect.top >= greenRect.top
					&& selectedRect.bottom <= greenRect.bottom
					&& selectedRect.right <= greenRect.right) {

				bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
						scaledPreviewWidth, scaledPreviewHeight,
						ArtemisSettingsActivity.smoothImagesEnabled);

				if (!_artemisMath.isFullscreen()) {
					// preview mode zoomed in picture
					bitmapToSave = Bitmap.createBitmap(bitmapToSave,
							(int) (selectedRect.left),
							(int) (selectedRect.top),
							(int) (selectedRect.width()),
							(int) (selectedRect.height()));
				} else {
					// Full screen zoomed in picture
					int left = (int) (scaledPreviewWidth - selectedRect.width()) / 2;
					int top = (int) (scaledPreviewHeight - selectedRect
							.height()) / 2;

					bitmapToSave = Bitmap.createBitmap(bitmapToSave, left, top,
							(int) (selectedRect.width()),
							(int) (selectedRect.height()));

				}
				System.gc();
				float ratio = selectedRect.width() / selectedRect.height();
				bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
						(int) (610 * ratio), 610,
						ArtemisSettingsActivity.smoothImagesEnabled);
			}
			// We need to scale down
			else {
				Log.d(logTag, "Scale down on save");
				// bitmapToSave =
				// Bitmap.createScaledBitmap(bitmapToSave,
				// (int) (previewWidth * scaleFactor),
				// (int) (previewHeight * scaleFactor),
				// smoothImagesEnabled);
				float ratio = selectedRect.width() / selectedRect.height();
				int newwidth = (int) (610 * ratio);
				Bitmap canvasBitmap = Bitmap.createBitmap(newwidth, 610,
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
				int ypos = (610 - bitmapToSave.getHeight()) / 2;
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
						System.gc();
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

	class MySurfaceView extends SurfaceView {

		private final Rect rect = new Rect();

		@SuppressLint("NewApi")
		public MySurfaceView(Context context) {
			super(context);
			if (Build.VERSION.SDK_INT >= 11)
				setLayerType(LAYER_TYPE_SOFTWARE, null);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			_paint.setStyle(Paint.Style.STROKE);
			_paint.setStrokeWidth(1f);
			if (tempFrameBitmap != null && _artemisMath.isFullscreen()) {
				// clear off everything to black first to hide the background
				canvas.drawColor(Color.BLACK);
				canvas.translate(-zoomCenterX, -zoomCenterY);
				canvas.scale(scaleFactor, scaleFactor);
				_paint.setFilterBitmap(true);
				frameLock.lock();
				try {
					canvas.drawBitmap(tempFrameBitmap, 0, previewWidth, 0, 0,
							previewWidth, previewHeight, false, _paint);
				} finally {
					frameLock.unlock();
				}
				if (!arrowLeft.isRecycled()) {
					rect.set(-200, 0, 0, previewHeight);
					canvas.drawBitmap(arrowLeft, null, rect, _paint);
				}
				if (!arrowRight.isRecycled()) {
					rect.set(previewWidth, 0, previewWidth + 200, previewHeight);
					canvas.drawBitmap(arrowRight, null, rect, _paint);
				}
				if (!arrowTop.isRecycled()) {
					rect.set(0, -200, previewWidth, 0);
					canvas.drawBitmap(arrowTop, null, rect, _paint);
				}
				if (!arrowBottom.isRecycled()) {
					rect.set(0, previewHeight, previewWidth,
							previewHeight + 200);
					canvas.drawBitmap(arrowBottom, null, rect, _paint);
				}
				canvas.restore();
			} else if (_artemisMath.isFullscreen()) {
				canvas.drawColor(Color.BLACK);
			}

			// for (ArtemisRectF lensRect : _artemisMath.get_currentLensBoxes())
			// {
			// _paint.setStrokeWidth(1f);
			// // black line underneath
			// if (lensRect.isSolid()) {
			// _paint.setStyle(Style.FILL);
			// _paint.setColor(Color.BLACK);
			// canvas.drawRect(lensRect, _paint);
			// } else {
			// if (Color.RED == lensRect.getColor()) {
			// _paint.setStrokeWidth(2f);
			// }
			// _paint.setStyle(Style.STROKE);
			// _paint.setColor(Color.BLACK);
			// _paint.setPathEffect(null);
			// canvas.drawRect(lensRect, _paint);
			// _paint.setColor(lensRect.getColor());
			// _paint.setPathEffect(_boxLineEffect);
			// canvas.drawRect(lensRect, _paint);
			//
			// }
			// }
			// if (_artemisMath.getCurrentGreenBox() != null) {
			// _paint.setStrokeWidth(2f);
			// _paint.setStyle(Style.STROKE);
			// _paint.setColor(Color.BLACK);
			// _paint.setPathEffect(null);
			// canvas.drawRect(_artemisMath.getCurrentGreenBox(), _paint);
			// _paint.setColor(_artemisMath.getCurrentGreenBox().getColor());
			// _paint.setPathEffect(_boxLineEffect);
			// canvas.drawRect(_artemisMath.getCurrentGreenBox(), _paint);
			// }
		}
	}

	public void setArtemisMath(ArtemisMath artemisMath) {
		_artemisMath = artemisMath;
	}

	public void restartPreview() {
		try {
			_camera.setPreviewDisplay(_cameraHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		disablePreviewCallback();
		enablePreviewCallback();
		_camera.startPreview();
	}

	public void releaseCamera() {
		if (_camera != null) {
			_camera.stopPreview();
			_camera.release();
			_camera = null;
		}
	}

	public void calculateZoom() {
		// Calculate the zoom scale and translation
		ArtemisRectF selectedBox = _artemisMath.getSelectedLensBox();
		ArtemisRectF greenBox = _artemisMath.getCurrentGreenBox();

		double newScaleFactor = (greenBox.width() / selectedBox.width()
				* scaledPreviewWidth / previewWidth);

		double digital = 1d;
		// setOptimalDigitalZoomLevel((float) newScaleFactor);

		double manualScale = newScaleFactor / digital;
		scaleFactor = (float) manualScale;

		double newZoomCenterX = ((previewWidth * manualScale) - (scaledPreviewWidth - requestedWidthDiff)) / 2;
		double newZoomCenterY = ((previewHeight * manualScale) - (greenBox
				.height() + 50)) / 2;

		scaleFactor = (float) manualScale;
		Log.v(logTag, "ScaleFactor: " + newScaleFactor + " Digital: " + digital);
		zoomCenterX = (float) newZoomCenterX;
		zoomCenterY = (float) newZoomCenterY;
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

	public void setWhiteBalance(String whiteBalance) {
		Camera.Parameters parms = _camera.getParameters();
		parms.setWhiteBalance(whiteBalance);
		_camera.setParameters(parms);
	}

	public void setExposureCompensation(Integer exposureIndex) {
		Camera.Parameters parms = _camera.getParameters();
		parms.setExposureCompensation(exposureIndex);
		_camera.setParameters(parms);
	}

	public void setFlash(boolean isEnabled) {
		Camera.Parameters parms = _camera.getParameters();
		if (isEnabled) {
			parms.setFlashMode("torch");
		} else {
			parms.setFlashMode("off");
		}
		_camera.setParameters(parms);
	}

	public void setPreviewSize(Size size) {
		Camera.Parameters parms = _camera.getParameters();
		_camera.stopPreview();
		ArtemisSettingsActivity.previewSize = size;
		parms.setPreviewSize(size.width, size.height);
		_camera.setParameters(parms);
		restartPreview();
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (getChildCount() > 0) {
			Log.v(logTag, "** Laying out Camera Preview");

			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;

			if (ArtemisSettingsActivity.supportedPreviewSizes == null
					&& _camera != null) {
				initPreviewSizes(_camera.getParameters());
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
			final float REQUESTED_WIDTH_RATIO = isTablet ? 0.95f : 0.91f;
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

	public void setFrameBuffer(byte[] bytes) {
		frameBuffer = bytes;
	}

	public void setTempFrameBitmap(int[] array) {
		tempFrameBitmap = array;
	}

	public void setCurrentFrame(ByteBuffer bb) {
		currentFrame = bb;
	}

	public Bitmap getArrowLeft() {
		return arrowLeft;
	}

	public Bitmap getArrowRight() {
		return arrowRight;
	}

	public Bitmap getArrowTop() {
		return arrowTop;
	}

	public Bitmap getArrowBottom() {
		return arrowBottom;
	}

	public void setScaleFactor(float f) {
		scaleFactor = f;
	}

	public void setZoomCenterX(float f) {
		zoomCenterX = f;
	}

	public void setZoomCenterY(float f) {
		zoomCenterY = f;
	}

	public void setArrowRight(Bitmap decodeResource) {
		arrowRight = decodeResource;
	}

	public void setArrowLeft(Bitmap decodeResource) {
		arrowLeft = decodeResource;
	}

	public void setArrowTop(Bitmap decodeResource) {
		arrowTop = decodeResource;
	}

	public void setArrowBottom(Bitmap decodeResource) {
		arrowBottom = decodeResource;
	}

	public void stopConversionWorker() {
		if (conversionThread != null)
			this.conversionThread.stopWorker();
	}

	public void setConversionWorker(ConversionWorker thread) {
		this.conversionThread = thread;
	}
}
