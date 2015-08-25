package com.chemicalwedding.artemis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.SizeF;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraPreview21 extends Fragment {

    protected static List<Integer> supportedExposureLevels;
    protected static List<String> supportedWhiteBalance, supportedFlashModes,
            supportedFocusModes, supportedSceneModes;
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
    static private int totalScreenHeight;
    static private int totalScreenWidth;
    // protected static boolean widthAndHeightSwapped = false;
    protected static int savedImageJPEGQuality;
    protected static int savedImageSizeIndex;
    protected static boolean blackAndWhitePreview;

    static protected int previewHeight, previewWidth, requestedWidthDiff = 0;
    protected Bitmap bitmapToSave;
    private float[] mDeviceFocalLengths;
    private Rect mDeviceActiveSensorSize;
    private SizeF mDevicePhysicalSensorSize;
    private float mSensorRatio;
    private Float mMaxDigitalZoom;
    private Integer mCroppingType;
    private Rect mOriginalCropRegion;

//    public static void initCameraDetails() {
//
//		if (CameraPreview21.previewSize == null) {
////			Camera camera = CameraPreview21.openFrontFacingCameraGingerbread();
////			Parameters parameters = camera.getParameters();
//
////			supportedPreviewSizes = parameters.getSupportedPreviewSizes();
//			// supportedPictureSizes = parameters.getSupportedPictureSizes();
//
//			Log.v(logTag, "Preview sizes supported: "
//					+ CameraPreview21.supportedPreviewSizes.size());
//			for (Size size : CameraPreview21.supportedPreviewSizes) {
//				Log.v(logTag, size.width + "x" + size.height);
//			}
////			CameraPreview21.previewSize = getOptimalPreviewSize(
////					CameraPreview21.supportedPreviewSizes, totalScreenWidth,
////					totalScreenHeight);
//
//			Log.v(logTag, "Preview size selected: " + previewSize.width + "x"
//					+ previewSize.height);
//			// parameters.setPreviewSize(previewSize.width, previewSize.height);
//			previewWidth = previewSize.width;
//			previewHeight = previewSize.height;
//
//			isAutoFocusSupported = parameters.getSupportedFocusModes()
//					.contains(Parameters.FOCUS_MODE_AUTO);
//			// pictureSize = getOptimalPictureSize(supportedPictureSizes,
//			// totalScreenWidth, totalScreenHeight);
//			// // parameters.setPictureSize(pictureSize.width,
//			// pictureSize.height);
//			// Log.v(logTag,
//			// "Picture sizes supported: " + supportedPictureSizes.size());
//			// for (Size size : supportedPictureSizes) {
//			// Log.v(logTag, size.width + "x" + size.height);
//			// }
//			// Log.v(logTag, "Picture size selected: " + pictureSize.width + "x"
//			// + pictureSize.height);
//
//			camera.release();
//		}
//	}

    private static int determineImageHeight(int startImageHeight) {
        switch (CameraPreview21.savedImageSizeIndex) {
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

    public void setTextureView(TextureView textureView) {
        mTextureView = textureView;
    }

    public void startArtemisPreview(boolean resetLensAndTouch) {

//		mCamera = camera;

//		if (mCamera != null) {

//			Parameters parameters = mCamera.getParameters();
        SharedPreferences artemisPrefs = getActivity()
                .getApplicationContext().getSharedPreferences(
                        ArtemisPreferences.class.getSimpleName(),
                        Context.MODE_PRIVATE);

        // init camera preferences for artemis settings
//			int min = parameters.getMinExposureCompensation();
//			int max = parameters.getMaxExposureCompensation();
//			CameraPreview21.exposureStep = parameters
//					.getExposureCompensationStep();
//			Log.v(logTag, "Camera Exposure min: " + min + " max: " + max);
//			CameraPreview21.supportedExposureLevels = new ArrayList<Integer>();
//			int defaultExposure = 0;
//			for (int i = min; i <= max; i++) {
//				CameraPreview21.supportedExposureLevels.add(i);
//			}
//			if (blackAndWhitePreview) {
//				Log.i(logTag, "BLACK AND WHITE ON");
//				parameters.setColorEffect(Parameters.EFFECT_MONO);
//			} else {
//				parameters.setColorEffect(Parameters.EFFECT_NONE);
//			}

//			CameraPreview21.supportedWhiteBalance = parameters
//					.getSupportedWhiteBalance();
//			CameraPreview21.supportedFlashModes = parameters
//					.getSupportedFlashModes();

//			if (CameraPreview21.supportedFlashModes != null) {
//				Log.v(logTag, "Supported Flash modes: ");
//				for (String flashMode : CameraPreview21.supportedFlashModes) {
//					Log.v(logTag, flashMode);
//				}
//
//				boolean flashEnabled = artemisPrefs.getBoolean(
//						ArtemisPreferences.FLASH_ENABLED, false);
//				if (flashEnabled) {
//					parameters.setFlashMode("torch");
//				} else
//					parameters.setFlashMode("off");
//
//			}
//
//			int exposureLevelIndex = artemisPrefs
//					.getInt(ArtemisPreferences.SELECTED_EXPOSURE_LEVEL,
//							defaultExposure);
//			Log.v(logTag, String.format(
//					"Selected exposure compensation index: %d",
//					exposureLevelIndex));
//			parameters.setExposureCompensation(exposureLevelIndex);
//
//			String whiteBalance = artemisPrefs.getString(
//					ArtemisPreferences.SELECTED_WHITE_BALANCE, "");
//			if (whiteBalance.length() > 0) {
//				parameters.setWhiteBalance(whiteBalance);
//			}
//
//			CameraPreview21.supportedFocusModes = parameters
//					.getSupportedFocusModes();
//			if (CameraPreview21.supportedFocusModes != null) {
//				String focusMode = artemisPrefs.getString(
//						ArtemisPreferences.SELECTED_FOCUS_MODE, "");
//				if (focusMode.length() == 0
//						&& CameraPreview21.supportedFocusModes
//								.contains(Parameters.FOCUS_MODE_AUTO)) {
//					parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
//				} else {
//					parameters.setFocusMode(focusMode);
//				}
//			}
//			CameraPreview21.autoFocusBeforePictureTake = artemisPrefs
//					.getBoolean(ArtemisPreferences.AUTO_FOCUS_ON_PICTURE, false);
//
//			CameraPreview21.supportedSceneModes = parameters
//					.getSupportedSceneModes();
//			if (CameraPreview21.supportedSceneModes != null) {
//				parameters.setSceneMode(artemisPrefs.getString(
//						ArtemisPreferences.SELECTED_SCENE_MODE, "auto"));
//			}
//
////			parameters.setPreviewSize(previewSize.width, previewSize.height);
//			// parameters.setPictureSize(pictureSize.width, pictureSize.height);

        if (!artemisPrefs.getBoolean(
                getActivity().getString(
                        R.string.preference_key_automaticlensangles), true)) {
            CameraPreview21.effectiveHAngle = artemisPrefs.getFloat(
                    ArtemisPreferences.CAMERA_LENS_H_ANGLE,
                    CameraPreview21.deviceHAngle);
            CameraPreview21.effectiveVAngle = artemisPrefs.getFloat(
                    ArtemisPreferences.CAMERA_LENS_V_ANGLE,
                    CameraPreview21.deviceVAngle);
        } else {
//            CameraPreview21.effectiveHAngle = 48.0f;
//            CameraPreview21.effectiveVAngle = 32.0f;
        }

        // handle auto focus setup

//			Log.v(logTag,
//					"Current preview format: " + parameters.getPreviewFormat());
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
        float pixelDensityScale = getActivity().getResources()
                .getDisplayMetrics().density;

        // setup the previous camera and lens selection
        ((ArtemisActivity) getActivity()).initCameraAndLensSelection();

        ((ArtemisActivity) getActivity()).initPreferences();

        if (!_artemisMath.isInitializedFirstTime()) {
            _artemisMath.setDeviceSpecificDetails(totalScreenWidth,
                    totalScreenHeight, pixelDensityScale, effectiveHAngle,
                    effectiveVAngle);
            _artemisMath.resetTouchToCenter(); // now with green box
            _artemisMath.calculateLargestLens();
            _artemisMath.selectFirstMeaningFullLens();
            _artemisMath.calculateRectBoxesAndLabelsForLenses();
            _artemisMath.setInitializedFirstTime(true);
        } else if (resetLensAndTouch) {
            _artemisMath.calculateLargestLens();
            _artemisMath.calculateRectBoxesAndLabelsForLenses();
            _artemisMath.selectFirstMeaningFullLens();
            _artemisMath.resetTouchToCenter(); // now with green box
            _artemisMath.calculateRectBoxesAndLabelsForLenses();
        }
        this.calculateZoom(true);
        ((ArtemisActivity) getActivity()).reconfigureNextAndPreviousLensButtons();
        ((ArtemisActivity) getActivity()).mCameraOverlay.invalidate();
        ((ArtemisActivity) getActivity()).mCameraAngleDetailView.invalidate();

        // Set the focal length lens textview
        ArtemisActivity._lensFocalLengthText.setText(_artemisMath
                .get_selectedLensFocalLength());


        // Set the background
        if (ArtemisActivity.arrowBackgroundImage == null) {
            Options o = new Options();
            o.inSampleSize = 2;
            ArtemisActivity.arrowBackgroundImage = BitmapFactory
                    .decodeResource(getResources(), R.drawable.arrows, o);
        }
//		}
    }


    public void restartPreview() {
//		if (mCamera != null)
//			mCamera.startPreview();
    }

    // private float buf[] = new float[9];
    // float prevScaleFactor = 1;
    Matrix origin, inverse;

    public void calculateZoom(boolean shouldCalcScaleFactor) {
        // Calculate the zoom scale and translation

//        Matrix endTransform = mTextureView.getTransform(null);
        Matrix endTransform = new Matrix();

        // prevScaleFactor = this.scaleFactor;
//        if (inverse != null) { // && !_artemisMath.isFullscreen()) {
//            endTransform.preConcat(inverse);
//        }
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        if (_artemisMath.isFullscreen() || !shouldCalcScaleFactor) {
            if (shouldCalcScaleFactor) {
                this.scaleFactor = _artemisMath.calculateFullscreenZoomRatio();
            }
            if (scaleFactor > 1f) {

                RectF selectedMapped = new RectF(_artemisMath.getSelectedLensBox());
//                RectF greenMapped = new RectF(_artemisMath.getCurrentGreenBox());
                Rect greenMapped = new Rect();
//                greenMapped.set(0, 0, totalScreenWidth, totalScreenHeight);
//                mTextureView.getDrawingRect(greenMapped);

//                origin.mapRect(selectedMapped);
//                origin.mapRect(greenMapped);

//                endTransform.preConcat(origin);
//                endTransform.setRectToRect(
//                        selectedMapped,
//                        new RectF(_artemisMath.getCurrentGreenBox()),
//                        Matrix.ScaleToFit.CENTER);
                endTransform.preConcat(origin);

                endTransform.postScale(scaleFactor, scaleFactor, _artemisMath
                        .getCurrentGreenBox().centerX(), _artemisMath
                        .getCurrentGreenBox().centerY());
//
//                lockFocus();
//                mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, );
//                Rect scaledRegion = new Rect(this.mDeviceActiveSensorSize);
//                scaledRegion.inset(2000, 0);
//
//                mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, scaledRegion);
//
//                unlockFocus();

            } else {
                endTransform.preConcat(origin);
//                endTransform
//                        .postTranslate(
//                                _artemisMath.getCurrentGreenBox().left
//                                        - (ArtemisMath.scaledPreviewWidth - _artemisMath
//                                        .getCurrentGreenBox().width())
//                                        / 2f,
//                                (_artemisMath.getCurrentGreenBox().top - (ArtemisMath.scaledPreviewHeight - _artemisMath
//                                        .getCurrentGreenBox().height()) / 2f) - 5);
                endTransform.postScale(scaleFactor, scaleFactor, _artemisMath
                        .getCurrentGreenBox().centerX(), _artemisMath
                        .getCurrentGreenBox().centerY());
//                endTransform
//                        .postTranslate(
//

            }

            inverse = new Matrix();
            endTransform.invert(inverse);

        } else {
            this.scaleFactor = 1f;

            endTransform = new Matrix(origin);
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
            View view = getView();
            if (view != null) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        final Toast toast = Toast.makeText(getActivity(),
                                getActivity()
                                        .getString(R.string.image_save_no_media),
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        } catch (IOException e) {
            View view = getView();
            if (view != null) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        final Toast toast = Toast.makeText(getActivity(),
                                getActivity().getString(R.string.image_save_error),
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
            e.printStackTrace();
        }
        bitmap.recycle();

        if (writeEXIFlocationInfo) {
            try {
                ExifInterface ex = new ExifInterface(filePath);

                if (ArtemisActivity.pictureSaveLocation != null) {
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
                }

                ex.setAttribute(ExifInterface.TAG_FOCAL_LENGTH,
                        _artemisMath.get_selectedLensFocalLength());
                ex.setAttribute(ExifInterface.TAG_MODEL,
                        ArtemisActivity._lensMakeText.getText().toString());
                ex.setAttribute(ExifInterface.TAG_MAKE,
                        ArtemisActivity._cameraDetailsText.getText().toString());
                ex.saveAttributes();

            } catch (IOException ioe) {
                Log.e(logTag, "Could not open image for writing EXIF data");
            }
        }

        MediaScannerConnection.scanFile(getActivity(),
                new String[]{filePath}, new String[]{"image/jpeg"}, null);
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

        SharedPreferences artemisPrefs = getActivity().getApplicationContext()
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
                        .getGPSLocationDetailStrings(getActivity());
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
                    if (ArtemisActivity.pictureSaveHeadingTiltString
                            .contains("|")) {
                        canvas.drawText(
                                ArtemisActivity.pictureSaveHeadingTiltString
                                        .substring(
                                                0,
                                                ArtemisActivity.pictureSaveHeadingTiltString
                                                        .indexOf('|')),
                                centerTextX, blankBmp.getHeight() - 50, paint);
                    }
                }
            } else if (showTiltRoll) {
                if (ArtemisActivity.headingDisplaySelection == 2
                        && ArtemisActivity.pictureSaveHeadingTiltString
                        .contains("|")) {
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
                        + getString(R.string.degree_symbol);
                String vAngle = getResources().getString(R.string.vangle_text)
                        + " "
                        + nf.format(_artemisMath.selectedLensAngleData[1])
                        + getString(R.string.degree_symbol);
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


//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		if (getChildCount() > 0 && changed
//				&& CameraPreview21.previewSize != null) {
//			Log.v(logTag, "** Laying out Camera Preview");
//
//			final View child = getChildAt(0);
//
//			// final int width = r - l;
//			// final int height = b - t;
//
//			final int previewWidth = CameraPreview21.previewSize.width;
//			final int previewHeight = CameraPreview21.previewSize.height;
//
//			Log.v(logTag, "Preview width: " + previewWidth + " height:"
//					+ previewHeight);
//
//			final float REQUESTED_WIDTH_RATIO = 1f;
//			int requestedWidth = (int) (totalScreenWidth * REQUESTED_WIDTH_RATIO);
//			ArtemisMath.scaledPreviewWidth = requestedWidth;
//
//			// final float pictureRatio = (float) pictureSize.width
//			// / pictureSize.height;
//			final float previewRatio = (float) previewWidth / previewHeight;
//
//			ArtemisMath.scaledPreviewHeight = Math.round(requestedWidth
//					/ previewRatio);
//
//			Log.v(logTag, "Scaled preview width: "
//					+ ArtemisMath.scaledPreviewWidth + " scaled height:"
//					+ ArtemisMath.scaledPreviewHeight);
//
//			child.layout(0, 5, ArtemisMath.scaledPreviewWidth,
//					ArtemisMath.scaledPreviewHeight + 5);
//
//			Log.v(logTag, "** Finished layout of Camera Preview");
//		}
//	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		totalScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
//		totalScreenHeight = MeasureSpec.getSize(heightMeasureSpec);
//		Log.v(logTag, "CameraPreview onMeasure screenWidth: "
//				+ totalScreenWidth + " screenHeight: " + totalScreenHeight);
//		// setMeasuredDimension(totalScreenWidth, totalScreenHeight);
//	}

    /**
     *
     * NEW API
     *
     *
     */


    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link android.util.Log}.
     */
    private static final String TAG = "CameraPreview21";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;
    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;
    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * {@link android.view.TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link android.view.TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    /**
     * ID of the current {@link android.hardware.camera2.CameraDevice}.
     */
    private String mCameraId;

//    private TextureView mTextureView;

    /**
     * A {@link android.hardware.camera2.CameraCaptureSession } for camera preview.
     */

    private CameraCaptureSession mCaptureSession;
    /**
     * A reference to the opened {@link android.hardware.camera2.CameraDevice}.
     */

    private CameraDevice mCameraDevice;
    /**
     * The {@link android.util.Size} of camera preview.
     */

    private android.util.Size mPreviewSize;

    /**
     * {@link android.hardware.camera2.CameraDevice.StateCallback} is called when {@link android.hardware.camera2.CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link android.os.Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link android.media.ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File mFile;

    /**
     * This a callback object for the {@link android.media.ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
        }

    };

    /**
     * {@link android.hardware.camera2.CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link android.hardware.camera2.CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link java.util.concurrent.Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * A {@link android.hardware.camera2.CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    int afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        int aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_WAITING_NON_PRECAPTURE;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    int aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (CaptureResult.CONTROL_AE_STATE_PRECAPTURE == aeState) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    } else if (CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED == aeState) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    int aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (CaptureResult.CONTROL_AE_STATE_PRECAPTURE != aeState) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                        CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static android.util.Size chooseOptimalSize(android.util.Size[] choices, int width, int height, android.util.Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<android.util.Size> bigEnough = new ArrayList<android.util.Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (android.util.Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public static CameraPreview21 newInstance() {
        CameraPreview21 fragment = new CameraPreview21();
        fragment.setRetainInstance(true);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mTextureView = (TextureView) view.findViewById(R.id.texture);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                if (characteristics.get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // For still image captures, we use the largest available size.
                android.util.Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        width, height, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
//                int orientation = getResources().getConfiguration().orientation;
//                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    mTextureView.setAspectRatio(
//                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
//                } else {
//                    mTextureView.setAspectRatio(
//                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
//                }

                mCameraId = cameraId;

                mDeviceFocalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                mDeviceActiveSensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                mSensorRatio = (float) mDeviceActiveSensorSize.width() / mDeviceActiveSensorSize.height();
                mDevicePhysicalSensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
                mMaxDigitalZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                mCroppingType = characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE);

                double r = 360 / Math.PI;
                effectiveHAngle = (float) (r * Math.atan(mDevicePhysicalSensorSize.getWidth() / (2 * mDeviceFocalLengths[0])));
                effectiveVAngle = (float) (r * Math.atan(mDevicePhysicalSensorSize.getHeight() /
                        (2 * mDeviceFocalLengths[0])));

                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            new ErrorDialog().show(getFragmentManager(), "dialog");
        }
    }

    /**
     * Opens the camera specified by {@link CameraPreview21#mCameraId}.
     */
    private void openCamera(int width, int height) {
        totalScreenWidth = width;
        totalScreenHeight = height;

        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(5000, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link android.hardware.camera2.CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link android.os.Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link android.os.Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link android.hardware.camera2.CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            ArtemisMath.scaledPreviewWidth = mPreviewSize.getWidth();
            ArtemisMath.scaledPreviewHeight = mPreviewSize.getHeight();

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                // Set first focal length
//                                mPreviewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH,
//                                        mDeviceFocalLengths[0]);
//                                Log.d(TAG, String.format("Setting lens focal length: %f", mDeviceFocalLengths[0]));

                                mTextureView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        startArtemisPreview(false);
                                    }
                                });

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mOriginalCropRegion = mPreviewRequest.get(CaptureRequest.SCALER_CROP_REGION);
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            Activity activity = getActivity();
                            if (null != activity) {
                                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        origin = new Matrix(matrix);
//        matrix.invert(origin);
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    protected void takePicture() {
        lockFocus();
    }

    protected void autofocusCamera(boolean takePic) {

    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when we
     * get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                               TotalCaptureResult result) {
                    Toast.makeText(getActivity(), "Saved: " + mFile, Toast.LENGTH_SHORT).show();
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is finished.
     */
    private void unlockFocus() {
        try {
            // Reset the autofucos trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a JPEG {@link android.media.Image} into the specified {@link java.io.File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<android.util.Size> {

        @Override
        public int compare(android.util.Size lhs, android.util.Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    public static class ErrorDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage("This device doesn't support Camera2 API.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }


}
