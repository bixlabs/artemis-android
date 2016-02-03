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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.util.Log;
import android.util.Size;
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
            supportedFocusModes;
    //    protected static List<Size> supportedPreviewSizes;
    protected static boolean lockBoxEnabled = true, quickshotEnabled = false,
            smoothImagesEnabled = false;
    protected static float deviceHAngle, effectiveHAngle, deviceVAngle,
            effectiveVAngle;

    protected static boolean isAutoFocusSupported = false;
    protected static boolean autoFocusBeforePictureTake = false;
    //    protected static Size previewSize;
    protected static float exposureStep;

    private TextureView mTextureView;
    private static final String logTag = "CameraPreview";

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

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
    private float mActiveSensorRatio;
    private Float mMaxDigitalZoom;
    private Integer mCroppingType;
    private Rect mOriginalCropRegion;
    private float previewRatio;
    private float mScale;
    private Matrix noRotation;
    private Integer mSensorOrientation;
    public static Size[] availablePictureSizes;
    private Size mSelectedPictureSize;
    static public int[] availableAutoFocusModes;
    static public int[] availableEffects;
    static public int[] availableWhiteBalanceModes;
    static public SortedSet<Integer> availableSceneModes;
    private int selectedSceneModeInt;
    private int selectedEffectInt;
    private int selectedFocusInt;
    private int selectedWhiteBalanceInt;
    private int mCenterX, mCenterY;
    private boolean mFlashSupported = false;

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

    public void startArtemisPreview() {

        if (getActivity() == null || !(getActivity() instanceof ArtemisActivity)) {
            return;
        }

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
            _artemisMath.calculateRectBoxesAndLabelsForLenses();
            _artemisMath.resetTouchToCenter(); // now with green box
            _artemisMath.calculateLargestLens();
            _artemisMath.selectFirstMeaningFullLens();
            _artemisMath.calculateRectBoxesAndLabelsForLenses();
            _artemisMath.setInitializedFirstTime(true);
        } else {
            _artemisMath.calculateLargestLens();
            _artemisMath.calculateRectBoxesAndLabelsForLenses();
//            _artemisMath.selectFirstMeaningFullLens();
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
    }

    Matrix origin, inverse;

    public void calculateZoom(boolean shouldCalcScaleFactor) {
        // Calculate the zoom scale and translation

        Matrix endTransform = new Matrix();

        if (_artemisMath.isFullscreen() || !shouldCalcScaleFactor) {
            if (shouldCalcScaleFactor) {
                this.scaleFactor = _artemisMath.calculateFullscreenZoomRatio();
            }
            if (scaleFactor > 1f) {

                endTransform.preConcat(origin);

                endTransform.postScale(scaleFactor, scaleFactor, _artemisMath
                        .getOutsideBox().centerX(), _artemisMath
                        .getOutsideBox().centerY());
            } else {
                endTransform.preConcat(origin);
                endTransform.postScale(scaleFactor, scaleFactor, _artemisMath
                        .getOutsideBox().centerX(), _artemisMath
                        .getOutsideBox().centerY());
                endTransform.postTranslate((_artemisMath.getOutsideBox().centerX() - mCenterX) / 2,
                        (_artemisMath.getOutsideBox().centerY() - mCenterY) / 2);

            }

//            inverse = new Matrix();
//            endTransform.invert(inverse);

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
            bitmap.compress(CompressFormat.JPEG, 100, fos);
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

            int footerHeight = (int) (bitmapToSave.getHeight() * 0.065f);

            int sideborder = 10;
            // add a larger border in 4:3
            if ((float) bitmapToSave.getWidth() / bitmapToSave.getHeight() < 1.4) {
                sideborder = 259; // to match 16:9 for 4:3
            }

            float widthRatio = bitmapToSave.getWidth()/13006f;

            float baseFontSize = footerHeight/2.94f;

            blankBmp = Bitmap.createBitmap(
                    bitmapToSave.getWidth() + sideborder,
                    bitmapToSave.getHeight() + footerHeight,
                    Bitmap.Config.ARGB_8888);
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
                xRef = (int)(1000f*widthRatio);
            } else if (fltext.length() >= 3 && fl < 100) {
                xRef = (int)(1200f*widthRatio);
            } else if (fltext.length() >= 3 && fl >= 100) {
                xRef = (int)(1500f*widthRatio);
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

            paint.setTextSize(baseFontSize-2);
            if (description.length() > 0) {
                canvas.drawText(description, 10, blankBmp.getHeight() - (baseFontSize*2),
                        paint);
            }
            paint.setTextSize(baseFontSize-4);
            if (showCameraDetails) {
                canvas.drawText(ArtemisActivity._cameraDetailsText.getText()
                        .toString(), 10, blankBmp.getHeight() - (baseFontSize), paint);
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
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextScaleX(0.87f);
                    canvas.drawText(gpsDetailsAndLocation[1], centerTextX,
                            blankBmp.getHeight() - 10, paint);
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setTextScaleX(1f);
                }
            }

            paint.setTextAlign(Paint.Align.CENTER);
            if (showHeading && showTiltRoll) {
                canvas.drawText(ArtemisActivity.pictureSaveHeadingTiltString,
                        centerTextX, blankBmp.getHeight() - (baseFontSize*2), paint);
            } else if (showHeading) {

                if (ArtemisActivity.headingDisplaySelection == 1) {
                    canvas.drawText(
                            ArtemisActivity.pictureSaveHeadingTiltString,
                            centerTextX, blankBmp.getHeight() - (baseFontSize*2), paint);
                } else if (ArtemisActivity.headingDisplaySelection == 2) {
                    if (ArtemisActivity.pictureSaveHeadingTiltString
                            .contains("|")) {
                        canvas.drawText(
                                ArtemisActivity.pictureSaveHeadingTiltString
                                        .substring(
                                                0,
                                                ArtemisActivity.pictureSaveHeadingTiltString
                                                        .indexOf('|')),
                                centerTextX, blankBmp.getHeight() - (baseFontSize*2), paint);
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
                            centerTextX, blankBmp.getHeight() - (baseFontSize*2), paint);
                }
            }
            paint.setTextAlign(Paint.Align.LEFT);

            if (showLensViewAngles) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(1);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextSize(baseFontSize-2);
                String hAngle = getResources().getString(R.string.hangle_text)
                        + " "
                        + nf.format(_artemisMath.selectedLensAngleData[0])
                        + getString(R.string.degree_symbol);
                String vAngle = getResources().getString(R.string.vangle_text)
                        + " "
                        + nf.format(_artemisMath.selectedLensAngleData[1])
                        + getString(R.string.degree_symbol);
                int xadjustHAngle = hAngle.length() < 14 ?  (int)(1080*widthRatio) : (int)(1200f*widthRatio);
                int xadjustVAngle = vAngle.length() < 14 ? (int)(1080*widthRatio) : (int)(1200f*widthRatio);
                int xadjust = xadjustHAngle >= xadjustVAngle ? xadjustHAngle
                        : xadjustVAngle;
                canvas.drawText(hAngle, blankBmp.getWidth() - xRef - xadjust,
                        blankBmp.getHeight() - baseFontSize, paint);
                canvas.drawText(vAngle, blankBmp.getWidth() - xRef - xadjust,
                        blankBmp.getHeight() - 10, paint);
            }

            if (showDateTime) {
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextSize(baseFontSize*0.6f);
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "h:mm aa | MM/dd/yyyy", Locale.getDefault());
                canvas.drawText(sdf.format(new Date()), blankBmp.getWidth()
                        - xRef - (int)(widthRatio*1080) , blankBmp.getHeight() - baseFontSize*2, paint);
            }

            if (showLensDetails) {
                String lensMake = ArtemisActivity._lensMakeText.getText()
                        .toString();
                paint.setTextSize(baseFontSize*2.0f);
                canvas.drawText(fltext + "mm", blankBmp.getWidth() - xRef,
                        blankBmp.getHeight() - 10, paint);

                paint.setTextSize(baseFontSize*0.6f);
                if (lensMake.length() > 28) {
                    paint.setTextSize(baseFontSize*0.5f);
                    paint.setTextScaleX(0.9f);
                }
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(lensMake, blankBmp.getWidth() - xRef + (int)(widthRatio*500),
                        blankBmp.getHeight() - baseFontSize*2, paint);

                paint.setStrokeWidth(baseFontSize*0.1f);
                canvas.drawLine(blankBmp.getWidth() - xRef - 13,
                        blankBmp.getHeight() - footerHeight + 10, blankBmp.getWidth() - xRef
                                - 13, blankBmp.getHeight() - 10, paint);
            }
        }

        savePicture(blankBmp, showGpsDetails);
    }

    /**
     *
     * NEW API
     *
     *
     */

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

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

    static protected android.util.Size previewSize;

    /**
     * {@link android.hardware.camera2.CameraDevice.StateCallback} is called when {@link android.hardware.camera2.CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
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
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private static android.util.Size[] findMatchingSizesByRatio(android.util.Size[] choices, android.util.Size aspectRatio, int minWidth) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<android.util.Size> matches = new ArrayList<android.util.Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (android.util.Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w
                    && option.getWidth() >= minWidth) {
                matches.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (matches.size() > 0) {
            return matches.toArray(new Size[matches.size()]);
        } else {
            Log.e(TAG, "Couldn't find any suitable ratio matches");
            return new Size[0];
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

                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                if (mTextureView != null) {
                    Rect r = new Rect();
                    mTextureView.getGlobalVisibleRect(r);
                    mCenterX = r.centerX();
                    mCenterY = r.centerY();
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                int sensorOrientation =
                        characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (sensorOrientation == 90 || sensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (sensorOrientation == 0 || sensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                previewRatio = (float) previewSize.getWidth() / previewSize.getHeight();

                Log.d(TAG, String.format("Selected preview size: %sw  %sh ratio: %s", previewSize.getWidth(), previewSize.getHeight(), previewRatio));

                availablePictureSizes = CameraPreview21.findMatchingSizesByRatio(map.getOutputSizes(ImageFormat.JPEG), previewSize, 1000);

                if (CameraPreview21.savedImageSizeIndex > 0) {
                    mSelectedPictureSize = availablePictureSizes[CameraPreview21.savedImageSizeIndex];
                } else {
                    // Uninitialized. Initialize to largest picture size
                    SharedPreferences prefs = getActivity()
                            .getApplicationContext().getSharedPreferences(
                                    ArtemisPreferences.class.getSimpleName(),
                                    Context.MODE_PRIVATE);
                    mSelectedPictureSize = largest;
                    List<Size> sizeList = Arrays.asList(availablePictureSizes);
                    int index = sizeList.indexOf(largest);
                    prefs.edit().putString(getString(R.string.preference_key_savedImageSize), "" + index).apply();
                }

                mImageReader = ImageReader.newInstance(mSelectedPictureSize.getWidth(), mSelectedPictureSize.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                Log.d(TAG, String.format("Total available screen size: %sw  %sh ratio: %s", totalScreenWidth, totalScreenHeight, (float) totalScreenWidth / totalScreenHeight));

                Log.d(TAG, String.format(
                        "Selected image reader size: %sw  %sh ratio: %s",
                        mSelectedPictureSize.getWidth(),
                        mSelectedPictureSize.getHeight(),
                        (float) mSelectedPictureSize.getWidth() / mSelectedPictureSize.getHeight()));

                mCameraId = cameraId;


                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;


                SharedPreferences artemisPrefs = getActivity().getSharedPreferences(
                        ArtemisPreferences.class.getSimpleName(), Activity.MODE_PRIVATE);

                mDeviceFocalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                mDeviceActiveSensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                mActiveSensorRatio = (float) mDeviceActiveSensorSize.width() / mDeviceActiveSensorSize.height();
                mDevicePhysicalSensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
                mMaxDigitalZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                mCroppingType = characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE);
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                availableWhiteBalanceModes = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
                availableAutoFocusModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
                availableEffects = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
                int[] sceneModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
                if (sceneModes != null) {
                    availableSceneModes = new TreeSet<>();
                    availableSceneModes.add(0);
                    for (int mode : sceneModes) {
                        availableSceneModes.add(mode);
                    }
                    String selectedSceneMode = artemisPrefs.getString(
                            getString(R.string.preference_key_selectedscenemode), "0");
                    this.selectedSceneModeInt = Integer.parseInt(selectedSceneMode);
                }
                if (availableEffects != null) {
                    Arrays.sort(availableAutoFocusModes);
                    String selectedEffect = artemisPrefs.getString(
                            getString(R.string.preference_key_selectedCameraEffect), "0");
                    this.selectedEffectInt = Integer.parseInt(selectedEffect);
                }

                if (availableAutoFocusModes != null) {
//                    Arrays.sort(availableAutoFocusModes);
                    String selectedAutoFocus = artemisPrefs.getString(
                            getString(R.string.preference_key_selectedfocusmode), "4");
                    this.selectedFocusInt = Integer.parseInt(selectedAutoFocus);
                }

                if (availableWhiteBalanceModes != null) {
//                    Arrays.sort(availableWhiteBalanceModes);
                    String selectedWB = artemisPrefs.getString(
                            getString(R.string.preference_key_selectedwhitebalance), "0");
                    this.selectedWhiteBalanceInt = Integer.parseInt(selectedWB);
                }

                if (!(availableAutoFocusModes.length == 1 && availableAutoFocusModes[0] == CameraCharacteristics.CONTROL_AF_MODE_OFF)) {
                    isAutoFocusSupported = true;
                }

                double r = 360 / Math.PI;
                deviceHAngle = effectiveHAngle = (float) (r * Math.atan(mDevicePhysicalSensorSize.getWidth() / (2 * mDeviceFocalLengths[0])));
                deviceVAngle = effectiveVAngle = (float) (r * Math.atan(mDevicePhysicalSensorSize.getHeight() /
                        (2 * mDeviceFocalLengths[0])));

                if (!artemisPrefs.getBoolean(
                        getActivity().getString(
                                R.string.preference_key_automaticlensangles), true)) {
                    CameraPreview21.effectiveHAngle = artemisPrefs.getFloat(
                            ArtemisPreferences.CAMERA_LENS_H_ANGLE,
                            CameraPreview21.deviceHAngle);
                    CameraPreview21.effectiveVAngle = artemisPrefs.getFloat(
                            ArtemisPreferences.CAMERA_LENS_V_ANGLE,
                            CameraPreview21.deviceVAngle);
                }

                boolean first = true;
                for (float fl : mDeviceFocalLengths) {
                    if (!first) {
                        Log.d(TAG, String.format("Device focal length available: %f", fl));
                    } else {
                        Log.d(TAG, String.format("Using device focal length: %f", fl));
                        first = false;
                    }
                }
                Log.d(TAG, String.format("Device physical sensor size: %s  ratio: %f", mDevicePhysicalSensorSize.toString(), (float) mDevicePhysicalSensorSize.getWidth() / mDevicePhysicalSensorSize.getHeight()));
                Log.d(TAG, String.format("Device active sensor size: %s  ratio: %f", mDeviceActiveSensorSize.toString(), mActiveSensorRatio));
                Log.d(TAG, String.format("Device sensor orientation angle: %d", mSensorOrientation));
                Log.d(TAG, String.format("Calculated device view angles: %f horiz x %f vert", deviceHAngle, deviceVAngle));
                Log.d(TAG, String.format("Effective view angles: %f horiz x %f vert", effectiveHAngle, effectiveVAngle));

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
        totalScreenWidth = width > height ? width : height;
        totalScreenHeight = width > height ? height : width;

//        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestCameraPermission();
//            return;
//        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    private void requestCameraPermission() {
        if (FragmentCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            ArtemisMath.scaledPreviewWidth = previewSize.getWidth();
            ArtemisMath.scaledPreviewHeight = previewSize.getHeight();

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
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
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
                                setAutoFlash(mPreviewRequestBuilder);

                                if (selectedSceneModeInt > 0) {
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,
                                            selectedSceneModeInt);
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE,
                                            CaptureRequest.CONTROL_MODE_USE_SCENE_MODE);
                                } else {
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE,
                                            CaptureRequest.CONTROL_MODE_AUTO);
                                }
                                if (selectedEffectInt > 0) {
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE,
                                            selectedEffectInt);
                                }
                                if (selectedFocusInt > 0) {
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                            selectedFocusInt);
                                }
                                if (selectedWhiteBalanceInt > 0) {
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE,
                                            selectedWhiteBalanceInt);
                                }

                                mTextureView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        startArtemisPreview();
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
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
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
        if (null == mTextureView || null == previewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            Log.d(TAG, String.format("Configure Transform: offset %f, %f", centerX - bufferRect.centerX(), centerY - bufferRect.centerY()));
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            mScale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            Log.d(TAG, String.format("Configure Transform: scale %f", mScale));

            matrix.postScale(mScale, mScale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
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
        lockFocus();
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

            setAutoFlash(captureBuilder);

            if (selectedEffectInt > 0) {
                captureBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE,
                        selectedEffectInt);
            }
            if (selectedWhiteBalanceInt > 0) {
                captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE,
                        selectedWhiteBalanceInt);
            }

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation) + (mSensorOrientation - 90));

            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
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
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                        CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL);
            }
            setAutoFlash(mPreviewRequestBuilder);
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

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

//    @Override
//    public void getBitmap(Bitmap bitmap) {
//        Log.d(TAG, String.format("getBitmap is %s null", bitmap!=null ? "not " : ""));
//        bitmapToSave = bitmap;
//        renderPictureDetailsAndSave();
//    }

    /**
     * Saves a JPEG {@link android.media.Image} into the specified {@link java.io.File}.
     */
    private class ImageSaver implements Runnable {

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
            buffer.rewind();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            bitmapToSave = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mImage.close();

            final RectF selectedRect = _artemisMath.getSelectedLensBox();
            final RectF greenRect = _artemisMath.getOutsideBox();
            final RectF screenRect = new RectF(0, 0, mTextureView.getWidth(), mTextureView.getHeight());

            final int imageWidth = bitmapToSave.getWidth();
            final int imageHeight = bitmapToSave.getHeight();

            final float screenWRatio = (float) mTextureView.getWidth() / mTextureView.getHeight();
            final float screenHRatio = (float) 1 / screenWRatio;


            int newHeight = (int) (imageWidth * screenHRatio);
            int newWidth = (int) (imageWidth);
            int widthDiff = (int) ((imageWidth - newWidth) / 2f);
            int heightDiff = (int) ((imageHeight - newHeight) / 2f);

            float hratio = greenRect.height() / greenRect.width();

            Log.d(TAG, String.format("screenWRatio %f", screenWRatio));

            if (scaleFactor >= 1) {

                int orig_width = bitmapToSave.getWidth();

                bitmapToSave = Bitmap.createBitmap(bitmapToSave, widthDiff, heightDiff, newWidth, newHeight, null, false);
                float screenImageWRatio = bitmapToSave.getWidth() / screenRect.width();
                bitmapToSave = Bitmap.createBitmap(bitmapToSave,
                        (int) (greenRect.left * screenImageWRatio),
                        (int) (greenRect.top * screenImageWRatio),
                        (int) (greenRect.width() * screenImageWRatio),
                        (int) (greenRect.height() * screenImageWRatio), null, false);
                float greenToSelectedRatio = bitmapToSave.getWidth() / greenRect.width();
                bitmapToSave = Bitmap.createBitmap(bitmapToSave,
                        (int) ((selectedRect.left - greenRect.left) * greenToSelectedRatio),
                        (int) ((selectedRect.top - greenRect.top) * greenToSelectedRatio),
                        (int) (selectedRect.width() * greenToSelectedRatio),
                        (int) (selectedRect.height() * greenToSelectedRatio), null, false);
                bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave, orig_width, (int)(orig_width * hratio), smoothImagesEnabled);
            } else {
                // Zoomed out, we need to scale the image down
                Log.d(logTag, "Scale down on save");
                Bitmap canvasBitmap = Bitmap.createBitmap(imageWidth,
                        (int) (imageWidth * hratio), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(canvasBitmap);
                Paint p = new Paint();
                float scale = _artemisMath.calculateFullscreenZoomRatio() * (totalScreenWidth / greenRect.width());

                bitmapToSave = Bitmap.createScaledBitmap(bitmapToSave,
                        (int) (imageWidth * scale), (int) (imageHeight * scale),
                        smoothImagesEnabled);

                int xpos = (canvasBitmap.getWidth() - bitmapToSave.getWidth()) / 2;
                int ypos = (canvasBitmap.getHeight() - bitmapToSave.getHeight()) / 2;

                // draw background
                c.drawBitmap(ArtemisActivity.arrowBackgroundImage, null,
                        new Rect(0, 0, c.getWidth(), c.getHeight()), p);

                c.drawBitmap(bitmapToSave, xpos, ypos, p);

                bitmapToSave = canvasBitmap;
            }

            if (!quickshotEnabled) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArtemisActivity.pictureSavePreview.setImageBitmap(bitmapToSave);
                        ArtemisActivity.viewFlipper.setInAnimation(null);
                        ArtemisActivity.viewFlipper.setDisplayedChild(3);
                        ArtemisActivity.currentViewId = R.id.savePictureViewFlipper;
                    }
                });
            } else {
                final Toast toast = Toast.makeText(getContext(), getContext()
                                .getString(R.string.image_saved_success),
                        Toast.LENGTH_SHORT);
                toast.show();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        renderPictureDetailsAndSave();
                        System.gc();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        toast.cancel();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            getActivity()
                                    .sendBroadcast(
                                            new Intent(
                                                    Intent.ACTION_MEDIA_MOUNTED,
                                                    Uri.parse("file://"
                                                            + Environment
                                                            .getExternalStorageDirectory())));
                        }
                    }
                }.execute();
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

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(parent,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }


}
