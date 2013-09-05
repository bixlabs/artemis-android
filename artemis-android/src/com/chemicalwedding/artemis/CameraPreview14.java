package com.chemicalwedding.artemis;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CameraPreview14 extends ViewGroup {

    protected static List<Integer> supportedExposureLevels;
    protected static List<String> supportedWhiteBalance, supportedFlashModes;
    protected static List<Size> supportedPreviewSizes;
    protected static boolean lockBoxEnabled = false, quickshotEnabled = false,
            smoothImagesEnabled = false;
    protected static float deviceHAngle, effectiveHAngle, deviceVAngle,
            effectiveVAngle;

    protected static boolean isAutoFocusSupported = false;
    protected static boolean autoFocusBeforePictureTake = false;
    protected static Size previewSize;
    protected static float exposureStep;

    private Camera mCamera;
    private TextureView mTextureView;
    private static final String logTag = "CameraPreview";

    private ArtemisMath _artemisMath = ArtemisMath.getInstance();

    protected float scaleFactor = 1f;
    private int totalScreenHeight;
    private int totalScreenWidth;
    //	protected static boolean widthAndHeightSwapped = false;
    protected static int savedImageJPEGQuality;
    protected static int savedImageSizeIndex;
    protected static boolean blackAndWhitePreview;

    protected int previewHeight, previewWidth, requestedWidthDiff = 0;
    protected Bitmap bitmapToSave;

    private ArtemisApplication artemisApplication;
    // private final float pixelDensity;
    private final String degreeSymbolFromStringsXML;

    // private List<Size> supportedPictureSizes;

    public CameraPreview14(Context context, AttributeSet attr) {
        super(context, attr);

        artemisApplication = ((ArtemisApplication) (((Activity) context)
                .getApplication()));
        mTextureView = new MyTextureView(context);
        addView(mTextureView);

        Camera camera = openFrontFacingCameraGingerbread();
        if (camera != null) {
            initCameraDetails(camera.getParameters());
            camera.release();
        }

        degreeSymbolFromStringsXML = context.getString(R.string.degree_symbol);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(logTag, "CameraPreview Surface Destroyed.");
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    private void initCameraDetails(Camera.Parameters parameters) {
        supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        // supportedPictureSizes = parameters.getSupportedPictureSizes();

        Log.v(logTag, "Preview sizes supported: "
                + CameraPreview14.supportedPreviewSizes.size());
        for (Size size : CameraPreview14.supportedPreviewSizes) {
            Log.v(logTag, size.width + "x" + size.height);
        }
        CameraPreview14.previewSize = getOptimalPreviewSize(
                CameraPreview14.supportedPreviewSizes, totalScreenWidth,
                totalScreenHeight);

        Log.v(logTag, "Preview size selected: " + previewSize.width + "x"
                + previewSize.height);
        // parameters.setPreviewSize(previewSize.width, previewSize.height);
        previewWidth = previewSize.width;
        previewHeight = previewSize.height;

        isAutoFocusSupported = parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_AUTO);
        // pictureSize = getOptimalPictureSize(supportedPictureSizes,
        // totalScreenWidth, totalScreenHeight);
        // // parameters.setPictureSize(pictureSize.width, pictureSize.height);
        // Log.v(logTag,
        // "Picture sizes supported: " + supportedPictureSizes.size());
        // for (Size size : supportedPictureSizes) {
        // Log.v(logTag, size.width + "x" + size.height);
        // }
        // Log.v(logTag, "Picture size selected: " + pictureSize.width + "x"
        // + pictureSize.height);

    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        if (sizes == null)
            return null;
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;

        Size optimalSize = null;
        int selectedWidth = 0;
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (size.width > selectedWidth) {
                optimalSize = size;
                selectedWidth = size.width;
            }
        }
        return optimalSize;
    }

    // private Size getOptimalPictureSize(List<Size> sizes, int w, int h) {
    // if (sizes == null)
    // return null;
    //
    // Size optimalSize = null;
    // int selectedWidth = 0;
    // for (Size size : sizes) {
    // if (size.width > selectedWidth) {
    // optimalSize = size;
    // selectedWidth = size.width;
    // }
    // }
    // return optimalSize;
    // }

    public void takePicture() {
        if (isAutoFocusSupported && autoFocusBeforePictureTake) {
            mCamera.autoFocus(autoFocusTakePictureCallback);
        } else {
            capturePreviewFrameDelayed();
        }
    }

    final AutoFocusCallback autoFocusTakePictureCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            capturePreviewFrameDelayed();
        }
    };

    public void autofocusCamera(boolean takePictureImmediatelyAfter) {
        if (mCamera != null) {
            if (!isAutoFocusSupported && takePictureImmediatelyAfter) {
                capturePreviewFrameDelayed();
                return;
            }
            if (takePictureImmediatelyAfter)
                mCamera.autoFocus(autoFocusTakePictureCallback);
            else
                mCamera.autoFocus(null);
        }
    }

    private void capturePreviewFrameDelayed() {
        artemisApplication.postDelayedOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                mCamera.setOneShotPreviewCallback(takePicturePreviewCallback);
            }
        }, 100);
    }

    final PreviewCallback takePicturePreviewCallback = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            // Get the YuV image
            YuvImage yuv_image = new YuvImage(data, ImageFormat.NV21,
                    previewWidth, previewHeight, null);
            // Convert YuV to Jpeg
            Rect rect = new Rect(0, 0, previewWidth, previewHeight);
            ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
            yuv_image.compressToJpeg(rect, 100, output_stream);
            // Convert from Jpeg to Bitmap
            bitmapToSave = BitmapFactory.decodeByteArray(
                    output_stream.toByteArray(), 0, output_stream.size());
            System.gc();

            RectF selectedRect = _artemisMath.getSelectedLensBox();
            RectF greenRect = _artemisMath.getCurrentGreenBox();

            // bitmapToSave = Bitmap.createBitmap(tempFrameBitmap, previewWidth,
            // previewHeight, Bitmap.Config.RGB_565);

            final int imageHeight = determineImageHeight(previewHeight);

            if (scaleFactor >= 1) {

                bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
                        ArtemisMath.scaledPreviewWidth,
                        ArtemisMath.scaledPreviewHeight, smoothImagesEnabled);

                bitmapToSave = Bitmap.createBitmap(bitmapToSave,
                        (int) (selectedRect.left), (int) (selectedRect.top),
                        (int) (selectedRect.width()),
                        (int) (selectedRect.height()));

                System.gc();

                float ratio = selectedRect.width() / selectedRect.height();
                bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
                        (int) (imageHeight * ratio), imageHeight,
                        smoothImagesEnabled);
            }
            // We need to scale down
            else {
                Log.d(logTag, "Scale down on save");
                float ratio = greenRect.width() / greenRect.height();
                int newwidth = (int) (imageHeight * ratio);
                Bitmap canvasBitmap = Bitmap.createBitmap(newwidth,
                        imageHeight, Bitmap.Config.RGB_565);
                Canvas c = new Canvas(canvasBitmap);
                Paint p = new Paint();

                float width = newwidth * ArtemisMath.horizViewAngle
                        / _artemisMath.selectedLensAngleData[0]
                        * totalScreenWidth / greenRect.width();
                float previewRatio = (float) previewHeight / previewWidth;
                bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
                        (int) (width), (int) (width * previewRatio),
                        smoothImagesEnabled);

                int xpos = (newwidth - bitmapToSave.getWidth()) / 2;
                int ypos = (imageHeight - bitmapToSave.getHeight()) / 2;

                // draw background
                c.drawBitmap(ArtemisActivity.arrowBackgroundImage, null,
                        new Rect(0, 0, c.getWidth(), c.getHeight()), p);

                c.drawBitmap(bitmapToSave, xpos, ypos, p);

                bitmapToSave = canvasBitmap;
            }

            if (!quickshotEnabled) {
                ArtemisActivity.pictureSavePreview.setImageBitmap(bitmapToSave);
                ArtemisActivity.viewFlipper.setInAnimation(null);
                ArtemisActivity.viewFlipper.setDisplayedChild(3);
                ArtemisActivity.currentViewId = R.id.savePictureViewFlipper;
            } else {
                final Toast toast = Toast.makeText(getContext(), getContext()
                        .getString(R.string.image_saved_success),
                        Toast.LENGTH_SHORT);
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
                }.execute(new String[]{});
            }
        }

        private int determineImageHeight(int startImageHeight) {
            switch (CameraPreview14.savedImageSizeIndex) {
                case 1:
                    startImageHeight = (int) Math.round(startImageHeight * 1.25);
                    break;
                case 2:
                    startImageHeight = (int) Math.round(startImageHeight * 0.75);
                    break;
                default:
                    break;
            }

            if (startImageHeight < 420) {
                return 420;
            } else {
                return startImageHeight;
            }
        }

    };

    // private Size pictureSize;

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

    public void openCamera() {

        mCamera = openFrontFacingCameraGingerbread();

        if (mCamera != null) {
            isCameraReleased = false;

            Camera.Parameters parameters = mCamera.getParameters();
            SharedPreferences artemisPrefs = getContext()
                    .getApplicationContext().getSharedPreferences(
                            ArtemisPreferences.class.getSimpleName(),
                            Context.MODE_PRIVATE);

            // init camera preferences for artemis settings
            int min = parameters.getMinExposureCompensation();
            int max = parameters.getMaxExposureCompensation();
            CameraPreview14.exposureStep = parameters.getExposureCompensationStep();
            Log.v(logTag, "Camera Exposure min: " + min + " max: " + max);
            CameraPreview14.supportedExposureLevels = new ArrayList<Integer>();
            int index = 0, defaultExposure = 0;
            for (int i = min; i <= max; i++) {
                if (i == 0) {
                    defaultExposure = index;
                }
                CameraPreview14.supportedExposureLevels.add(i);
                index++;
            }
            if (blackAndWhitePreview) {
                Log.i(logTag, "BLACK AND WHITE ON");
                parameters.setColorEffect(Parameters.EFFECT_MONO);
            } else {
                parameters.setColorEffect(Parameters.EFFECT_NONE);
            }

            CameraPreview14.supportedWhiteBalance = parameters
                    .getSupportedWhiteBalance();
            CameraPreview14.supportedFlashModes = parameters
                    .getSupportedFlashModes();
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
            int exposureLevelIndex = artemisPrefs.getInt(ArtemisPreferences.SELECTED_EXPOSURE_LEVEL, defaultExposure);
            parameters.setExposureCompensation(exposureLevelIndex);
            String whiteBalance = artemisPrefs.getString(
                    ArtemisPreferences.SELECTED_WHITE_BALANCE, "");
            if (whiteBalance.length() > 0) {
                parameters.setWhiteBalance(whiteBalance);
            }
            parameters.setJpegQuality(100);
            mCamera.setParameters(parameters);

            parameters.setPreviewSize(previewSize.width, previewSize.height);
            // parameters.setPictureSize(pictureSize.width, pictureSize.height);

            try {
                deviceHAngle = parameters.getHorizontalViewAngle();
            } catch (Exception e) {
                deviceHAngle = 51.2f;// workaround for
                // bug
                // in certain
                // Android
                // devices that crash on getting
                // angle
            } finally {
                if (CameraPreview14.deviceHAngle < 1f) {
                    CameraPreview14.deviceHAngle = 51.2f;
                }
            }

            try {
                CameraPreview14.deviceVAngle = parameters
                        .getVerticalViewAngle();
            } catch (Exception e) {
                CameraPreview14.deviceVAngle = 39.4f; // workaround for
                // bug
                // in certain
                // Android
                // devices that crash on getting
                // angle
            } finally {
                if (CameraPreview14.deviceVAngle < 1f) {
                    CameraPreview14.deviceVAngle = 39.4f;
                }
            }

            if (!artemisPrefs.getBoolean(
                    getContext().getString(
                            R.string.preference_key_automaticlensangles), true)) {
                CameraPreview14.effectiveHAngle = artemisPrefs.getFloat(
                        ArtemisPreferences.CAMERA_LENS_H_ANGLE,
                        CameraPreview14.deviceHAngle);
                CameraPreview14.effectiveVAngle = artemisPrefs.getFloat(
                        ArtemisPreferences.CAMERA_LENS_V_ANGLE,
                        CameraPreview14.deviceVAngle);
            } else {
                CameraPreview14.effectiveHAngle = CameraPreview14.deviceHAngle;
                CameraPreview14.effectiveVAngle = CameraPreview14.deviceVAngle;
            }

            // handle auto focus setup
/*			if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				CameraPreview14.autoFocusBeforePictureTake = artemisPrefs
						.getBoolean(ArtemisPreferences.AUTO_FOCUS_ON_PICTURE,
								false);
			} else*/
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
                CameraPreview14.autoFocusBeforePictureTake = artemisPrefs
                        .getBoolean(ArtemisPreferences.AUTO_FOCUS_ON_PICTURE,
                                false);
            }

            Log.v(logTag,
                    "Current preview format: " + parameters.getPreviewFormat());
            // Get the supported zoom ratios
            // CameraPreview14.supportedZoomRatios =
            // parameters.getZoomRatios();
            // if (CameraPreview14.supportedZoomRatios != null) {
            // Log.v(logTag,
            // "Zoom ratios supported: " + supportedZoomRatios.size());
            // for (Integer zoomRatios : supportedZoomRatios) {
            // Log.v(logTag, zoomRatios + "");
            // }
            // }

            ArtemisMath.orangeBoxColor = getResources().getColor(
                    R.color.orangeArtemisText);

            // init artemis math unit
            float pixelDensityScale = getContext().getResources()
                    .getDisplayMetrics().density;

            _artemisMath.setDeviceSpecificDetails(totalScreenWidth,
                    totalScreenHeight, pixelDensityScale, effectiveHAngle,
                    effectiveVAngle);

            if (!_artemisMath.isInitializedFirstTime()) {
                _artemisMath.calculateLargestLens();
                _artemisMath.calculateRectBoxesAndLabelsForLenses();
                _artemisMath.selectFirstMeaningFullLens();
                _artemisMath.resetTouchToCenter(); // now with green box
                _artemisMath.calculateRectBoxesAndLabelsForLenses();
                _artemisMath.setInitializedFirstTime(true);
            }

            // Set the focal length lens textview
            ArtemisActivity._lensFocalLengthText.setText(_artemisMath
                    .get_selectedLensFocalLength());

            // initialize the buffer used for drawing the fullscreen bitmap and
            // enable preview callback
//			PixelFormat p = new PixelFormat();
//			PixelFormat.getPixelFormatInfo(parameters.getPreviewFormat(), p);
//			frameBufSize = (previewWidth * previewHeight * p.bitsPerPixel) / 8;
//			if (frameBuffer == null || frameBuffer.length != frameBufSize)
//				frameBuffer = new byte[frameBufSize];
//			tempFrameBitmap = new int[frameBufSize];

            mCamera.setParameters(parameters);

            try {
                mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
                mCamera.startPreview();
            } catch (IOException ioe) {
                // Something bad happened
            }

            // Set the background
            if (ArtemisActivity.arrowBackgroundImage == null) {
                Options o = new Options();
                o.inSampleSize = 2;
                ArtemisActivity.arrowBackgroundImage = BitmapFactory
                        .decodeResource(getResources(), R.drawable.arrows, o);
            }
        }
    }

    protected boolean isCameraReleased = false;

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            isCameraReleased = true;
            mCamera = null;
            mTextureView.invalidate();
        }
    }

    public void setArtemisMath(ArtemisMath artemisMath) {
        _artemisMath = artemisMath;
    }

    public void restartPreview() {
        if (mCamera != null)
            mCamera.startPreview();
        else {
            openCamera();
        }
    }

    // private float buf[] = new float[9];
    // float prevScaleFactor = 1;

    public void calculateZoom(boolean shouldCalcScaleFactor) {
        // Calculate the zoom scale and translation
        Matrix endTransform = new Matrix();
        // prevScaleFactor = this.scaleFactor;
        if (_artemisMath.isFullscreen() || !shouldCalcScaleFactor) {
            if (shouldCalcScaleFactor) {
                this.scaleFactor = _artemisMath.calculateFullscreenZoomRatio();
            }
            if (scaleFactor > 1f) {
                endTransform.setRectToRect(
                        (RectF) _artemisMath.getSelectedLensBox(),
                        (RectF) _artemisMath.getCurrentGreenBox(),
                        Matrix.ScaleToFit.CENTER);
            } else {
                endTransform
                        .postTranslate(
                                _artemisMath.getCurrentGreenBox().left
                                        - (ArtemisMath.scaledPreviewWidth - _artemisMath
                                        .getCurrentGreenBox().width())
                                        / 2f,
                                (_artemisMath.getCurrentGreenBox().top - (ArtemisMath.scaledPreviewHeight - _artemisMath
                                        .getCurrentGreenBox().height()) / 2f) - 5);
                endTransform.postScale(scaleFactor, scaleFactor, _artemisMath
                        .getCurrentGreenBox().centerX(), _artemisMath
                        .getCurrentGreenBox().centerY());
            }

        } else {
            this.scaleFactor = 1f;
        }

        Log.v(logTag, "Zoom ScaleFactor: " + scaleFactor);
        Log.v(logTag, "Selected Lens: " + _artemisMath.getSelectedLensBox());

        mTextureView.setTransform(endTransform);
    }

    public void savePicture(Bitmap bitmap, boolean writeEXIFlocationInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss.S",
                Locale.getDefault());
        String title = sdf.format(Calendar.getInstance().getTime());
        String filePath = ArtemisActivity.savePictureFolder + "/" + title
                + ".jpg";
        Log.v(logTag, "Saving file: " + filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmap.compress(CompressFormat.JPEG, savedImageJPEGQuality, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(logTag, "Picture file could not be created");
            this.post(new Runnable() {
                @Override
                public void run() {
                    final Toast toast = Toast.makeText(getContext(),
                            getContext()
                                    .getString(R.string.image_save_no_media),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        } catch (IOException e) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    final Toast toast = Toast.makeText(getContext(),
                            getContext().getString(R.string.image_save_error),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            });
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
                ex.setAttribute(ExifInterface.TAG_FOCAL_LENGTH,
                        _artemisMath.get_selectedLensFocalLength());
                ex.setAttribute(ExifInterface.TAG_MODEL,
                        ArtemisActivity._lensMakeText.getText().toString());
                ex.setAttribute(ExifInterface.TAG_MAKE,
                        ArtemisActivity._cameraDetailsText.getText().toString());

//				SharedPreferences artemisPrefs = getContext()
//						.getApplicationContext().getSharedPreferences(
//								ArtemisPreferences.class.getSimpleName(),
//								Context.MODE_PRIVATE);
//				String desc = artemisPrefs.getString(
//						ArtemisPreferences.SAVE_PICTURE_SHOW_DESCRIPTION, null);
//				String notes = artemisPrefs.getString(
//						ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, null);
//				if (desc != null && !desc.isEmpty()) {
//					ex.setAttribute("DocumentName", desc);
//				}
//				if (notes != null && !notes.isEmpty()) {
//					ex.setAttribute("ImageDescription", notes);
//				}

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

        SharedPreferences artemisPrefs = getContext().getApplicationContext()
                .getSharedPreferences(ArtemisPreferences.class.getSimpleName(),
                        Context.MODE_PRIVATE);

        boolean showGpsDetails = artemisPrefs.getBoolean(
                ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_DETAILS, true);

        Bitmap blankBmp = bitmapToSave;

        if (!artemisPrefs.getBoolean(ArtemisPreferences.SAVE_RAW_IMAGE, false)) {

            int sideborder = 10, footerHeight = 75;
            // add a larger border in 4:3
            if ((float) bitmapToSave.getWidth() / bitmapToSave.getHeight() < 1.4) {
                sideborder = 259; // to match 16:9 for 4:3
            }
            blankBmp = Bitmap.createBitmap(
                    bitmapToSave.getWidth() + sideborder,
                    bitmapToSave.getHeight() + footerHeight,
                    Bitmap.Config.RGB_565);
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

            String description = artemisPrefs.getString(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_DESCRIPTION, "");
            String notes = artemisPrefs.getString(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, "");
            boolean showCameraDetails = artemisPrefs.getBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS, true);
            boolean showLensDetails = artemisPrefs.getBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS, true);
            boolean showGpsLocationString = artemisPrefs.getBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION, true);
            boolean showLensViewAngles = artemisPrefs
                    .getBoolean(
                            ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_VIEW_ANGLES,
                            true);
            boolean showDateTime = artemisPrefs.getBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_DATE_TIME, true);
            boolean showHeading = artemisPrefs.getBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_HEADING, true);
            boolean showTiltRoll = artemisPrefs.getBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_TILT_ROLL, true);

            paint.setTextSize(18);
            if (description.length() > 0) {
                canvas.drawText(description, 10, blankBmp.getHeight() - 50,
                        paint);
            }
            paint.setTextSize(14);
            if (showCameraDetails) {
                canvas.drawText(ArtemisActivity._cameraDetailsText.getText()
                        .toString(), 10, blankBmp.getHeight() - 30, paint);
            }
            paint.setTypeface(null);

            if (notes.length() > 0) {
                canvas.drawText(notes, 10, blankBmp.getHeight() - 10, paint);
            }

            final int centerTextX = blankBmp.getWidth() / 2 - 30;
            if (showGpsDetails || showGpsLocationString) {
                String[] gpsDetailsAndLocation = ArtemisActivity
                        .getGPSLocationDetailStrings(getContext());
                // if (showGpsDetails && gpsDetailsAndLocation.length > 0) {
                // canvas.drawText(gpsDetailsAndLocation[0], 10,
                // blankBmp.getHeight() - 10, paint);
                // }
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
                    canvas.drawText(
                            ArtemisActivity.pictureSaveHeadingTiltString,
                            centerTextX, blankBmp.getHeight() - 50, paint);
                } else if (ArtemisActivity.headingDisplaySelection == 2) {
                    canvas.drawText(
                            ArtemisActivity.pictureSaveHeadingTiltString
                                    .substring(
                                            0,
                                            ArtemisActivity.pictureSaveHeadingTiltString
                                                    .indexOf('|')),
                            centerTextX, blankBmp.getHeight() - 50, paint);
                }
            } else if (showTiltRoll) {
                if (ArtemisActivity.headingDisplaySelection == 2) {
                    canvas.drawText(
                            ArtemisActivity.pictureSaveHeadingTiltString
                                    .substring(
                                            ArtemisActivity.pictureSaveHeadingTiltString
                                                    .indexOf('|'),
                                            ArtemisActivity.pictureSaveHeadingTiltString
                                                    .length() - 1),
                            centerTextX, blankBmp.getHeight() - 50, paint);
                }
            }
            paint.setTextAlign(Align.LEFT);

            if (showLensViewAngles) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(1);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextSize(18f);
                String hAngle = getResources().getString(R.string.hangle_text)
                        + " "
                        + nf.format(_artemisMath.selectedLensAngleData[0])
                        + degreeSymbolFromStringsXML;
                String vAngle = getResources().getString(R.string.vangle_text)
                        + " "
                        + nf.format(_artemisMath.selectedLensAngleData[1])
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
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "hh:mm aa | MM/dd/yyyy", Locale.getDefault());
                canvas.drawText(sdf.format(new Date()), blankBmp.getWidth()
                        - xRef - 140, blankBmp.getHeight() - 56, paint);
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
                        blankBmp.getHeight() - 68, blankBmp.getWidth() - xRef
                        - 13, blankBmp.getHeight() - 3, paint);
            }
        }

        savePicture(blankBmp, showGpsDetails);
    }

    public void clearFullScreenTempFrame() {
        // tempFrameBitmap = new int[frameBufSize];
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
        previewSize = size;
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

            // final int width = r - l;
            // final int height = b - t;

            final int previewWidth = CameraPreview14.previewSize.width;
            final int previewHeight = CameraPreview14.previewSize.height;

            Log.v(logTag, "Preview width: " + previewWidth + " height:"
                    + previewHeight);

            final float REQUESTED_WIDTH_RATIO = 1f;
            int requestedWidth = (int) (totalScreenWidth * REQUESTED_WIDTH_RATIO);
            ArtemisMath.scaledPreviewWidth = requestedWidth;

            // final float pictureRatio = (float) pictureSize.width
            // / pictureSize.height;
            final float previewRatio = (float) previewWidth / previewHeight;

            ArtemisMath.scaledPreviewHeight = Math.round(requestedWidth
                    / previewRatio);

            Log.v(logTag, "Scaled preview width: "
                    + ArtemisMath.scaledPreviewWidth + " scaled height:"
                    + ArtemisMath.scaledPreviewHeight);

            child.layout(0, 5, ArtemisMath.scaledPreviewWidth,
                    ArtemisMath.scaledPreviewHeight + 5);

            Log.v(logTag, "** Finished layout of Camera Preview");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        totalScreenHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.v(logTag, "CameraPreview onMeasure screenWidth: "
                + totalScreenWidth + " screenHeight: " + totalScreenHeight);
        setMeasuredDimension(totalScreenWidth, totalScreenHeight);
    }
}
