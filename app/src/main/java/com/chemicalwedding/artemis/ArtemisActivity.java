package com.chemicalwedding.artemis;

<<<<<<< HEAD
<<<<<<< HEAD
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
<<<<<<< HEAD
=======
=======

<<<<<<< HEAD:app/src/main/java/com/chemicalwedding/artemis/ArtemisActivity.java
>>>>>>> f78dc0f (Configure Kotlin)
=======
import java.io.ByteArrayOutputStream;
>>>>>>> 99abbaa (vstand-ins):artemis-android/src/com/chemicalwedding/artemis/ArtemisActivity.java
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
>>>>>>> 4c62fd4 (3.0.5.1 uploaded to the app store for fix remembering lens selections)
=======
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
>>>>>>> ed0b9bd (Look and feel changes)
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
import android.support.annotation.Nullable;
>>>>>>> 28c22f9 (milestone 2 Look and Feel changes to main view and menu)
=======
>>>>>>> ed0b9bd (Look and feel changes)
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
<<<<<<< HEAD
=======
import android.support.v4.content.ContextCompat;
>>>>>>> 4c62fd4 (3.0.5.1 uploaded to the app store for fix remembering lens selections)
=======
>>>>>>> ed0b9bd (Look and feel changes)
=======
=======

>>>>>>> f78dc0f (Configure Kotlin)
import androidx.annotation.Nullable;
=======

>>>>>>> 8bb9eb3 (fixes file permissions management)
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
<<<<<<< HEAD
>>>>>>> 9899aa0 (Migrate to AndroidX. Update target SDK version to 29)
=======

>>>>>>> f78dc0f (Configure Kotlin)
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.bumptech.glide.Glide;
import com.chemicalwedding.artemis.LongPressButton.ClickBoolean;
import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.chemicalwedding.artemis.database.Camera;
import com.chemicalwedding.artemis.database.CustomCamera;
import com.chemicalwedding.artemis.database.Lens;
import com.chemicalwedding.artemis.database.LensAdapter;
import com.chemicalwedding.artemis.database.Look;
import com.chemicalwedding.artemis.database.MediaFile;
import com.chemicalwedding.artemis.database.MediaType;
import com.chemicalwedding.artemis.database.SaveMetadataToMoviesOptions;
import com.chemicalwedding.artemis.database.ZoomLens;
import com.chemicalwedding.artemis.model.Extender;
import com.chemicalwedding.artemis.model.ExtenderAdapter;
import com.chemicalwedding.artemis.model.Frameline;
import com.chemicalwedding.artemis.model.FramelineRate;
import com.chemicalwedding.artemis.model.FramelineRatesAdapter;
<<<<<<< HEAD
<<<<<<< HEAD:app/src/main/java/com/chemicalwedding/artemis/ArtemisActivity.java
=======
import com.chemicalwedding.artemis.utils.FileUtils;
import com.chemicalwedding.artemis.utils.FramelineDrawingUtils;
import com.chemicalwedding.artemis.utils.ImageUtils;
import com.chemicalwedding.artemis.utils.VideoUtils;
>>>>>>> db76629 (add color picker for virtual stand ins):artemis-android/src/com/chemicalwedding/artemis/ArtemisActivity.java
import com.chemicalwedding.artemis.vstandins.ModelGLSurfaceView;
import com.chemicalwedding.artemis.vstandins.ModelRenderer;
import com.chemicalwedding.artemis.vstandins.SceneLoader;
import com.chemicalwedding.artemis.vstandins.TouchController;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.model.Object3DData;
=======
import com.chemicalwedding.artemis.utils.ArtemisFileUtils;
>>>>>>> 8bb9eb3 (fixes file permissions management)
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.sbstrm.appirater.Appirater;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import org.jcodec.containers.mp4.boxes.MetaValue;
import org.jcodec.movtool.MetadataEditor;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_OFF;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;


public class ArtemisActivity extends Activity implements
        CameraPreview21.RecordingCallback,
        LensAdaptersAdapter.SelectedLensAdapterCallback,
        FramelinesAdapter.FramelinesCallback,
        FramelineRatesAdapter.SelectedFramelineRateCallback,
        ExtenderAdapter.ExtenderCallback,
        ModelRenderer.BitmapGeneratedCallback
{
    private static final String TAG = ArtemisActivity.class.getSimpleName();

    private static final String DEFAULT_LENS_MAKE = "Generic Spherical Lenses";
    private static final int GALLERY_IMAGE_LOADER = 1;
    private static final int NUM_CAMERA_PAGES = 4;
    private static final int CHOOSE_MODEL_REQUEST = 13335;
    private static final int CHOOSEN_MODEL_DEFAULT_VALUE = -1;

    public static List<Frameline> appliedFramelines;

    private Handler mUiHandler = new Handler();

    private CameraPreview21 mCameraPreview;

    private LongPressButton _nextLensButton;
    private LongPressButton _prevLensButton;
    private ClickBoolean nextClickBoolean;
    private ClickBoolean prevClickBoolean;
    protected static ViewFlipper viewFlipper;
    private ViewFlipper _cameraSettingsFlipper;
    private ViewFlipper _lensSettingsFlipper;
    private ViewFlipper savePictureViewFlipper;
    protected static TextView _cameraDetailsText;
    protected static TextView _lensMakeText;
    protected static TextView headingTiltText;
    private ListView _lensListView;
    protected static TextView _lensFocalLengthText;
    protected CameraOverlay mCameraOverlay;
    protected CameraAngleDetailView mCameraAngleDetailView;

    private ArtemisDatabaseHelper _artemisDBHelper;

    protected static int currentViewId;
    // camera related
    private Camera _selectedCamera, tempSelectedCamera;
    private ArrayList<String> _allCameraFormats;
    // camera rowid paired with ratio
    private ArrayList<Pair<Integer, String>> _ratiosListForCamera;

    // lens related
    private ArrayList<Lens> _lensesForMake, tempLensesForMake;
    String tempSelectedLensMake;
    private ArrayList<Lens> _selectedLenses = new ArrayList<Lens>();
    private ArrayList<CustomCamera> customCameras;
    private ArrayList<ZoomLens> zoomLenses;
    private RelativeLayout addCustomLensLayout;

    // use to handle math calculations
    private static ArtemisMath _artemisMath = ArtemisMath.getInstance();

    protected static ImageView pictureSavePreview;

    private LocationManager locationManager;
    private SensorManager sensorManager;
    private String locationProvider;
    private static Location lastKnownLocation;
    protected static Location pictureSaveLocation;
    protected static String pictureSaveHeadingTiltString;

    protected static boolean gpsEnabled = false, sensorEnabled = false;
    protected static int headingDisplaySelection = 2;

    protected static Bitmap arrowBackgroundImage;

    private boolean wasFocalLengthButtonPressed = false;
    private boolean isEditingPictureDetailsOnly = false;
    private boolean isHapticFeedbackEnabled = true;
    private String backPressedMode;
    protected boolean takePictureAfterAutoFocusAndLongClickShutter = true;
    protected boolean autoFocusAfterLongClickShutter = true;
    protected boolean takePictureAfterReleaseLongClickShutter = false;

    private boolean volumeUpAutoFocusAndPicture;
    private boolean volumeUpPicture;
    private boolean volumeUpAutoFocus;
    private boolean volumeDownAutoFocusAndPicture;
    private boolean volumeDownPicture;
    private boolean volumeDownAutoFocus;

    protected static final long lensRepeatSpeedCustomLens = 35;
    protected static final long lensRepeatSpeedNormal = 200;
    private String mSelectedFormat;


    protected ImageView takePictureButton;
    protected ImageView recordVideoButton;
    protected Chronometer recordVideoChronometer;
    protected LinearLayout recordVideoChronometerContainer;
    protected boolean isRecordingVideo;
    protected File videoFolder;
    protected String videoFileName;

    private ImageView looksButton;
    private ImageView addLensAdapterButton;
    private LinearLayout addLensAdapterView;
    private LinearLayout mainMenu;
    private TextView addCustomlensAdapterButton;
    private Guideline horizontalGuidelineBottom;
    private TextView selectedLensAdapter;
    private TextView removeLensAdapterButton;
    private ImageView loadingIndicator;
    private RelativeLayout loadingIndicatorContainer;
    private AnimatorSet frontAnim;
    private AnimatorSet backAnim;
    private boolean isFront;
    private LinearLayout selectedLensAdapterMilis;
    private Timer time;
<<<<<<< HEAD
    private LinearLayout lensMakeTextContainer;
    private TextView lensFocalLengthMM;
    private ImageView menuButton;
    private ImageView heliosImage;
<<<<<<< HEAD
=======
>>>>>>> ed0b9bd (Look and feel changes)
=======
    private TextView addExtenderButton;
    private TextView addFramelineButton;
    private Frameline currentFrameline = null;
    private ImageView framelineBackButton;
    public static Extender selectedExtender;
>>>>>>> cf3855e (add lens extender and framelines)

    private Uri modelUri;

    private SceneLoader scene;
    private ModelGLSurfaceView glSurfaceView;
    private Handler handler;
    public static boolean takeScreenshot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Artemis Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        currentViewId = R.id.artemisPreview;

        SharedPreferences settings = this.getSharedPreferences(
                ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);

        // Check settings if we should keep the screen on
        if (settings.getBoolean(
                getString(R.string.preference_key_keepscreenon), false)) {
            getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Avoid the top bar animation in fullscreen app
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Hide the system ui on create so it doesn't show for a split second
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        // Appirator
        Appirater.appLaunched(this);

//        if (null == savedInstanceState) {
        mCameraPreview = CameraPreview21.newInstance(ArtemisActivity.this);
        getFragmentManager().beginTransaction()
                .add(R.id.cameraContainer, mCameraPreview)
                .commit();
//        }

        startArtemis();
        appliedFramelines = _artemisDBHelper.getAppliedFramelines();
        mCameraOverlay.invalidate();
    }

    protected void startArtemis() {
        // Connect member variables to view objects
        bindViewObjects();

        // Setup listeners for events
        bindViewEvents();

        // connect to the database and load some initial data
        initDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying Artemis");

        // Close the database connection
        if (_artemisDBHelper != null) {
            _artemisDBHelper.close();
            _artemisDBHelper = null;
        }
    }

<<<<<<< HEAD
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "Pausing Artemis");
<<<<<<< HEAD
		_cameraPreview.releaseCamera();
<<<<<<< HEAD
		mCameraContainer.removeAllViews();
=======
>>>>>>> f0bf7d9 (Revert back galaxy nexus changes)
=======
>>>>>>> fde0e9e (Changes to make the preview start properly on the nexus 5)
=======
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Pausing Artemis");
>>>>>>> ed45a34 (more cleanups)

        if (gpsEnabled && locationManager != null)
            locationManager.removeUpdates(locationListener);

        if (sensorEnabled)
            sensorManager.unregisterListener(sensorEventListener);

        if (ArtemisActivity.arrowBackgroundImage != null) {
            ArtemisActivity.arrowBackgroundImage.recycle();
            ArtemisActivity.arrowBackgroundImage = null;
        }

        if(glSurfaceView != null) {
//            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Stopping Artemis");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Starting Artemis");

        Tracker tracker = ((ArtemisApplication) getApplication()).getTracker();
        tracker.setScreenName("ArtemisActivity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
<<<<<<< HEAD
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "Resuming Artemis");

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
		_cameraPreview = new CameraPreview14(this, null);
		mCameraContainer.addView(_cameraPreview, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

=======
>>>>>>> f0bf7d9 (Revert back galaxy nexus changes)
		if (_cameraPreview.isCameraReleased) {
			_cameraPreview.openCamera();
			// _artemisMath.calculateRectBoxesAndLabelsForLenses();
		}

<<<<<<< HEAD
=======
>>>>>>> fde0e9e (Changes to make the preview start properly on the nexus 5)
=======
		initPreferences();
<<<<<<< HEAD
<<<<<<< HEAD
		
>>>>>>> 71b640f (Fix resuming from settings and new changes not being applied)
=======

<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> 42f959f (Fix custom camera calibration activity on kitkat)
=======
		initCamera();
=======
		reinitCamera();
>>>>>>> e819b9d (Make resume work better, fix resume from settings)
=======
		if (Build.VERSION.SDK_INT < 19) {
			reinitCamera();
		}
>>>>>>> 7476643 (Fix bugs... don't show close lens to orange box, save and edit picture)
=======

		reinitCamera();
>>>>>>> a589c79 (Fix nexus 5 restart preview bug)

>>>>>>> 4c8ce40 (Finally nailed it, reuse the texture view on resume)
		initSensorManager();

		initLocationManager();

		// Set the background
		if (ArtemisActivity.arrowBackgroundImage == null) {
			Options o = new Options();
			o.inSampleSize = 2;
			ArtemisActivity.arrowBackgroundImage = BitmapFactory
					.decodeResource(getResources(), R.drawable.arrows, o);
		}
	}

	private boolean isSurfaceAvailable = false;

	private void reinitCamera() {
//		if (mCamera == null && mTextureView != null && isSurfaceAvailable) {
//
//			mCamera = android.hardware.Camera.open();
//
//			try {
//				mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
//			} catch (IOException t) {
//			}
//
//			mCameraPreview.openCamera(mCamera, false);
//			_artemisMath.calculateRectBoxesAndLabelsForLenses();
//			this.reconfigureNextAndPreviousLensButtons();
//
//		}
	}

	private void initSensorManager() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(sensorEventListener,
=======
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resuming Artemis");

        initPreferences();

        initSensorManager();

        initLocationManager();

        // Set the background
        if (ArtemisActivity.arrowBackgroundImage == null) {
            Options o = new Options();
            o.inSampleSize = 2;
            ArtemisActivity.arrowBackgroundImage = BitmapFactory
                    .decodeResource(getResources(), R.drawable.arrows, o);
        }

        if(glSurfaceView != null) {
//            glSurfaceView.onResume();
        }
    }

    private boolean isSurfaceAvailable = false;

    private void initSensorManager() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener,
>>>>>>> ed45a34 (more cleanups)
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);
        sensorEnabled = true;
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelEvent(event);
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magEvent(event);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        float[] lastAccelValue;

        private void accelEvent(SensorEvent event) {

            if (lastAccelValue == null) {
                lastAccelValue = new float[3];
            }
            System.arraycopy(event.values, 0, lastAccelValue, 0, 3);
        }

        float[] lastMagValue;

        private void magEvent(SensorEvent event) {
            if (lastMagValue == null) {
                lastMagValue = new float[3];
            }
            System.arraycopy(event.values, 0, lastMagValue, 0, 3);

            if (lastAccelValue != null) {
                computeOrientation();
            }
        }

        float[] inR = new float[16];
        float[] outR = new float[16];
        float[] orientationVector = new float[4];

        FloatAverage yawAvg = new FloatAverage(),
                pitchAvg = new FloatAverage(), rollAvg = new FloatAverage();

        private void computeOrientation() {

            if (SensorManager.getRotationMatrix(inR, null, lastAccelValue,
                    lastMagValue)) {

                SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X,
                        SensorManager.AXIS_Z, outR);

                SensorManager.getOrientation(outR, orientationVector);
                yawAvg.add(orientationVector[0]);
                pitchAvg.add(orientationVector[1]);
                rollAvg.add(orientationVector[2]);

                int yaw = (int) Math.toDegrees(yawAvg.average());
                if (yaw < 0) {
                    yaw += 360;
                }
                int pitch = (int) Math.toDegrees(pitchAvg.average());
                int roll = (int) Math.toDegrees(rollAvg.average()) + 90;

                String heading = "";
                if (headingDisplaySelection > 0) {
                    heading = azimuthToString(yaw);
                    if (headingDisplaySelection > 1)
                        heading += " | " + pitchToString(pitch) + " | "
                                + rollToString(roll);
                }
                if (headingTiltText != null)
                    headingTiltText.setText(heading);
            }
        }

        final class FloatAverage {
            final static int SAMPLE = 5;
            ArrayList<Float> lastValues = new ArrayList<Float>();

            public void add(Float newFloat) {
                lastValues.add(newFloat);
                if (lastValues.size() > SAMPLE)
                    lastValues.remove(0);
            }

            public Float average() {
                if (lastValues.size() > 0) {
                    float sum = 0;
                    for (Float val : lastValues) {
                        sum += val;
                    }
                    return sum / lastValues.size();
                } else
                    return null;
            }
        }

        private String azimuthToString(int azimuth) {
            String azString = "";

            // Add extra spaces to azimuth if required
            final String spaces = "  ";
            String azimuthFormatted = Integer.toString(azimuth);
            int spacesRequired = 3 - azimuthFormatted.length();
            azimuthFormatted = azimuthFormatted
                    + getString(R.string.degree_symbol)
                    + spaces.substring(0, spacesRequired);

            // 1st quadrant

            if (azimuth < 5 || azimuth > 355) {
                azString = azimuthFormatted + "N ";
            } else if (azimuth > 0 && azimuth < 22.5) {

                azString = azimuthFormatted + "N ";
            } else if (azimuth > 22.5 && azimuth < 67.5) {

                azString = azimuthFormatted + "NE";
            }
            // 2nd Quadrant
            else if (azimuth > 67.5 && azimuth < 112.5) {

                azString = azimuthFormatted + "E ";
            } else if (azimuth > 112.5 && azimuth < 157.5) {

                azString = azimuthFormatted + "SE";
            }
            // 3rd Quadrant
            else if (azimuth > 157.5 && azimuth < 202.5) {

                azString = azimuthFormatted + "S ";
            } else if (azimuth > 202.5 && azimuth < 247.5) {

                azString = azimuthFormatted + "SW";
            }
            // 4th Quadrant
            else if (azimuth > 247.5 && azimuth < 292.5) {

                azString = azimuthFormatted + "W ";
            } else if (azimuth > 292.5 && azimuth < 337.5) {

                azString = azimuthFormatted + "NW";
            } else if (azimuth > 337.5 && azimuth < 360) {

                azString = azimuthFormatted + "N ";
            }
            return azString;
        }

        private String pitchToString(int pitch) {

            final String spaces = "          ";
            String pitchFormatted = Integer.toString(Math.abs(pitch));
            final String degrees = getString(R.string.degree_symbol);
            if (pitch >= -1 && pitch <= 1) {
                pitchFormatted = "0" + degrees + getString(R.string.tilt_level)
                        + " ";
            } else if (pitch < -1) {
                pitchFormatted = pitchFormatted + degrees
                        + getString(R.string.tilt_up);
                int spacesRequired = 4 - pitchFormatted.length();
                pitchFormatted = pitchFormatted
                        + spaces.substring(0, spacesRequired);

            } else if (pitch > 1) {
                pitchFormatted = pitchFormatted + degrees
                        + getString(R.string.tilt_down);
                int spacesRequired = 4 - pitchFormatted.length();
                pitchFormatted = pitchFormatted
                        + spaces.substring(0, spacesRequired);
            }

            return pitchFormatted;
        }

        private String rollToString(int roll) {

            final String spaces = "            ";
            String rollFormatted = Integer.toString(Math.abs(roll));
            final String degrees = getString(R.string.degree_symbol);
            if (roll >= -1 && roll <= 1) {
                rollFormatted = "0" + degrees + getString(R.string.roll_level)
                        + "  ";
            } else if (roll < -1) {
                rollFormatted = rollFormatted + degrees
                        + getString(R.string.left_roll);
                int spacesRequired = 5 - rollFormatted.length();
                rollFormatted = rollFormatted
                        + spaces.substring(0, spacesRequired);

            } else if (roll > 1) {
                rollFormatted = rollFormatted + degrees
                        + getString(R.string.right_roll);
                int spacesRequired = 5 - rollFormatted.length();
                rollFormatted = rollFormatted
                        + spaces.substring(0, spacesRequired);
            }

            return rollFormatted;
        }
    };

    private void initLocationManager() {
        if (gpsEnabled) {
            Log.i(TAG, "Artemis GPS is enabled");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            locationProvider = locationManager.getBestProvider(criteria, true);
            if (locationProvider != null
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                lastKnownLocation = locationManager
                        .getLastKnownLocation(locationProvider);
                Log.d(TAG, "Last known location is "
                        + (lastKnownLocation == null ? "null" : "not null"));
                locationManager.requestLocationUpdates(locationProvider, 60000, // 60
                        // secs
                        300, // 300 meters
                        locationListener);
            }
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Updating Location");
            lastKnownLocation = location;
            Log.d(TAG, "Updated location is "
                    + (lastKnownLocation == null ? "null" : "not null"));

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void bindViewObjects() {


//		mTextureView = new TextureView(this);
//		mTextureView.setSurfaceTextureListener(this);
//		mCameraPreview.setTextureView(mTextureView);
//		mCameraPreview.addView(mTextureView);

        mCameraOverlay = (CameraOverlay) findViewById(R.id.cameraOverlay);
        mCameraAngleDetailView = (CameraAngleDetailView) findViewById(R.id.CameraAngleDetailView);
        viewFlipper = (ViewFlipper) findViewById(R.id.mainViewFlipper);
        _lensSettingsFlipper = (ViewFlipper) findViewById(R.id.lens_settings_flipper);
        _cameraDetailsText = (TextView) findViewById(R.id.cameraDetailsText);
        _lensMakeText = (TextView) findViewById(R.id.lensMakeText);
        headingTiltText = (TextView) findViewById(R.id.headingTiltText);
        _nextLensButton = (LongPressButton) findViewById(R.id.nextButton);
        _prevLensButton = (LongPressButton) findViewById(R.id.prevButton);
        nextClickBoolean = _nextLensButton.getClickBoolean();
        prevClickBoolean = _prevLensButton.getClickBoolean();

        // keep binding more objects
        _lensFocalLengthText = (TextView) findViewById(R.id.lensFocalLengthText);
        pictureSavePreview = (ImageView) findViewById(R.id.savePicturePreview);
        savePictureViewFlipper = (ViewFlipper) findViewById(R.id.savePictureViewFlipper);
        _lensListView = (ListView) findViewById(R.id.lensList);
        addCustomLensLayout = (RelativeLayout) findViewById(R.id.addCustomLens);

        recordVideoChronometerContainer = findViewById(R.id.videoChronometerContainer);
        recordVideoChronometerContainer.setVisibility(View.INVISIBLE);

        recordVideoChronometer = findViewById(R.id.videoChronometer);

<<<<<<< HEAD
<<<<<<< HEAD
=======
        addLensAdapterButton = findViewById(R.id.addLensAdapterButton);
>>>>>>> ed0b9bd (Look and feel changes)
=======
        looksButton = findViewById(R.id.look_active);


>>>>>>> 449fcf5 (Add looks interface. Apply look to stills and video mode. Delete looks)
        addCustomlensAdapterButton = findViewById(R.id.addCustomLensAdapterButton);
        addLensAdapterView = findViewById(R.id.addLensAdapterView);
        mainMenu = findViewById(R.id.mainMenu);
        horizontalGuidelineBottom = findViewById(R.id.horizontal_guideline_bottom);
        selectedLensAdapter = findViewById(R.id.selectedLensAdapter);
        removeLensAdapterButton = findViewById(R.id.removeLensAdapterButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicatorContainer = findViewById(R.id.loadingIndicatorContainer);
        selectedLensAdapterMilis = findViewById(R.id.selectedLensAdapterMilis);

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        selectedLensAdapterMilis.setCameraDistance(8000 * scale);
        mCameraAngleDetailView.setCameraDistance(8000 * scale);

        frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(ArtemisActivity.this.getApplicationContext(), R.animator.front_animator);
        backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(ArtemisActivity.this.getApplicationContext(), R.animator.back_animator);

        time = new Timer();
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArtemisActivity.this.runAnimation();
                    }
                });
            }
        }, 0, 1550);
<<<<<<< HEAD

        lensMakeTextContainer = findViewById(R.id.lensMakeTextContainer);
        lensFocalLengthMM = findViewById(R.id.lensFocalLengthMM);
        menuButton = findViewById(R.id.menuButton);
        heliosImage = findViewById(R.id.heliosImageView);
        setHeliosImageRotation();
        setLensMakeTextAnimation();
<<<<<<< HEAD
=======
>>>>>>> ed0b9bd (Look and feel changes)
=======

        addExtenderButton = findViewById(R.id.addExtenderButton);
        addFramelineButton = findViewById(R.id.addFramelineButton);
>>>>>>> cf3855e (add lens extender and framelines)
    }

    public void runAnimation() {
        if (isFront) {
            frontAnim.setTarget(selectedLensAdapterMilis);
            backAnim.setTarget(mCameraAngleDetailView);
            frontAnim.start();
            backAnim.start();
            isFront = false;
        } else {
            frontAnim.setTarget(mCameraAngleDetailView);
            backAnim.setTarget(selectedLensAdapterMilis);
            frontAnim.start();
            backAnim.start();
            isFront = true;
        }
    }

    private void bindViewEvents() {
        mCameraOverlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("bixlabs", "preview tapped");
                if (isRecordingVideo) {
                    mCameraPreview.stopRecording();
                } else if (addLensAdapterView.getVisibility() == View.VISIBLE) {
                    hideLensAdapterViewAndShowMainMenu();
                }
            }
        });

        recordVideoButton = ((ImageView) findViewById(R.id.recordVideo));

        recordVideoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingIndicatorContainer.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (isRecordingVideo) {
                    hideGlView();
                    loadingIndicatorContainer.setVisibility(View.VISIBLE);
                    System.out.println("container is visible");
                    mCameraPreview.stopRecording();
                } else {
                    if (lastKnownLocation != null)
                        pictureSaveLocation = lastKnownLocation;
                    mCameraPreview.startRecording();
                }
            }
        });

        recordVideoChronometer.setOnChronometerTickListener(chronometer -> {
            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            int h = (int) (time / 3600000);
            int m = (int) (time - h * 3600000) / 60000;
            int s = (int) (time - h * 3600000 - m * 60000) / 1000;
            // (h < 10 ? "0"+h: h)+":"
            String t = (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
            chronometer.setText(t);
        });
        recordVideoChronometer.setBase(SystemClock.elapsedRealtime());
        recordVideoChronometer.setText("00:00");

        // Setup help button and help overlay touch events
        ((View) findViewById(R.id.helpButtonView))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openHelpFile();
                        if (isHapticFeedbackEnabled) {
                            buzz(v);
                        }
                    }
                });

        ((View) findViewById(R.id.cameraDetailsView))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCameraSettingsView();
                        if (isHapticFeedbackEnabled) {
                            buzz(v);
                        }
                    }
                });

        ((View) findViewById(R.id.lensMakeView))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wasFocalLengthButtonPressed = false;
                        addCustomLensLayout.setVisibility(View.INVISIBLE);
                        openLensSettingsView();
                        if (isHapticFeedbackEnabled) {
                            buzz(v);
                        }
                    }
                });

        final TextView saveLensesButton = (TextView) findViewById(R.id.saveLenses);
        saveLensesButton
                .setOnClickListener(new LenseSelectionSaveClickListener());

        // this prevents no lenses from being selected
        _lensListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long id) {
                System.out.println("checked Item count: " + _lensListView.getCheckedItemCount());
                if (_lensListView.getCheckedItemCount() > 0) {
                    saveLensesButton.setEnabled(true);
                } else {
                    saveLensesButton.setEnabled(false);
                }
            }
        });

        // arrow_next / prev lens
        _nextLensButton.setOnClickListener(nextLensClickListener);
        _nextLensButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                nextClickBoolean.setDown(true);
                mUiHandler.post(nextLensRunnable);
                if (isHapticFeedbackEnabled) {
                    buzz(_lensFocalLengthText);
                }
                return true;
            }
        });
        _prevLensButton.setOnClickListener(previousLensClickListener);
        _prevLensButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                prevClickBoolean.setDown(true);
                mUiHandler.post(previousLensRunnable);
                if (isHapticFeedbackEnabled) {
                    buzz(_lensFocalLengthText);
                }
                return true;
            }
        });

        // view under the focal length label in the preview
        ((ConstraintLayout) findViewById(R.id.focalLengthLensButton))
                .setOnClickListener(new focalLengthLensButtonViewClickListener());

        // fullscreen button
        ((ImageView) findViewById(R.id.fullscreenButton))
                .setOnClickListener(fullscreenViewClickListener);

        // save picture buttons
        ((Button) findViewById(R.id.savePictureCancelButton))
                .setOnClickListener(new CancelSavePictureClickListener());
        ((Button) findViewById(R.id.save_edit_details_PictureCancelButton))
                .setOnClickListener(new CancelSavePictureClickListener());
        ((Button) findViewById(R.id.savePictureAndEditButton))
                .setOnClickListener(new EditPictureDetailsClickListener());
        ((Button) findViewById(R.id.savePictureButton))
                .setOnClickListener(new SavePictureClickListener());

        ((Button) findViewById(R.id.savePictureDetails))
                .setOnClickListener(new SavePictureEditDetailsClickListener());

        mCameraAngleDetailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_artemisMath.isHAngleMode())
                    _artemisMath.setHAngleMode(false);
                else
                    _artemisMath.setHAngleMode(true);
                v.postInvalidate();
            }
        });

        ((ImageView) findViewById(R.id.camera_settings_back))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

        ((ImageView) findViewById(R.id.lens_settings_back))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int displayed = _lensSettingsFlipper
                                .getDisplayedChild();
                        if (displayed > 0 && displayed < 3
                                && !wasFocalLengthButtonPressed) {
                            _lensSettingsFlipper.setInAnimation(
                                    ArtemisActivity.this, R.anim.slide_in_left);
                            _lensSettingsFlipper.setOutAnimation(
                                    ArtemisActivity.this,
                                    R.anim.slide_out_right);
                            addCustomLensLayout.setVisibility(View.INVISIBLE);

                            _lensSettingsFlipper.showPrevious();
                        } else if (_lensSettingsFlipper.getDisplayedChild() == 0
                                || wasFocalLengthButtonPressed) {
                            wasFocalLengthButtonPressed = false;
                            openArtemisCameraPreviewView();
                        } else if (displayed == 3) {
                            // Custom zoom lens, return to first page
                            wasFocalLengthButtonPressed = false;
                            _lensSettingsFlipper.setInAnimation(
                                    ArtemisActivity.this, R.anim.slide_in_left);
                            _lensSettingsFlipper.setOutAnimation(
                                    ArtemisActivity.this,
                                    R.anim.slide_out_right);
                            _lensSettingsFlipper.setDisplayedChild(0);
                        } else if (displayed == 4) {
                            _lensSettingsFlipper.setInAnimation(
                                    ArtemisActivity.this, R.anim.slide_in_left);
                            _lensSettingsFlipper.setOutAnimation(
                                    ArtemisActivity.this,
                                    R.anim.slide_out_right);
                            addCustomLensLayout.setVisibility(View.INVISIBLE);

                            _lensSettingsFlipper.setDisplayedChild(2);
                        } else if (displayed == 5) {
                            _lensSettingsFlipper.setInAnimation(
                                    ArtemisActivity.this, R.anim.slide_in_left);
                            _lensSettingsFlipper.setOutAnimation(
                                    ArtemisActivity.this,
                                    R.anim.slide_out_right);
                            addCustomLensLayout.setVisibility(View.INVISIBLE);

                            _lensSettingsFlipper.showPrevious();
                        }
                    }
                });

        ((ImageView) findViewById(R.id.framelines_back)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openArtemisCameraPreviewView();
                currentViewId = R.id.framelineListView;
            }
        });

        findViewById(R.id.menuSettings).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openArtemisSettings();
                        if (isHapticFeedbackEnabled) {
                            buzz(v);
                        }
                    }
                });

        ((SearchView) findViewById(R.id.cameraSearch))
                .setOnQueryTextListener(new CameraSearchQueryTextListener());

        // Custom cam related
        // findViewById(R.id.save_custom_camera).setOnClickListener(
        // addCustomCameraClickListener);
        findViewById(R.id.save_custom_camera_active_sensor).setOnClickListener(
                addCustomCameraClickListener);
        findViewById(R.id.start_calib_custom_camera).setOnClickListener(
                startCustomCameraCalibration);
        findViewById(R.id.customCamera_active_sensor).setOnClickListener(
                startCustomCameraActiveSensorSize);

        findViewById(R.id.addCustomLensImage).setOnClickListener(
                addCustomLensClickListener);
        findViewById(R.id.addCustomLens).setOnClickListener(
                addCustomLensClickListener);

        // Gallery button
        findViewById(R.id.galleryButton).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();
                        if (isHapticFeedbackEnabled) {
                            buzz(v);
                        }
                    }
                });

        findViewById(R.id.menuMetadata).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isEditingPictureDetailsOnly = true; // indicate we
                        // aren't in the
                        // process of taking
                        // a picture

                        loadPictureDetailsSettings();

                        viewFlipper.setDisplayedChild(3);
                        savePictureViewFlipper.setDisplayedChild(1);
                        if (isHapticFeedbackEnabled) {
                            buzz(v);
                        }
                    }
                });

<<<<<<< HEAD
        addCustomlensAdapterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCustomLensAdapterDialog();
            }
        });

        removeLensAdapterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ArtemisActivity.this.selectedLensAdapter(null);
            }
        });

        Glide.with(this)
                .load(R.raw.loading)
                .into(loadingIndicator);

        _lensMakeText.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                setLensMakeTextAnimation();
            }
        });

        ((ImageView) findViewById(R.id.menu_back))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

        ((LinearLayout) findViewById(R.id.menuLensAdapters)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                addLensAdapterView.setVisibility(View.VISIBLE);
                mainMenu.setVisibility(View.GONE);
                horizontalGuidelineBottom.setGuidelinePercent(0.85f);

=======
        addLensAdapterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addLensAdapterView.setVisibility(View.VISIBLE);
                mainMenu.setVisibility(View.GONE);
                horizontalGuidelineBottom.setGuidelinePercent(0.85f);
>>>>>>> ed0b9bd (Look and feel changes)
                refreshLensAdapters();
            }
        });

<<<<<<< HEAD
        ((LinearLayout) findViewById(R.id.menuVideoMode)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                if (recordVideoButton.getVisibility() == View.GONE) {
                    recordVideoButton.setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.shutterButton)).setVisibility(View.GONE);
                    recordVideoChronometerContainer.setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.imgVideoMode)).setImageDrawable(getDrawable(R.drawable.stillsmode));
                    ((TextView) findViewById(R.id.txtVideoMode)).setText(getString(R.string.stills_mode));
                } else {
                    recordVideoButton.setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.shutterButton)).setVisibility(View.VISIBLE);
                    recordVideoChronometerContainer.setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.imgVideoMode)).setImageDrawable(getDrawable(R.drawable.videomode));
                    ((TextView) findViewById(R.id.txtVideoMode)).setText(getString(R.string.video_mode));
                }
            }
        });

<<<<<<< HEAD
        ((LinearLayout) findViewById(R.id.menuFramelines)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openFramelinesListView();
            }
        });

=======
        ((LinearLayout) findViewById(R.id.menuLooks)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

                Intent looksIntent = new Intent(getBaseContext(), LooksActivity.class);
                startActivity(looksIntent);
            }
        });

        ((LinearLayout) findViewById(R.id.menuVirtualStandIn)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

                findViewById(R.id.editVirtualStandInMenu).setVisibility(View.VISIBLE);
                mainMenu.setVisibility(View.GONE);
                TouchController.isEditing = true;

                Intent virtualStandInIntent = new Intent(getBaseContext(), VirtualStandInActivity.class);
                startActivityForResult(virtualStandInIntent, CHOOSE_MODEL_REQUEST);
            }
        });

        ((ImageButton) findViewById(R.id.acceptStandIn)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.editVirtualStandInMenu).setVisibility(View.GONE);
                mainMenu.setVisibility(View.VISIBLE);
                TouchController.isEditing = false;
                scene.clearSelectedObject();
                glSurfaceView.getModelRenderer().takeSnapshot = true;
            }
        });

        ((ImageView) findViewById(R.id.standInColorButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object3DData selected = scene.getSelectedObject();
                if(selected != null) {
                    findViewById(R.id.colorPickerModel).setVisibility(View.VISIBLE);
                    ColorPickerView colorPickerView = findViewById(R.id.colorPickerModelView);
                    colorPickerView.setColorListener(new ColorEnvelopeListener() {
                        @Override
                        public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                            Log.i("MYTAG", "ENVELOP COLOR: " + envelope.getHexCode());
                            AlphaTileView alphaTileView = findViewById(R.id.alphaTileModelView);
                            alphaTileView.setPaintColor(envelope.getColor());
                        }
                    });
                    hideGlView();
                }
            }
        });

        findViewById(R.id.colorPickerModelBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.colorPickerModel).setVisibility(View.GONE);
                showGlView();
            }
        });
        findViewById(R.id.colorPickerModelDoneButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerView colorPickerView = findViewById(R.id.colorPickerModelView);
                int color = colorPickerView.getColor();
                Object3DData selected = scene.getSelectedObject();
                if(selected != null) {
                    float red = Color.red(color);
                    float green = Color.green(color);
                    float blue = Color.blue(color);
                    selected.setColor(new float[] {red / 255, green / 255, blue / 255, 1});
                }
                findViewById(R.id.colorPickerModel).setVisibility(View.GONE);
                showGlView();
            }
        });

        ((ImageView) findViewById(R.id.addStandInButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.editVirtualStandInMenu).setVisibility(View.VISIBLE);
                mainMenu.setVisibility(View.GONE);

                Intent virtualStandInIntent = new Intent(getBaseContext(), VirtualStandInActivity.class);
                startActivityForResult(virtualStandInIntent, CHOOSE_MODEL_REQUEST);
            }
        });

        ((ImageView) findViewById(R.id.helpStandInButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), VirtualStandInsOnboardingActivity.class);
                startActivity(intent);
            }
        });

        ((ImageView) findViewById(R.id.refreshStandInPosicionAndScaleButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scene.resetSelectedObject();
            }
        });

        ((ImageView) findViewById(R.id.deleteStandInButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int remainingObjects = scene.removeSelectedObject();
                if(remainingObjects == 0) {
                    ((RelativeLayout) findViewById(R.id.openGlContainer)).removeAllViews();
                    glSurfaceView = null;
                    mainMenu.setVisibility(View.VISIBLE);
                    findViewById(R.id.editVirtualStandInMenu).setVisibility(View.GONE);
                    scene = null;
                    File modelFile = ImageUtils.getModelFile();
                    if(modelFile.exists()){
                        modelFile.delete();
                    }
                }
            }
        });


>>>>>>> 449fcf5 (Add looks interface. Apply look to stills and video mode. Delete looks)
        menuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenu();
            }
        });
<<<<<<< HEAD
=======
        addCustomlensAdapterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCustomLensAdapterDialog();
            }
        });

        removeLensAdapterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ArtemisActivity.this.selectedLensAdapter(null);
            }
        });

        Glide.with(this)
                .load(R.raw.loading)
                .into(loadingIndicator);
>>>>>>> ed0b9bd (Look and feel changes)
=======

        addExtenderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedExtender == null) {
                    openLensExtenderManufacturerView();
                } else {
                    removeLensExtender();
                }
            }
        });

        addFramelineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Frameline frameline = buildDefaultFrameline();
                showFramelineMenu(frameline);
            }
        });

        findViewById(R.id.framelineBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFramelineMenu();
                saveCurrentFramelineToDb(mCameraOverlay.currentFrameline);
                mCameraOverlay.currentFrameline = null;
                openFramelinesListView();
                appliedFramelines = _artemisDBHelper.getAppliedFramelines();
                mCameraOverlay.invalidate();
            }
        });

        findViewById(R.id.framelineSubmenuBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.framelineTopBack).setVisibility(View.GONE);
                findViewById(R.id.framelineRateMenu).setVisibility(View.GONE);

                findViewById(R.id.framelineTopMenu).setVisibility(View.VISIBLE);
                findViewById(R.id.framelineMenu).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.framelineRateButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFramelineRateMenu();
            }
        });

        findViewById(R.id.framelineScaleButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFramelineScaleMenu();
            }
        });


        findViewById(R.id.framelineScaleBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.framelineScaleTop).setVisibility(View.GONE);
                findViewById(R.id.framelineScaleMenu).setVisibility(View.GONE);

                findViewById(R.id.framelineTopMenu).setVisibility(View.VISIBLE);
                findViewById(R.id.framelineMenu).setVisibility(View.VISIBLE);
            }
        });

        ((SeekBar) findViewById(R.id.scaleBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCameraOverlay.currentFrameline.setScale(i);
                mCameraOverlay.invalidate();
                ((TextView) findViewById(R.id.scale100Button)).setText(String.valueOf(i) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.framelineStyle1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setFramelineType(1);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.framelineStyle2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setFramelineType(3);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.framelineStyle3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setFramelineType(2);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.framelineStyle4).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setFramelineType(4);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.framelineStyle5).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setFramelineType(5);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.framelineStyle6).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setFramelineType(6);
                mCameraOverlay.invalidate();
            }
        });

        findViewById(R.id.shading0percent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setShading(0);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.shading25percent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setShading(1);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.shading50percent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setShading(2);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.shading75percent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setShading(3);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.shading100percent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setShading(4);
                mCameraOverlay.invalidate();
            }
        });

        findViewById(R.id.framelineOffsetCenterVerticalButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setVerticalOffset(0);
                mCameraOverlay.invalidate();
                ((TextView) findViewById(R.id.txtFramelineHorizontalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getHorizontalOffset()));
                ((TextView) findViewById(R.id.txtFramelineVerticalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getVerticalOffset()));
            }
        });
        findViewById(R.id.framelineOffsetCenterHorizontalButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setHorizontalOffset(0);
                mCameraOverlay.invalidate();
                ((TextView) findViewById(R.id.txtFramelineHorizontalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getHorizontalOffset()));
                ((TextView) findViewById(R.id.txtFramelineVerticalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getVerticalOffset()));
            }
        });
        findViewById(R.id.framelineOffsetCenterButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setHorizontalOffset(0);
                mCameraOverlay.currentFrameline.setVerticalOffset(0);
                mCameraOverlay.invalidate();
                ((TextView) findViewById(R.id.txtFramelineHorizontalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getHorizontalOffset()));
                ((TextView) findViewById(R.id.txtFramelineVerticalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getVerticalOffset()));
            }
        });

        findViewById(R.id.framelineOffsetTopButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousVerticalOffset = mCameraOverlay.currentFrameline.getVerticalOffset();
                mCameraOverlay.currentFrameline.setVerticalOffset(previousVerticalOffset + 1);
                mCameraOverlay.invalidate();
                ((TextView) findViewById(R.id.txtFramelineHorizontalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getHorizontalOffset()));
                ((TextView) findViewById(R.id.txtFramelineVerticalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getVerticalOffset()));
            }
        });

        findViewById(R.id.framelineOffsetBottomButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousVerticalOffset = mCameraOverlay.currentFrameline.getVerticalOffset();
                mCameraOverlay.currentFrameline.setVerticalOffset(previousVerticalOffset - 1);
                mCameraOverlay.invalidate();
                ((TextView) findViewById(R.id.txtFramelineHorizontalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getHorizontalOffset()));
                ((TextView) findViewById(R.id.txtFramelineVerticalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getVerticalOffset()));
            }
        });

        findViewById(R.id.framelineOffsetRightButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousHorizontalOffset = mCameraOverlay.currentFrameline.getHorizontalOffset();
                mCameraOverlay.currentFrameline.setHorizontalOffset(previousHorizontalOffset - 1);
                mCameraOverlay.invalidate();
                ((TextView) findViewById(R.id.txtFramelineHorizontalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getHorizontalOffset()));
                ((TextView) findViewById(R.id.txtFramelineVerticalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getVerticalOffset()));
            }
        });
        findViewById(R.id.framelineOffsetLeftButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousHorizontalOffset = mCameraOverlay.currentFrameline.getHorizontalOffset();
                mCameraOverlay.currentFrameline.setHorizontalOffset(previousHorizontalOffset + 1);
                mCameraOverlay.invalidate();
                ((TextView) findViewById(R.id.txtFramelineHorizontalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getHorizontalOffset()));
                ((TextView) findViewById(R.id.txtFramelineVerticalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getVerticalOffset()));
            }
        });


        findViewById(R.id.framelineShadingButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFramelineShadingMenu();
            }
        });

        findViewById(R.id.framelineShadingBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.framelineShadingTop).setVisibility(View.GONE);
                findViewById(R.id.framelineShadingMenu).setVisibility(View.GONE);

                findViewById(R.id.framelineTopMenu).setVisibility(View.VISIBLE);
                findViewById(R.id.framelineMenu).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.framelineOffsetButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFramelineOffsetMenu();
            }
        });

        findViewById(R.id.framelineOffsetBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.framelineOffsetTop).setVisibility(View.GONE);
                findViewById(R.id.framelineOffsetMenu).setVisibility(View.GONE);

                findViewById(R.id.framelineTopMenu).setVisibility(View.VISIBLE);
                findViewById(R.id.framelineMenu).setVisibility(View.VISIBLE);

                findViewById(R.id.framelineOffsetTopButtonContainer).setVisibility(View.GONE);
                findViewById(R.id.framelineOffsetBottomButtonContainer).setVisibility(View.GONE);
                findViewById(R.id.framelineOffsetLeftButtonContainer).setVisibility(View.GONE);
                findViewById(R.id.framelineOffsetRightButtonContainer).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.framelineStyleButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFramelineStyleMenu();
            }
        });

        findViewById(R.id.framelineStyleBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.framelineStyleTop).setVisibility(View.GONE);
                findViewById(R.id.framelineStyleMenu).setVisibility(View.GONE);

                findViewById(R.id.framelineTopMenu).setVisibility(View.VISIBLE);
                findViewById(R.id.framelineMenu).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.framelineLineWidthButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFramelineLineWidthMenu();
            }
        });

        findViewById(R.id.framelineLineWidthBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.framelineLineWidthTop).setVisibility(View.GONE);
                findViewById(R.id.framelineLineWidthMenu).setVisibility(View.GONE);

                findViewById(R.id.framelineTopMenu).setVisibility(View.VISIBLE);
                findViewById(R.id.framelineMenu).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.framelineCenterMarkerStyleButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFramelineCenterMarkerStyleMenu();
            }
        });

        findViewById(R.id.framelineSetLineStyleButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setDotted(!mCameraOverlay.currentFrameline.isDotted());
                mCameraOverlay.invalidate();
            }
        });

        findViewById(R.id.framelineCenterMarkerStyleBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.framelineCenterMarkerStyleTop).setVisibility(View.GONE);
                findViewById(R.id.framelineCenterMarkerStyleMenu).setVisibility(View.GONE);

                findViewById(R.id.framelineTopMenu).setVisibility(View.VISIBLE);
                findViewById(R.id.framelineMenu).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.lineWidth1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setLineWidth(1);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.lineWidth2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setLineWidth(2);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.lineWidth3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setLineWidth(3);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.lineWidth4).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setLineWidth(4);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.lineWidth5).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setLineWidth(5);
                mCameraOverlay.invalidate();
            }
        });

        findViewById(R.id.framelineLineColorButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPickerDialog();
                hideGlView();
            }
        });

        findViewById(R.id.framelineLineColorDoneButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.colorPickerMenu).setVisibility(View.INVISIBLE);
                ColorPickerView colorPicker = findViewById(R.id.colorPickerView);
                mCameraOverlay.currentFrameline.setColor(colorPicker.getColor());
                mCameraOverlay.invalidate();
                showGlView();
            }
        });

        findViewById(R.id.colorPickerBackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.colorPickerMenu).setVisibility(View.INVISIBLE);
                showGlView();
            }
        });


        findViewById(R.id.addCustomFramelineRateButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddCustomFramelineRateDialog();
            }
        });

        findViewById(R.id.centerMarkerStyle0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerType(0);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.centerMarkerStyle1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerType(1);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.centerMarkerStyle2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerType(2);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.centerMarkerStyle3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerType(3);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.centerMarkerStyle4).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerType(4);
                mCameraOverlay.invalidate();
            }
        });

        findViewById(R.id.centerMarkerWidth1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerLineWidth(1);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.centerMarkerWidth2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerLineWidth(2);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.centerMarkerWidth3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerLineWidth(3);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.centerMarkerWidth4).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerLineWidth(4);
                mCameraOverlay.invalidate();
            }
        });
        findViewById(R.id.centerMarkerWidth5).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraOverlay.currentFrameline.setCenterMarkerLineWidth(5);
                mCameraOverlay.invalidate();
            }
        });
<<<<<<< HEAD:app/src/main/java/com/chemicalwedding/artemis/ArtemisActivity.java

<<<<<<< HEAD:app/src/main/java/com/chemicalwedding/artemis/ArtemisActivity.java
>>>>>>> cf3855e (add lens extender and framelines)
=======
        setup3DModels();
>>>>>>> 99abbaa (vstand-ins):artemis-android/src/com/chemicalwedding/artemis/ArtemisActivity.java
=======
>>>>>>> db76629 (add color picker for virtual stand ins):artemis-android/src/com/chemicalwedding/artemis/ArtemisActivity.java
    }

    public void setup3DModels(String modelFileName) {
        try {
            if(glSurfaceView == null) { // First model added
                modelUri = Uri.parse("assets://" + getPackageName() + "/models/" + modelFileName);
                handler = new Handler(getMainLooper());
                scene = new SceneLoader(this);
                scene.init();

                glSurfaceView = new ModelGLSurfaceView(this);
                glSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
                ((RelativeLayout) findViewById(R.id.openGlContainer)).addView(glSurfaceView);
            } else {
                modelUri = Uri.parse("assets://" + getPackageName() + "/models/" + modelFileName);
                scene.addModel();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }


    public Uri getParamUri() {
        return modelUri;
    }

    public int getParamType() {
        return 2;
    }

    public ModelGLSurfaceView getGLView() {
        return glSurfaceView;
    }

    public SceneLoader getScene() {
        return scene;
    }

    public static Bitmap modelBitmap;
    public void generated(Bitmap bitmap) {
        // TODO - add support for save openGlGenerated Bitmap
        modelBitmap = bitmap;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                //create a file to write bitmap data
                String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File f = new File(absolutePath, "test_objecb.png");
                try {
                    f.createNewFile();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showAddCustomLensAdapterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ArtemisActivity.this);
        builder.setTitle("Add custom lens adapter");
        final EditText input = new EditText(this);
<<<<<<< HEAD
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
=======
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
>>>>>>> ed0b9bd (Look and feel changes)
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double factor = Double.parseDouble(input.getText().toString());
                addCustomAdapterToDatabase(factor);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void showDeleteFramelineDialog(Frameline frameline) {
        AlertDialog dialog = new AlertDialog.Builder(
                ArtemisActivity.this)
                .setMessage(R.string.sure_you_want_to_delete_frameline)
                .setTitle(R.string.delete_frameline)
                .setPositiveButton(R.string.delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                _artemisDBHelper.deleteFrameline(frameline);
                                appliedFramelines = _artemisDBHelper.getAppliedFramelines();
                                mCameraOverlay.invalidate();
                                List<Frameline> framelines = _artemisDBHelper.getFramelines();
                                ListView framelinesList = (ListView) findViewById(R.id.framelinesList);
                                if (framelines.size() > 0) {
                                    FramelinesAdapter adapter = new FramelinesAdapter(ArtemisActivity.this, framelines, ArtemisActivity.this);
                                    framelinesList.setAdapter(adapter);
                                } else {
                                    framelinesList.setAdapter(null);
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        }).create();
        dialog.show();
    }

    private void addCustomAdapterToDatabase(double adapterFactor) {
        LensAdapter adapter = new LensAdapter();
        adapter.setMagnificationFactor(adapterFactor);
        adapter.setCustomAdapter(true);
        _artemisDBHelper.insertLensAdapters(adapter);
        refreshLensAdapters();
    }

    private void refreshLensAdapters() {
        List<LensAdapter> adapters = _artemisDBHelper.getLensAdapters();

        RecyclerView recyclerView = findViewById(R.id.adaptersRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ArtemisActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new LensAdaptersAdapter(adapters, ArtemisActivity.this, ArtemisActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    public void hideLensAdapterViewAndShowMainMenu() {
        addLensAdapterView.setVisibility(View.GONE);
        mainMenu.setVisibility(View.VISIBLE);
        horizontalGuidelineBottom.setGuidelinePercent(0.899f);
    }

    protected void buzz(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                        | HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
    }

    private void initDatabase() {
        // Open Database
        if (_artemisDBHelper == null) {
            _artemisDBHelper = new ArtemisDatabaseHelper(this);

            // load initialization data for the first camera selection
            // ((ArtemisApplication) getApplication())
            // .postOnWorkerThread(new Runnable() {
            // @Override
            // public void run() {
            _allCameraFormats = _artemisDBHelper.getCameraFormats();
//            _allCameraGenres = _artemisDBHelper.getCameraGenres();
            _allCameraFormats.add(getString(R.string.custom_cameras));
        }
        // }
        // });
    }

    protected void initPreferences() {
        SharedPreferences artemisPrefs = getApplication().getSharedPreferences(
                ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);

        String lookId = artemisPrefs.getString(
                getString(R.string.preference_key_selectedCameraEffect), "0");

        Look selectedLook = _artemisDBHelper.getLook(Integer.valueOf(lookId));
        Integer effectId;
        if (selectedLook == null) {
            // In previous versions lookId was effectId
            effectId = Integer.parseInt(lookId);
        } else {
            effectId = selectedLook.getEffectId();
        }
        if (effectId == CONTROL_EFFECT_MODE_OFF) {
            looksButton.setVisibility(View.INVISIBLE);
        } else {
            looksButton.setVisibility(View.VISIBLE);
        }

        // check if gps is enabled
        gpsEnabled = artemisPrefs.getBoolean(ArtemisPreferences.GPS_ENABLED,
                true);

        ArtemisFileUtils.Companion.setExternalDir(artemisPrefs.getString(ArtemisPreferences.SAVE_PICTURE_FOLDER, ""));

//		// lock box setting
        CameraOverlay.lockBoxEnabled = artemisPrefs.getBoolean(
                ArtemisPreferences.LOCK_BOXES_ENABLED, true);

        // quick shot save setting
        CameraPreview21.quickshotEnabled = artemisPrefs.getBoolean(
                ArtemisPreferences.QUICKSHOT_ENABLED, false);

        // smooth image filter
        CameraPreview21.smoothImagesEnabled = artemisPrefs.getBoolean(
                ArtemisPreferences.SMOOTH_IMAGE_ENABLED, true);

        // autofocus camera before picture
        CameraPreview21.autoFocusBeforePictureTake = artemisPrefs.getBoolean(
                ArtemisPreferences.AUTO_FOCUS_ON_PICTURE, false);

        // black and white camera preview
        CameraPreview21.blackAndWhitePreview = artemisPrefs.getBoolean(
                getString(R.string.preference_key_previewblackwhite), false);

        ArtemisActivity.headingDisplaySelection = artemisPrefs.getInt(
                ArtemisPreferences.HEADING_DISPLAY, 2);

        isHapticFeedbackEnabled = artemisPrefs.getBoolean(
                getString(R.string.preference_key_hapticfeedback), true);

        CameraPreview21.savedImageJPEGQuality = Integer.parseInt(artemisPrefs
                .getString(
                        getString(R.string.preference_key_savedImageQuality),
                        getString(R.string.image_quality_default)));
        String savedImageSize = artemisPrefs.getString(
                getString(R.string.preference_key_savedImageSize), "-1");
        CameraPreview21.savedImageSizeIndex = Integer.parseInt(savedImageSize);

        backPressedMode = artemisPrefs.getString(
                getString(R.string.preference_key_backkeyOption),
                getString(R.string.backkey_default));

        String longPressShutterMode = artemisPrefs.getString(
                getString(R.string.preference_key_longpressshutter),
                getString(R.string.longPressShutter_default));

        autoFocusAfterLongClickShutter = false;
        takePictureAfterAutoFocusAndLongClickShutter = false;
        takePictureAfterReleaseLongClickShutter = false;

        if (!CameraPreview21.autoFocusBeforePictureTake) {
            if ("autofocusAndCapture".equals(longPressShutterMode)) {
                Log.v(TAG, "init long press autofocus and capture");
                autoFocusAfterLongClickShutter = true;
                takePictureAfterAutoFocusAndLongClickShutter = true;
                takePictureAfterReleaseLongClickShutter = false;
            } else if ("autofocusAndCaptureOnRelease"
                    .equals(longPressShutterMode)) {
                Log.v(TAG, "init long press autofocus and capture on release");
                autoFocusAfterLongClickShutter = true;
                takePictureAfterAutoFocusAndLongClickShutter = false;
                takePictureAfterReleaseLongClickShutter = true;
            } else if ("autofocus".equals(longPressShutterMode)) {
                Log.v(TAG, "init long press only autofocus");
                autoFocusAfterLongClickShutter = true;
                takePictureAfterAutoFocusAndLongClickShutter = false;
                takePictureAfterReleaseLongClickShutter = false;
            }
        }

        takePictureButton = (ImageView) findViewById(R.id.shutterButton);
        reconfigureShutterButton();

        if (artemisPrefs.getBoolean(
                getString(R.string.preference_key_mapVolumeKeys), true)) {

            String volumeUpMode = artemisPrefs
                    .getString(
                            getString(R.string.preference_key_volumeUpAction),
                            CameraPreview21.isAutoFocusSupported ? getString(R.string.volumeUpAction_default)
                                    : getString(R.string.volumeUpAction_noautofocus_default));

            if ("autofocusAndCapture".equals(volumeUpMode)) {
                volumeUpAutoFocusAndPicture = true;
            } else if ("takePicture".equals(volumeUpMode)) {
                volumeUpPicture = true;
            } else if ("autofocus".equals(volumeUpMode)) {
                volumeUpAutoFocus = true;
            }

            String volumeDownMode = artemisPrefs.getString(
                    getString(R.string.preference_key_volumeDownAction),
                    getString(R.string.volumeDownAction_default));

            if ("autofocusAndCapture".equals(volumeDownMode)) {
                volumeDownAutoFocusAndPicture = true;
            } else if ("takePicture".equals(volumeDownMode)) {
                volumeDownPicture = true;
            } else if ("autofocus".equals(volumeDownMode)) {
                volumeDownAutoFocus = true;
            }

        }
    }

    protected void initCameraAndLensSelection() {
        SharedPreferences artemisPrefs = getApplication().getSharedPreferences(
                ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);

        // Retrieve the previously selected camera and lenses
        int selectedCameraRowId = artemisPrefs.getInt(
                ArtemisPreferences.SELECTED_CAMERA_ROW, Integer.MAX_VALUE);
        String lensMake = artemisPrefs.getString(
                ArtemisPreferences.SELECTED_LENS_MAKE, DEFAULT_LENS_MAKE);
        int selectedLensIndex = artemisPrefs.getInt(ArtemisPreferences.SELECTED_LENS_INDEX, -1);
        int selectedZoomLensPK = artemisPrefs.getInt(
                getString(R.string.preference_key_selectedzoomlens), -1);

        if (selectedCameraRowId == Integer.MAX_VALUE) {
            selectedCameraRowId = _artemisDBHelper.findDefaultCameraID();
        }

        if (selectedCameraRowId > 0) {
            // Id is above 0, this is a normal Camera from the Camera's table
            setSelectedCamera(selectedCameraRowId, false, false);
        } else {
            // Set custom camera
            CustomCamera selectedCustomCamera = _artemisDBHelper
                    .getCustomCameraDetailsForRowId(-selectedCameraRowId);

            Log.v(TAG, String.format("Custom cam loaded on start: %s",
                    selectedCustomCamera));

            _selectedCamera = new Camera(selectedCustomCamera);
            tempSelectedCamera = _selectedCamera;
            _artemisMath.setSelectedCamera(_selectedCamera);
//            lensMake = DEFAULT_LENS_MAKE;
            _cameraDetailsText.setText(selectedCustomCamera.getName() + " "
                    + _selectedCamera.getRatio());
            String text = selectedCustomCamera.getName() + " " + _selectedCamera.getRatio();
            setCameraDetailsTextAnimation();
        }

        if (selectedZoomLensPK < 0) {
            setSelectedLensMake(lensMake, false, false);

            String selectedLensesRowIds = artemisPrefs.getString(
                    ArtemisPreferences.SELECTED_LENS_ROW_CSV, null);
            if (selectedLensesRowIds == null) {
                selectedLensesRowIds = "";
                for (Lens lens : tempLensesForMake) {
                    if ("1".equals(lens.getLensSet())) {
                        selectedLensesRowIds += lens.getPk() + ",";
                    }
                }
                if (selectedLensesRowIds.length() > 0) {
                    selectedLensesRowIds = selectedLensesRowIds.substring(0, selectedLensesRowIds.length() - 1);
                }
            }
            Log.d(TAG, "Setting selected lens rowids: " + selectedLensesRowIds);
            setSelectedLenses(selectedLensesRowIds, true, false);
            _artemisMath.selectFirstMeaningFullLens();
            if (selectedLensIndex >= 0 && selectedLensIndex < _selectedLenses.size()) {
                setSelectedLensIndex(selectedLensIndex);
            }
        } else {
            setSelectedZoomLens(
                    _artemisDBHelper.getZoomLens(selectedZoomLensPK), false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Open the menu when the menu button is pressed
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSearchRequested() {
        // Do nothing when search button is pressed
        return false;
    }

    ;

    @Override
    public void onBackPressed() {
        switch (currentViewId) {
            case R.id.cameraSettings:
                int currentDisplayedChild = _cameraSettingsFlipper
                        .getDisplayedChild();
                if (currentDisplayedChild > 0 && currentDisplayedChild != NUM_CAMERA_PAGES) {
                    // If we aren't at the first page (or third), go back to the
                    // previous page
                    _cameraSettingsFlipper.setInAnimation(this,
                            R.anim.slide_in_left);
                    _cameraSettingsFlipper.setOutAnimation(this,
                            R.anim.slide_out_right);
                    _cameraSettingsFlipper
                            .setDisplayedChild(currentDisplayedChild - 1);
                } else if (currentDisplayedChild == NUM_CAMERA_PAGES) {
                    // Custom camera page, go back to the first page
                    _cameraSettingsFlipper.setInAnimation(this,
                            R.anim.slide_in_left);
                    _cameraSettingsFlipper.setOutAnimation(this,
                            R.anim.slide_out_right);

                    findViewById(R.id.cameraSearch).setVisibility(View.VISIBLE);

                    _cameraSettingsFlipper.setDisplayedChild(0);
                } else {
                    // otherwise, at the first page. go back to main preview
                    openArtemisCameraPreviewView();
                }
                break;
            case R.id.lensSettings:
                currentDisplayedChild = _lensSettingsFlipper.getDisplayedChild();
                if (currentDisplayedChild > 0 && !wasFocalLengthButtonPressed) {
                    // If we aren't at the first page, go back to the previous page
                    if (tempSelectedCamera != null
                            && tempSelectedCamera.getRowid() != null
                            && tempSelectedCamera.getRowid() == -1) {
                        // This is a custom camera
                        break;
                    }

                    addCustomLensLayout.setVisibility(View.INVISIBLE);

                    _lensSettingsFlipper.setInAnimation(this, R.anim.slide_in_left);
                    _lensSettingsFlipper.setOutAnimation(this,
                            R.anim.slide_out_right);
                    if (_lensSettingsFlipper.getDisplayedChild() == 2) {
                        // On zoom lens page, go back to first page
                        _lensSettingsFlipper.setDisplayedChild(0);
                    } else {
                        // normally just go back a page
                        _lensSettingsFlipper
                                .setDisplayedChild(currentDisplayedChild - 1);
                    }
                } else {
                    // otherwise, at the first page. go back to main preview
                    openArtemisCameraPreviewView();
                }
                break;

            /*
             * Lens settings back is handled both here and in the lens back button
             * click listener
             */

            case R.id.savePictureViewFlipper:
                openArtemisCameraPreviewView();
                break;
            case R.id.artemisMenu:
                openArtemisCameraPreviewView();
                break;
            case R.id.artemisPreview:
            default:
                if (!isEditingPictureDetailsOnly) {
                    // we want to either quit, or present the quit dialog, or do
                    // nothing
                    if ("prompt".equals(backPressedMode)) {
                        // Present alert dialog
                        AlertDialog dialog = new AlertDialog.Builder(
                                ArtemisActivity.this)
                                .setMessage(R.string.sure_you_want_to_exit)
                                .setTitle(R.string.exit_artemis)
                                .setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                _artemisMath
                                                        .setInitializedFirstTime(false);
                                                finish();
                                            }
                                        })
                                .setNegativeButton(R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        dialog.show();
                    } else if ("nothing".equals(backPressedMode)) {
                        // Do nothing
                    } else {
                        // finish
                        finish();
                    }

                } else
                    openArtemisCameraPreviewView();
                break;
        }
    }

    private void openMenu() {
        if (_cameraSettingsFlipper == null)
            _cameraSettingsFlipper = (ViewFlipper) findViewById(R.id.camera_settings_flipper);

        _cameraSettingsFlipper.setInAnimation(null);
        _cameraSettingsFlipper.setOutAnimation(null);
        _cameraSettingsFlipper.setDisplayedChild(0);

        viewFlipper.setDisplayedChild(4);
        currentViewId = R.id.artemisMenu;
    }

    private void openCameraSettingsView() {
        if (_cameraSettingsFlipper == null)
            _cameraSettingsFlipper = (ViewFlipper) findViewById(R.id.camera_settings_flipper);
        _cameraSettingsFlipper.setInAnimation(null);
        _cameraSettingsFlipper.setOutAnimation(null);
        _cameraSettingsFlipper.setDisplayedChild(0);

        // Clear any search query
        ((SearchView) findViewById(R.id.cameraSearch)).setQuery("", false);
        ((SearchView) findViewById(R.id.cameraSearch))
                .setVisibility(View.VISIBLE);

        viewFlipper.setDisplayedChild(1);
        currentViewId = R.id.cameraSettings;

        ListView cameraFormatList = (ListView) findViewById(R.id.cameraFormatList);
//        ArrayAdapter<String> formatAdapter = new ArrayAdapter<String>(this,
//                R.layout.text_list_item, _allCameraFormats);
        StringOptionArrayAdapter adapter = new StringOptionArrayAdapter(ArtemisActivity.this, _allCameraFormats, cameraFormatList);
        cameraFormatList.setAdapter(adapter);
        cameraFormatList.setTextFilterEnabled(true);

        cameraFormatList
                .setOnItemClickListener(new CameraFormatItemClickedListener());
        cameraFormatList.requestFocus();
    }

    private void openLensSettingsView() {

        _lensSettingsFlipper.setInAnimation(null);
        _lensSettingsFlipper.setDisplayedChild(0);
        viewFlipper.setDisplayedChild(2);
        currentViewId = R.id.lensSettings;
        addCustomLensLayout.setVisibility(View.INVISIBLE);

        ArrayList<String> lensManufacturers = _artemisDBHelper
                .getLensManufacturers();
//        if (tempSelectedCamera != null && tempSelectedCamera.getRowid() != null
//                && tempSelectedCamera.getRowid() == -1) {
//            // This is a custom camera
//            lensManufacturers = new ArrayList<String>();
//            lensManufacturers.add(DEFAULT_LENS_MAKE);
//        } else {
//            // Regular db camera
//            lensManufacturers = _artemisDBHelper
//                    .getLensManufacturers();
//        }
        lensManufacturers.add(getString(R.string.custom_zoom_lens));
        Log.i(TAG, "Lens manufacturers available: " + lensManufacturers.size());

        ListView lensManufacturerList = (ListView) findViewById(R.id.lensManufacturerList);
        StringOptionArrayAdapter adapter = new StringOptionArrayAdapter(this, lensManufacturers, lensManufacturerList);
        lensManufacturerList.setAdapter(adapter);
//        lensManufacturerList.setTextFilterEnabled(true);

        lensManufacturerList
                .setOnItemClickListener(new LensManufacturerItemClickedListener());
    }

    private void openLensExtenderManufacturerView() {
        _lensSettingsFlipper.setInAnimation(ArtemisActivity.this,
                R.anim.slide_in_right);
        _lensSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                R.anim.slide_out_left);
        _lensSettingsFlipper.setDisplayedChild(4); // 4 = R.id.lensExtenderManufacturerList - lens_settings.xml

        ArrayList<String> lensManufacturers = _artemisDBHelper
                .getExtenderManufacturers();
        ListView extenderManufacturersList = (ListView) findViewById(R.id.lensExtenderManufacturerList);
        StringOptionArrayAdapter adapter = new StringOptionArrayAdapter(this, lensManufacturers, extenderManufacturersList);
        extenderManufacturersList.setAdapter(adapter);

        extenderManufacturersList.setOnItemClickListener(new ExtenderManufacturerItemClickedListener());
    }

    private void openFramelinesListView() {
        Log.i("MYTAG", "openFramelinesListView");
        viewFlipper.setDisplayedChild(5);
        List<Frameline> framelines = _artemisDBHelper.getFramelines();
        ListView framelinesList = (ListView) findViewById(R.id.framelinesList);
        if (framelines.size() > 0) {
            FramelinesAdapter adapter = new FramelinesAdapter(this, framelines, this);
            framelinesList.setAdapter(adapter);
        } else {
            framelinesList.setAdapter(null);
        }
    }

    private void openLensExtenderView(String selectedManufacturer) {
        _lensSettingsFlipper.setInAnimation(ArtemisActivity.this,
                R.anim.slide_in_right);
        _lensSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                R.anim.slide_out_left);
        _lensSettingsFlipper.setDisplayedChild(5); // 5 = R.id.lensExtenderView - lens_settings.xml

        ArrayList<Extender> extenders = _artemisDBHelper.getExtenderForManufacturer(selectedManufacturer);


        ListView extenderList = (ListView) findViewById(R.id.lensExtenderList);
        ExtenderAdapter extenderAdapter = new ExtenderAdapter(ArtemisActivity.this, extenders, ArtemisActivity.this);

        addCustomLensLayout.setVisibility(View.VISIBLE);
        extenderList.setAdapter(extenderAdapter);
        extenderList.setOnItemClickListener(new ExtenderItemClickedListener());
    }

    private void removeLensExtender() {
        Log.i("MYTAG", "removing lens extender");
        selectedExtender = null;
        applySelectedLenses();
        setLensMakeTextAnimation();
        addExtenderButton.setText(getString(R.string.addExtender));
    }

    private void openArtemisCameraPreviewView() {
        viewFlipper.setInAnimation(null);
        viewFlipper.setDisplayedChild(0);
        currentViewId = R.id.artemisPreview;
    }

    private void openGallery() {
        // New gallery here
        Intent galleryIntent = new Intent(getBaseContext(), GalleryActivity.class);
        startActivity(galleryIntent);
        //getLoaderManager().initLoader(GALLERY_IMAGE_LOADER, null, this);
    }

    private void toastNoImageFound() {
        final Toast toast = Toast.makeText(ArtemisActivity.this,
                getString(R.string.gallery_no_images_found), Toast.LENGTH_LONG);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    @SuppressLint("DefaultLocale")
    public static String getBucketId(String bucketName) {
        bucketName = bucketName.toLowerCase();
        if (bucketName.charAt(bucketName.length() - 1) == '/') {
            bucketName = bucketName.substring(0, bucketName.length() - 1);
        }
        String bucketId = Integer.toString(bucketName.hashCode());
        Log.i(TAG, "Gallery Intent Bucket ID: " + bucketId);
        return bucketId;
    }

<<<<<<< HEAD
    @Override
    public void recordingStarted() {
        recordVideoChronometer.setBase(SystemClock.elapsedRealtime());
        recordVideoChronometer.start();

        isRecordingVideo = true;
        recordVideoButton.setImageResource(R.drawable.recording_video);
        deconfigureShutterButton();
    }

    @Override
    public void recordingStopped(String filePath, HashMap<String, String> cameraMetadata) {
        CropVideoOptions options = new CropVideoOptions();
        options.filePath = filePath;
        options.cameraMetadata = cameraMetadata;

        new CropVideoFileTask().execute(options);
    }

    @Override
    public void applyFrameline(Frameline frameline) {
        Log.i("MYTAG", "applyFrameline " + frameline.getTitle());
        frameline.setApplied(true);
        _artemisDBHelper.saveFrameline(frameline);
        appliedFramelines = _artemisDBHelper.getAppliedFramelines();
        mCameraOverlay.invalidate();
    }

    @Override
    public void removeFrameline(Frameline frameline) {
        Log.i("MYTAG", "removeFrameline " + frameline.getTitle());
        frameline.setApplied(false);
        _artemisDBHelper.saveFrameline(frameline);
        appliedFramelines = _artemisDBHelper.getAppliedFramelines();
        mCameraOverlay.invalidate();
    }

    @Override
    public void deleteFrameline(Frameline frameline) {
        Log.i("MYTAG", "deleteFrameline " + frameline.getTitle());
        showDeleteFramelineDialog(frameline);
    }

    @Override
    public void editFrameline(Frameline frameline) {
        Log.i("MYTAG", "editFrameline " + frameline.getTitle());
        showFramelineMenu(frameline);
    }

    @Override
    public void applyExtender(Extender extender) {
        Log.i("MYTAG", "applyExtender " + extender.toString());
        selectedExtender = extender;
        applySelectedLenses();
        setLensMakeTextAnimation();
        addExtenderButton.setText(getString(R.string.remove_extender));
    }

    public void applySelectedLenses() {
        wasFocalLengthButtonPressed = false;
        _lensSettingsFlipper.setOutAnimation(null);
        SparseBooleanArray checkedValuesArray = _lensListView
                .getCheckedItemPositions();
        _selectedLenses = new ArrayList<Lens>();
        String selectedLensString = "";
        for (int listIndex = 0; listIndex < checkedValuesArray.size(); listIndex++) {
            if (checkedValuesArray.valueAt(listIndex)) { // if it's checked
                int lensMakeIndex = checkedValuesArray.keyAt(listIndex);
                Lens lens = tempLensesForMake.get(lensMakeIndex);
                if (listIndex > 0) {
                    selectedLensString += ",";
                }
                selectedLensString += lens.getPk();
                _selectedLenses.add(lens);
            }
        }
        Log.i(TAG, "SELECTED LENSES: " + selectedLensString);

        if (tempSelectedCamera.getRowid() != -1) {
            setSelectedCamera(tempSelectedCamera.getRowid(), true, false);
        }
        setSelectedLensMake(tempSelectedLensMake, true, false);
        setSelectedLenses(selectedLensString, true, true);
        updateLensesInDB();
        _artemisMath.setFullscreen(false);
        _artemisMath.selectFirstMeaningFullLens();
        _artemisMath.calculateLargestLens();
        _artemisMath.calculateRectBoxesAndLabelsForLenses();
        _artemisMath.resetTouchToCenter(); // now with green box
        _artemisMath.onFullscreenOffSelectLens();
        mCameraPreview.calculateZoom(true);
        mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
        mCameraAngleDetailView.postInvalidate();
        reconfigureNextAndPreviousLensButtons();
        openArtemisCameraPreviewView();
    }

    private class CropVideoOptions {
        String filePath;
        HashMap<String, String> cameraMetadata;
    }
<<<<<<< HEAD

    private class CropVideoFileTask extends AsyncTask<CropVideoOptions, Void, Void> {

        @Override
        protected Void doInBackground(CropVideoOptions... cropVideoOptions) {
            String filePath = cropVideoOptions[0].filePath;
            HashMap<String, String> cameraMetadata = cropVideoOptions[0].cameraMetadata;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recordVideoChronometer.setBase(SystemClock.elapsedRealtime());
                    recordVideoChronometer.stop();
                    recordVideoChronometer.setText("00:00");
                    recordVideoButton.setImageResource(R.drawable.video_icon);
                }
            });

            isRecordingVideo = false;
=======

    private class CropVideoFileTask extends AsyncTask<CropVideoOptions, Void, Void> {

        @Override
        protected Void doInBackground(CropVideoOptions... cropVideoOptions) {
            String filePath = cropVideoOptions[0].filePath;
            HashMap<String, String> cameraMetadata = cropVideoOptions[0].cameraMetadata;
            recordVideoChronometerContainer.setVisibility(View.INVISIBLE);
            recordVideoChronometer.setBase(SystemClock.elapsedRealtime());
            recordVideoChronometer.stop();

            isRecordingVideo = false;
            recordVideoButton.setImageResource(R.drawable.video_icon);
>>>>>>> ed0b9bd (Look and feel changes)
            MediaType mediaType = MediaType.VIDEO;
            File file = new File(filePath);
            MediaFile mediaFile = new MediaFile(file.getName(), file.getAbsolutePath(), new Date(file.lastModified()), mediaType);

            videoFileName = mediaFile.getPath();
            String videoFileNameCropped = videoFileName.substring(0, videoFileName.lastIndexOf("."));
            String formatString = videoFileName.substring(videoFileName.lastIndexOf("."));
            videoFileNameCropped = videoFileNameCropped + "_preview" + formatString;

            ArtemisRectF selectedLensBox = _artemisMath.getSelectedLensBox();

            final int videoWidth = mCameraPreview.videoSize.getWidth();
            final int videoHeight = mCameraPreview.videoSize.getHeight();

<<<<<<< HEAD
            final float screenWRatio = (float) _artemisMath.screenWidth / _artemisMath.screenHeight;
            final float screenHRatio = (float) 1 / screenWRatio;

            int newVideoHeight = (int) (videoWidth * screenHRatio);
            int newVideoWidth = (int) (videoWidth);
            int newVideoWidthDiff = (int) ((videoWidth - newVideoWidth) / 2f);
            int newVideoHeightDiff = (int) ((videoHeight - newVideoHeight) / 2f);

            String cropVideoInputToScreen = newVideoWidth + ":" + newVideoHeight + ":" + newVideoWidthDiff + ":" + newVideoHeightDiff;
            String scaleScreenSize = mCameraOverlay.getWidth() + ":" + mCameraOverlay.getHeight();
            String cropVideoToSelectedBox = selectedLensBox.width() + ":" + selectedLensBox.height() + ":" + selectedLensBox.left + ":" + selectedLensBox.top;

            String[] cmd = {"-y", "-i", videoFileName,
                    "-filter:v",
                    "crop=" + cropVideoInputToScreen +
                            ",scale=" + scaleScreenSize +
                            ",crop=" + cropVideoToSelectedBox +
                            ",setsar=1:1, fps=fps=25",
                    "-c:a", "copy", videoFileNameCropped};
            int rc = FFmpeg.execute(cmd);

            Bitmap framelinesBitmap = Bitmap.createBitmap((int) selectedLensBox.width(), (int) selectedLensBox.height(), Bitmap.Config.ARGB_8888);

            List<Frameline> appliedFramelines = ArtemisActivity.appliedFramelines;

            Canvas c = new Canvas();
            c.setBitmap(framelinesBitmap);
            if (appliedFramelines != null) {
                for (Frameline frameline : appliedFramelines) {
                    RectF rect = new RectF();
                    rect.top = 0;
                    rect.bottom = c.getHeight();
                    rect.left = 0;
                    rect.right = c.getWidth();
                    double rate = frameline.getRate().getRate();
                    int framelineScale = frameline.getScale();
                    int verticalOffsetPercentage = frameline.getVerticalOffset();
                    int horizontalOffsetPercentage = frameline.getHorizontalOffset();
                    int stroke = frameline.getLineWidth();
                    boolean dottedLine = frameline.isDotted();
                    int color = frameline.getColor();
                    int framelineType = frameline.getFramelineType();
                    int centerMarkerType = frameline.getCenterMarkerType();
                    int centerMarkerLineWidth = frameline.getCenterMarkerLineWidth();
                    int shadingColorId = frameline.getShading() == 0 ? R.color.shading_0
                            : frameline.getShading() == 1 ? R.color.shadin_25
                            : frameline.getShading() == 2 ? R.color.shadin_50
                            : frameline.getShading() == 3 ? R.color.shadin_75
                            : R.color.shadin_100;
                    int backgroundColor = getResources().getColor(shadingColorId);
                    CameraOverlay.drawFrameline(c, rect, framelineScale, verticalOffsetPercentage, horizontalOffsetPercentage, stroke, dottedLine, color, framelineType, centerMarkerType, centerMarkerLineWidth, backgroundColor);
                }
=======
            try {
                Bitmap framelinesBitmap = FramelineDrawingUtils
                        .createBitmapFromFramelines((int) selectedLensBox.width(), (int)selectedLensBox.height(), ArtemisActivity.this);
                File framelinesPng = ImageUtils.saveBitmapAsTemporalPng(getApplicationContext(), framelinesBitmap, "frameline.png");
                file = VideoUtils.watermarkVideo(getApplicationContext(), file, framelinesPng);
            } catch (IOException ex) {
                ex.printStackTrace();
>>>>>>> 5e1520f (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
            }
            saveBitmapAsJPEG(framelinesBitmap);

<<<<<<< HEAD:app/src/main/java/com/chemicalwedding/artemis/ArtemisActivity.java
            if (rc == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "Command execution completed successfully.");
                if (file.delete()) {
                    file = new File(videoFileNameCropped);
                    mediaFile = new MediaFile(file.getName(), file.getAbsolutePath(), new Date(file.lastModified()), mediaType);
=======
            try {
                File modelFile = ImageUtils.getModelFile();
                if(modelFile != null && modelFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(modelFile.getPath());
                    Bitmap bitmap3DModel = Bitmap.createScaledBitmap(bitmap, (int) selectedLensBox.width(), (int) selectedLensBox.height(), true);
<<<<<<< HEAD
<<<<<<< HEAD
                    modelFile = ImageUtils.saveBitmapAsTemporalPng(bitmap3DModel);
=======
                    modelFile = ImageUtils.saveBitmapAsTemporalPng(getApplicationContext(), bitmap3DModel, "scaled-model.png");
>>>>>>> 5e1520f (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
=======
                    modelFile = ImageUtils.saveBitmapAsTemporalPng(getApplicationContext(), bitmap3DModel, "scaled-model.png");
>>>>>>> d36402c (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
                    if(modelFile.exists()) {
                        file = VideoUtils.watermarkVideo(file, modelFile);
                    }
>>>>>>> db76629 (add color picker for virtual stand ins):artemis-android/src/com/chemicalwedding/artemis/ArtemisActivity.java
                }
            } else if (rc == RETURN_CODE_CANCEL) {
                Log.i(Config.TAG, "Command execution cancelled by user.");
            } else {
                Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
                Config.printLastCommandOutput(Log.INFO);
            }


            String root = Environment.getExternalStorageDirectory().toString();
            String frameilneFileName = root + "/Artemis/Project-1/frameline.png";
            String videoFileNameFramelined = videoFileName.substring(0, videoFileName.lastIndexOf("."));
            String formatStringFramelined = videoFileName.substring(videoFileName.lastIndexOf("."));
            videoFileNameFramelined = videoFileNameFramelined + "_framelined" + formatString;

//            String[] complexCommand = {"ffmpeg","-y" ,"-i",
//                    videoFileNameCropped,"-strict","experimental", "-vf", "movie="+ frameilneFileName +" [watermark]; [in][watermark] overlay=main_w-overlay_w-10:10 [out]","-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050", videoFileNameFramelined};
            String[] complexCommand = {"-i", videoFileNameCropped, "-i", frameilneFileName, "-filter_complex",
                    "overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2", "-c:a", "copy", videoFileNameFramelined};
            int result = FFmpeg.execute(complexCommand);

            if (result == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "Command execution completed successfully.");
                file = new File(videoFileNameCropped);
                if (file.delete()) {
                    file = new File(videoFileNameFramelined);
                    mediaFile = new MediaFile(file.getName(), file.getAbsolutePath(), new Date(file.lastModified()), mediaType);
                }
            } else {
                Log.i(Config.TAG, "command framelines error");
            }
            File tempFramelineImage = new File(frameilneFileName);
            tempFramelineImage.delete();

            String root2 = Environment.getExternalStorageDirectory().toString();
            String frameilneFileName2 = root + "/test_objecb.png";
            String videoFileNameFramelined2 = videoFileName.substring(0, videoFileName.lastIndexOf("."));
            String formatStringFramelined2 = videoFileName.substring(videoFileName.lastIndexOf("."));
            videoFileNameFramelined2 = videoFileNameFramelined2 + "_framelined2" + formatString;

//            String[] complexCommand = {"ffmpeg","-y" ,"-i",
//                    videoFileNameCropped,"-strict","experimental", "-vf", "movie="+ frameilneFileName +" [watermark]; [in][watermark] overlay=main_w-overlay_w-10:10 [out]","-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050", videoFileNameFramelined};
            String[] complexCommand2 = {"-i", videoFileNameFramelined, "-i", frameilneFileName2, "-filter_complex",
                    "overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2", "-c:a", "copy", videoFileNameFramelined2};
            int result2 = FFmpeg.execute(complexCommand2);

            if (result2 == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "Command execution completed successfully.");
                file = new File(videoFileNameFramelined);
                if (file.delete()) {
                    file = new File(videoFileNameFramelined2);
                    mediaFile = new MediaFile(file.getName(), file.getAbsolutePath(), new Date(file.lastModified()), mediaType);
                }
            } else {
                Log.i(Config.TAG, "command framelines error");
            }
            File tempFramelineImage2 = new File(frameilneFileName);
            tempFramelineImage2.delete();

            SharedPreferences artemisPrefs = getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);
            String selectedSaveMetadataToMoviesString = artemisPrefs.getString(
                    getString(R.string.preference_key_saveMetadataToMovies), "ASK");
            Log.i("bixlabs", selectedSaveMetadataToMoviesString);
            SaveMetadataToMoviesOptions saveMetadataToMoviesOptions = SaveMetadataToMoviesOptions.valueOf(selectedSaveMetadataToMoviesString);
<<<<<<< HEAD

            if (saveMetadataToMoviesOptions == SaveMetadataToMoviesOptions.NEVER) {
                try {
                    MetadataEditor editor = MetadataEditor.createFrom(new File(mediaFile.getPath()));
                    Map<String, MetaValue> meta = editor.getKeyedMeta();

=======

            if (saveMetadataToMoviesOptions == SaveMetadataToMoviesOptions.NEVER) {
                try {
                    MetadataEditor editor = MetadataEditor.createFrom(new File(mediaFile.getPath()));
                    Map<String, MetaValue> meta = editor.getKeyedMeta();

<<<<<<< HEAD
>>>>>>> ed0b9bd (Look and feel changes)
                    for(String key: cameraMetadata.keySet()) {
=======
                    for (String key : cameraMetadata.keySet()) {
>>>>>>> f78dc0f (Configure Kotlin)
                        meta.put(key, MetaValue.createString(cameraMetadata.get(key)));
                    }

                    editor.save(false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArtemisActivity.this, "Video saved!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("fullScreenMediaPath", mediaFile.getPath());
                bundle.putString("fullScreenMediaType", mediaFile.getMediaType().toString());
                bundle.putSerializable("metadata", cameraMetadata);

                if (saveMetadataToMoviesOptions == SaveMetadataToMoviesOptions.ASK) {
                    Intent mediaFulllScreenIntent = new Intent(ArtemisActivity.this, SaveVideoActivity.class);
                    mediaFulllScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    mediaFulllScreenIntent.putExtras(bundle);
                    reconfigureShutterButton();
                    startActivity(mediaFulllScreenIntent);
                } else if (saveMetadataToMoviesOptions == SaveMetadataToMoviesOptions.ALWAYS) {
                    Intent videoMetadataIntent = new Intent(ArtemisActivity.this, SaveVideoMetadataActivity.class);
                    videoMetadataIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    videoMetadataIntent.putExtras(bundle);
                    startActivity(videoMetadataIntent);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadingIndicatorContainer.setVisibility(View.GONE);
            showGlView();
        }
    }

    public void saveBitmapAsJPEG(Bitmap bm) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Artemis/Project-1");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "frameline" + ".png";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deconfigureShutterButton() {
        takePictureButton.setImageResource(R.drawable.camera_icon);
        takePictureButton.setOnClickListener(null);
        takePictureButton.setOnLongClickListener(null);
    }

    public void reconfigureShutterButton() {
        // shutter release (take picture) button
        takePictureButton.setImageResource(R.drawable.camerabutton);
        takePictureButton.setOnClickListener(takePictureClickListener);
        if (autoFocusAfterLongClickShutter) {
            takePictureButton.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    pictureSaveHeadingTiltString = headingTiltText.getText()
                            .toString();

                    mCameraPreview
                            .autofocusCamera(takePictureAfterAutoFocusAndLongClickShutter);
                    if (autoFocusAfterLongClickShutter
                            && !takePictureAfterReleaseLongClickShutter) {
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    @Override
    public void selectedLensAdapter(LensAdapter adapter) {
        if (adapter == null) {
            selectedLensAdapter.setVisibility(View.GONE);
<<<<<<< HEAD
            ArtemisMath.lensAdapterFactor = 1;
            mCameraPreview.calculateZoom(true);
            mCameraAngleDetailView.postInvalidate();
            _lensFocalLengthText.setTextColor(getColor(R.color.orangeArtemisText));
            lensFocalLengthMM.setTextColor(getColor(R.color.orangeArtemisText));
        } else {
            selectedLensAdapter.setText("x" + adapter.getMagnificationFactor());
            ArtemisMath.lensAdapterFactor = adapter.getMagnificationFactor().floatValue();
            selectedLensAdapter.setVisibility(View.VISIBLE);
            mCameraPreview.calculateZoom(true);
            mCameraAngleDetailView.postInvalidate();
            _lensFocalLengthText.setTextColor(Color.RED);
            lensFocalLengthMM.setTextColor(Color.RED);
=======
        } else {
            selectedLensAdapter.setText("x" + adapter.getMagnificationFactor());
            selectedLensAdapter.setVisibility(View.VISIBLE);
>>>>>>> ed0b9bd (Look and feel changes)
        }
    }

    @Override
    public void deleteLensAdapter(LensAdapter adapter) {
        if (adapter.isCustomAdapter()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ArtemisActivity.this);
<<<<<<< HEAD
            builder.setTitle("Delete custom adapter");
            builder.setMessage("Are you sure you want to delete the custom lens adapter?");
=======
            String title = getString(R.string.delete_custom_adapter_title);
            String message = getString(R.string.delete_custom_adapter_message);
            builder.setTitle(title);
            builder.setMessage(message);
>>>>>>> ed0b9bd (Look and feel changes)

            builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _artemisDBHelper.deleteLensAdapterById(adapter.getPk());
                    refreshLensAdapters();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            builder.show();
        }
    }

    final class CameraGenreItemClickedListener implements OnItemClickListener {
=======
    final class CameraFormatItemClickedListener implements OnItemClickListener {
>>>>>>> 4c62fd4 (3.0.5.1 uploaded to the app store for fix remembering lens selections)

        @Override
        public void onItemClick(AdapterView<?> adapterView, View selectedItem,
                                int index, long arg3) {
            TextView selectedTextView = ((RelativeLayout) selectedItem).findViewById(R.id.text);
            mSelectedFormat = selectedTextView.getText().toString();

            if (index == adapterView.getCount() - 1) {
                // Custom cameras was selected...
                if (customCameras == null) {
                    customCameras = _artemisDBHelper.getCustomCameras();
                    CustomCamera addnew = new CustomCamera();
                    addnew.setName(getString(R.string.custom_camera_new_camera));
                    customCameras.add(addnew);
                    ListView customCameraList = (ListView) findViewById(R.id.customCameraList);
//                    ArrayAdapter<CustomCamera> adapter = new ArrayAdapter<CustomCamera>(
//                            ArtemisActivity.this, R.layout.text_list_item,
//                            customCameras);
                    CustomCameraAdapter adapter = new CustomCameraAdapter(ArtemisActivity.this, customCameras, customCameraList);
                    customCameraList.setAdapter(adapter);
                    customCameraList
                            .setOnItemClickListener(new CustomCameraClickListener());
                    registerForContextMenu(customCameraList);
                }
                findViewById(R.id.cameraSearch).setVisibility(View.INVISIBLE);

                _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                        R.anim.slide_in_right);
                _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                        R.anim.slide_out_left);
                // set the displayed child to the last page ( == num pages)
                _cameraSettingsFlipper.setDisplayedChild(NUM_CAMERA_PAGES);
                return;
            }

            Log.i(TAG, "Camera format selected: " + mSelectedFormat);
            ArrayList<String> manufacturerListForCameraFormat = _artemisDBHelper
                    .getCameraManufacturersForFormat(mSelectedFormat);
            Log.i(TAG, "Manufacturers available: " + manufacturerListForCameraFormat.size());

            ListView cameraManufacturersList = (ListView) findViewById(R.id.cameraManufacturerList);
//            ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(
//                    ArtemisActivity.this, R.layout.text_list_item,
//                    manufacturerListForCameraFormat);
            StringOptionArrayAdapter adapter = new StringOptionArrayAdapter(ArtemisActivity.this, manufacturerListForCameraFormat, cameraManufacturersList);
            cameraManufacturersList.setAdapter(adapter);
            cameraManufacturersList.setTextFilterEnabled(true);
            cameraManufacturersList
                    .setOnItemClickListener(new CameraManufacturerItemClickedListener());
            _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                    R.anim.slide_in_right);
            _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                    R.anim.slide_out_left);

            _cameraSettingsFlipper.showNext();
        }

    }

    private OnClickListener startCustomCameraCalibration = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent customCam = new Intent(ArtemisActivity.this,
                    CustomCameraCalibrationActivity.class);

            String ratio = ((EditText) findViewById(R.id.custom_camera_aspectratio))
                    .getText().toString();
            try {
                float ratioFloat = Float.parseFloat(ratio);
                customCam.putExtra("ratio", ratioFloat);
            } catch (NumberFormatException e) {

            }

            String focallength = ((EditText) findViewById(R.id.custom_camera_focallength))
                    .getText().toString();
            try {
                float focalLengthFloat = Float.parseFloat(focallength);
                customCam.putExtra("focalLength", focalLengthFloat);
            } catch (NumberFormatException p) {

            }

//            String squeeze = ((EditText) findViewById(R.id.custom_camera_squeezeratio))
//                    .getText().toString();
//            try {
//                float squeezeFloat = Float.parseFloat(squeeze);
//                customCam.putExtra("squeeze", squeezeFloat);
//            } catch (NumberFormatException p) {
//
//            }

            CharSequence name = ((EditText) findViewById(R.id.custom_camera_name))
                    .getText().toString();
            if (name.length() > 0) {
                customCam.putExtra("name", name);
            }

            startActivityForResult(customCam, 100);
        }
    };

    private OnClickListener startCustomCameraActiveSensorSize = new OnClickListener() {
        @Override
        public void onClick(View v) {
            _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                    R.anim.slide_in_right);
            _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                    R.anim.slide_out_left);
            _cameraSettingsFlipper.showNext();
        }
    };

    private final OnItemClickListener zoomLensListItemClicked = new OnItemClickListener() {

        AlertDialog addZoomLensDialog;

        @Override
        public void onItemClick(AdapterView<?> adapterView, View v,
                                int selected, long id) {

            if (selected == adapterView.getCount() - 1) {
                if (addZoomLensDialog == null) {
                    addZoomLensDialog = new AlertDialog.Builder(
                            ArtemisActivity.this)
                            .setView(
                                    View.inflate(ArtemisActivity.this,
                                            R.layout.create_zoom_lens_dialog,
                                            null))
                            .setTitle(R.string.add_new_zoom_lens)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                        }
                                    }).create();
                    addZoomLensDialog.setButton(Dialog.BUTTON_POSITIVE,
                            getString(R.string.add),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    ZoomLens lens = new ZoomLens();
                                    String name = ((EditText) addZoomLensDialog
                                            .findViewById(R.id.zoomLensName))
                                            .getText().toString();
                                    if (name.isEmpty()) {
                                        name = getString(R.string.untitled_zoom_lens);
                                    }

                                    String min = ((EditText) addZoomLensDialog
                                            .findViewById(R.id.zoomLensMinFL))
                                            .getText().toString();
                                    String max = ((EditText) addZoomLensDialog
                                            .findViewById(R.id.zoomLensMaxFL))
                                            .getText().toString();

                                    Float minFL = null;
                                    try {
                                        minFL = Float.parseFloat(min);
                                    } catch (NumberFormatException e) {
                                    }
                                    Float maxFL = null;
                                    try {
                                        maxFL = Float.parseFloat(max);
                                    } catch (NumberFormatException e) {
                                    }

                                    if (minFL == null || maxFL == null
                                            || minFL <= 0 || maxFL <= 0
                                            || minFL >= maxFL) {
                                        new AlertDialog.Builder(
                                                ArtemisActivity.this)
                                                .setTitle(
                                                        R.string.custom_zoom_lens_error)
                                                .setMessage(
                                                        R.string.custom_zoom_lens_error_message)
                                                .setPositiveButton(
                                                        R.string.ok,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {
                                                                addZoomLensDialog
                                                                        .show();
                                                            }
                                                        }).create().show();

                                    } else {
                                        lens.setName(name);
                                        lens.setMinFL(minFL);
                                        lens.setMaxFL(maxFL);
                                        lens.setPk((int) _artemisDBHelper
                                                .addZoomLens(lens));
                                        zoomLenses.add(zoomLenses.size() - 1,
                                                lens);
                                        ((ArrayAdapter<?>) ((ListView) findViewById(R.id.customZoomLensList))
                                                .getAdapter())
                                                .notifyDataSetChanged();
                                    }
                                }
                            });
                }
                addZoomLensDialog.show();
            } else {
                // Select zoom lens
                if (tempSelectedCamera.getRowid() != -1) {
                    setSelectedCamera(tempSelectedCamera.getRowid(), true,
                            false);
                }
                setSelectedZoomLens(zoomLenses.get(selected), true);
                _lensSettingsFlipper.setInAnimation(null);
                _lensSettingsFlipper.setOutAnimation(null);
                _lensSettingsFlipper.setDisplayedChild(0);
                mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
                mCameraAngleDetailView.postInvalidate();
                openArtemisCameraPreviewView();
            }
        }
    };

    public void setSelectedZoomLens(final ZoomLens zoomLens,
                                    boolean savePreference) {
        _artemisMath.setSelectedLenses(new ArrayList<Lens>());
        _artemisMath.selectedZoomLens = zoomLens;
        _artemisMath.get_currentLensBoxes().clear();
        _artemisMath.setSelectedLens(null);

        String extenderString = ArtemisActivity.selectedExtender == null ? "" : (" + " + ArtemisActivity.selectedExtender.getModel());
        ((TextView) findViewById(R.id.lensMakeText))
                .setText(_artemisMath.selectedZoomLens.toString() + extenderString);
        setLensMakeTextAnimation();
        _artemisMath.resetTouchToCenter();
        _artemisMath.calculateZoomLenses();
        _artemisMath.calculateRectBoxesAndLabelsForLenses();
        _artemisMath.selectFirstMeaningFullLens();
        _artemisMath.onFullscreenOffSelectLens();
        mCameraPreview.calculateZoom(true);
        reconfigureNextAndPreviousLensButtons();

        if (savePreference) {
            Editor appPrefsEditor = getApplication().getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
                    .edit();
            appPrefsEditor.putInt(
                    getString(R.string.preference_key_selectedzoomlens),
                    zoomLens.getPk());
            appPrefsEditor.commit();
        }

    }

    final class CameraManufacturerItemClickedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View selectedItem,
                                int arg2, long arg3) {
            TextView selectedTextView = ((RelativeLayout) selectedItem).findViewById(R.id.text);
            String selectedManufacturer = selectedTextView.getText().toString();
            Log.i(TAG, "Camera manufacturer selected: " + selectedManufacturer);
            List<String> cameraSensors = _artemisDBHelper
                    .getCameraSensorsForManufacturer(selectedManufacturer);
            Log.i(TAG, "sensors available: " + cameraSensors.size());

            ListView cameraSensorList = (ListView) findViewById(R.id.cameraSensorList);
//            ArrayAdapter<String> sensorAdaptor = new ArrayAdapter<String>(
//                    ArtemisActivity.this, R.layout.text_list_item, cameraSensors);
            StringOptionArrayAdapter adapter = new StringOptionArrayAdapter(ArtemisActivity.this, cameraSensors, cameraSensorList);
            cameraSensorList.setAdapter(adapter);
            cameraSensorList.setTextFilterEnabled(true);
            cameraSensorList
                    .setOnItemClickListener(new CameraSensorItemClickedListener());

            _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                    R.anim.slide_in_right);
            _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                    R.anim.slide_out_left);

            _cameraSettingsFlipper.showNext();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    _cameraSettingsFlipper.getWindowToken(), 0);
            _cameraSettingsFlipper.requestFocus();
        }
    }

    final class CameraSensorItemClickedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View selectedItem,
                                int arg2, long arg3) {
            TextView selectedTextView = ((RelativeLayout) selectedItem).findViewById(R.id.text);
            String selectedSensor = selectedTextView.getText().toString();
            Log.i(TAG, String.format("Camera sensor selected: %s  format %s ", selectedSensor, mSelectedFormat));
            // _currentCameraSensor = selectedSensor;
            _ratiosListForCamera = _artemisDBHelper
                    .getCameraRatiosForSensor(mSelectedFormat, selectedSensor);
            Log.i(TAG, "Ratios available: " + _ratiosListForCamera.size());

            // Add the ratio names to a list and bind to the list view
            ArrayList<String> ratioNames = new ArrayList<String>();
            for (Pair<Integer, String> pair : _ratiosListForCamera) {
                ratioNames.add(pair.second);
            }
            ListView cameraRatioList = (ListView) findViewById(R.id.cameraRatiosList);
//            ArrayAdapter<String> ratioAdapter = new ArrayAdapter<String>(
//                    ArtemisActivity.this, R.layout.text_list_item, ratioNames);
            StringOptionArrayAdapter adapter = new StringOptionArrayAdapter(ArtemisActivity.this, ratioNames, cameraRatioList);
            cameraRatioList.setAdapter(adapter);
            cameraRatioList.setTextFilterEnabled(true);
            cameraRatioList
                    .setOnItemClickListener(new CameraRatioItemClickedListener());

            _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                    R.anim.slide_in_right);
            _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                    R.anim.slide_out_left);

            _cameraSettingsFlipper.showNext();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    _cameraSettingsFlipper.getWindowToken(), 0);
            _cameraSettingsFlipper.requestFocus();
        }
    }

    final class CameraRatioItemClickedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View selectedItem,
                                int selectedIndex, long arg3) {
            // TextView selectedTextView = (TextView) selectedItem;
            // String selectedRatio = selectedTextView.getText().toString();
            // Log.i(_logTag, "Camera ratio selected: " + selectedRatio);
            // _selectedCameraRatioIndex = selectedIndex;
            int rowid = _ratiosListForCamera.get(selectedIndex).first;
            // tempCameraRowId = rowid;
            setSelectedCamera(rowid, false, true);

            addCustomLensLayout.setVisibility(View.INVISIBLE);

            openLensSettingsView();
        }
    }

    final class CameraSearchQueryTextListener implements OnQueryTextListener {

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.isEmpty() && _cameraSettingsFlipper != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        _cameraSettingsFlipper.getWindowToken(), 0);
                _cameraSettingsFlipper.requestFocus();

                _cameraSettingsFlipper.setInAnimation(null);
                _cameraSettingsFlipper.setOutAnimation(null);
                _cameraSettingsFlipper.setDisplayedChild(0);

                return true;
            }

            ArrayList<String> sensorListForCamera = _artemisDBHelper
                    .findSensorsForQuery(newText);

            ListView cameraSensorList = (ListView) findViewById(R.id.cameraSensorList);
//            ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(
//                    ArtemisActivity.this, R.layout.text_list_item,
//                    sensorListForCamera);
            StringOptionArrayAdapter adapter = new StringOptionArrayAdapter(ArtemisActivity.this, sensorListForCamera, cameraSensorList);
            cameraSensorList.setAdapter(adapter);
            cameraSensorList.setTextFilterEnabled(true);
            cameraSensorList
                    .setOnItemClickListener(new CameraSensorItemClickedListener());

            if (_cameraSettingsFlipper != null) {
                _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                        R.anim.slide_in_right);
                _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                        R.anim.slide_out_left);

                if (_cameraSettingsFlipper.getDisplayedChild() != 1) {
                    _cameraSettingsFlipper.setInAnimation(null);
                    _cameraSettingsFlipper.setOutAnimation(null);
                    _cameraSettingsFlipper.setDisplayedChild(1);
                }
            }
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    _cameraSettingsFlipper.getWindowToken(), 0);
            _cameraSettingsFlipper.requestFocus();
            return true;
        }

    }

    final class ExtenderManufacturerItemClickedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("MYTAG", "extender manufacturer item clicked ");
            if (i < adapterView.getCount() - 1) {
                TextView selectedTextView = (TextView) ((RelativeLayout) view).findViewById(R.id.text);
                String selectedManufacturer = selectedTextView.getText().toString();
                openLensExtenderView(selectedManufacturer);
            }
        }
    }

    final class ExtenderItemClickedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("MYTAG", "extender item selected");
        }
    }

    final class LensManufacturerItemClickedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View selectedItem,
                                int index, long id) {

            if (index < adapterView.getCount() - 1) {

                TextView selectedTextView = (TextView) ((RelativeLayout) selectedItem).findViewById(R.id.text);
                String selectedManufacturer = selectedTextView.getText().toString();


                ArrayList<String> lensMakes = _artemisDBHelper.getLensMakeForLensManufacturer(selectedManufacturer);

//            if (tempSelectedCamera != null && tempSelectedCamera.getRowid() != null
//                    && tempSelectedCamera.getRowid() == -1) {
//                // This is a custom camera
//                lensMakes = new ArrayList<String>();
//                lensMakes.add(DEFAULT_LENS_MAKE);
//            } else {
//                // Regular db camera
//                lensMakes = _artemisDBHelper.getLensMakeForLensManufacturer(selectedManufacturer);
//            }

//                lensMakes.add(getString(R.string.custom_zoom_lens));
                Log.i(TAG, "Lens makers available: " + lensMakes.size());

                ListView lensMakerList = (ListView) findViewById(R.id.lensMakerList);
//                ArrayAdapter<String> formatAdapter = new ArrayAdapter<String>(ArtemisActivity.this,
//                        R.layout.text_list_item, lensMakes);
                StringOptionArrayAdapter adapter = new StringOptionArrayAdapter(ArtemisActivity.this, lensMakes, lensMakerList);
                lensMakerList.setAdapter(adapter);
//                lensMakerList.setTextFilterEnabled(true);

                lensMakerList
                        .setOnItemClickListener(new LensMakerItemClickedListener());

                _lensSettingsFlipper.setInAnimation(ArtemisActivity.this,
                        R.anim.slide_in_right);
                _lensSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                        R.anim.slide_out_left);
                _lensSettingsFlipper.showNext();
            } else {
                // Display the zoom lens list (w/ add zoom lens button)

                if (zoomLenses == null) {
                    zoomLenses = _artemisDBHelper.getZoomLenses();
                    ZoomLens addnew = new ZoomLens();
                    addnew.setName(getString(R.string.new_zoom_lens));
                    zoomLenses.add(addnew);
                    ListView zoomLensList = (ListView) findViewById(R.id.customZoomLensList);
//                    ArrayAdapter<ZoomLens> adapter = new ArrayAdapter<ZoomLens>(
//                            ArtemisActivity.this, R.layout.text_list_item,
//                            zoomLenses);
                    ZoomLensesAdapter adapter = new ZoomLensesAdapter(ArtemisActivity.this, zoomLenses, zoomLensList);
                    zoomLensList.setAdapter(adapter);
                    zoomLensList
                            .setOnItemClickListener(zoomLensListItemClicked);
                    registerForContextMenu(zoomLensList);
                }

                _lensSettingsFlipper.setInAnimation(ArtemisActivity.this,
                        R.anim.slide_in_right);
                _lensSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                        R.anim.slide_out_left);
                _lensSettingsFlipper.setDisplayedChild(3);
            }
        }
    }

    final class LensMakerItemClickedListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View selectedItem,
                                int index, long id) {

            TextView selectedTextView = ((RelativeLayout) selectedItem).findViewById(R.id.text);
            tempSelectedLensMake = selectedTextView.getText().toString();
            // Log.i(_logTag, "Lens make selected: " + selectedLensMake);
            setSelectedLensMake(tempSelectedLensMake, false, true);
            // Log.i(_logTag, "Lenses available: " + _lensesForMake.size());

            loadLensesForLensMake();

            _lensSettingsFlipper.setInAnimation(ArtemisActivity.this,
                    R.anim.slide_in_right);
            _lensSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                    R.anim.slide_out_left);

            addCustomLensLayout.setVisibility(View.VISIBLE);

            _lensSettingsFlipper.showNext();
        }
    }

    final class LenseSelectionCancelClickListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            wasFocalLengthButtonPressed = false;
            _lensSettingsFlipper.setOutAnimation(null);
            openArtemisCameraPreviewView();
        }
    }

    final class LenseSelectionSaveClickListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            wasFocalLengthButtonPressed = false;
            _lensSettingsFlipper.setOutAnimation(null);
            SparseBooleanArray checkedValuesArray = _lensListView
                    .getCheckedItemPositions();
            _selectedLenses = new ArrayList<Lens>();
            String selectedLensString = "";
            for (int listIndex = 0; listIndex < checkedValuesArray.size(); listIndex++) {
                if (checkedValuesArray.valueAt(listIndex)) { // if it's checked
                    int lensMakeIndex = checkedValuesArray.keyAt(listIndex);
                    Lens lens = tempLensesForMake.get(lensMakeIndex);
                    if (listIndex > 0) {
                        selectedLensString += ",";
                    }
                    selectedLensString += lens.getPk();
                    _selectedLenses.add(lens);
                }
            }
            Log.i(TAG, "SELECTED LENSES: " + selectedLensString);

            if (tempSelectedCamera.getRowid() != -1) {
                setSelectedCamera(tempSelectedCamera.getRowid(), true, false);
            }
            setSelectedLensMake(tempSelectedLensMake, true, false);
            setSelectedLenses(selectedLensString, true, true);
            updateLensesInDB();
            _artemisMath.setFullscreen(false);
            _artemisMath.selectFirstMeaningFullLens();
            _artemisMath.calculateLargestLens();
            _artemisMath.calculateRectBoxesAndLabelsForLenses();
            _artemisMath.resetTouchToCenter(); // now with green box
            _artemisMath.onFullscreenOffSelectLens();
            mCameraPreview.calculateZoom(true);
            mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
            mCameraAngleDetailView.postInvalidate();
            reconfigureNextAndPreviousLensButtons();
            openArtemisCameraPreviewView();
        }

    }

    private void setSelectedCamera(int cameraRowId,
                                   boolean saveToAppPreferences, boolean saveTemporary) {
        if (saveTemporary) {
            tempSelectedCamera = _artemisDBHelper
                    .getCameraDetailsForRowId(cameraRowId);

        } else {
            _selectedCamera = _artemisDBHelper
                    .getCameraDetailsForRowId(cameraRowId);
            tempSelectedCamera = _selectedCamera;
            _artemisMath.setSelectedCamera(_selectedCamera);

            _cameraDetailsText.setText(_selectedCamera.getSensor() + " "
                    + _selectedCamera.getRatio());
            String text = _selectedCamera.getSensor() + " " + _selectedCamera.getRatio();
            setCameraDetailsTextAnimation();
        }
        if (saveToAppPreferences) {
            Editor appPrefsEditor = getApplication().getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
                    .edit();
            appPrefsEditor.putInt(ArtemisPreferences.SELECTED_CAMERA_ROW, cameraRowId);
            appPrefsEditor.commit();
        }
    }

    private void setCameraDetailsTextAnimation() {
        if (_cameraDetailsText.getPaint().measureText(_cameraDetailsText.getText().toString()) > findViewById(R.id.cameraDetailsView).getWidth()) {
            _cameraDetailsText.startAnimation((Animation) AnimationUtils.loadAnimation(this, R.anim.text_animation));
            _cameraDetailsText.invalidate();
        } else {
            _cameraDetailsText.clearAnimation();
        }
    }

    private void setLensMakeTextAnimation() {
        String title = tempSelectedLensMake + (selectedExtender != null ? " " + selectedExtender.getModel() : "");

        float textWidth = _lensMakeText.getPaint().measureText(title);
        int viewWidth = findViewById(R.id.lensMakeTextContainer).getWidth();
        if (textWidth > viewWidth) {
            _lensMakeText.startAnimation((Animation) AnimationUtils.loadAnimation(this, R.anim.text_animation));
            _cameraDetailsText.invalidate();
        } else {
            _lensMakeText.clearAnimation();
        }
    }

    private void setHeliosImageRotation() {
        Animation rotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_animation);
        LinearInterpolator interpolator = new LinearInterpolator();
        rotateAnimation.setInterpolator(interpolator);
        heliosImage.startAnimation(rotateAnimation);
    }

    private void setSelectedLensMake(String lensMake,
                                     boolean saveToAppPreferences, boolean saveTemporary) {
        if (!saveTemporary) {
            _lensesForMake = _artemisDBHelper.getLensesForMake(lensMake);
            tempLensesForMake = _lensesForMake;
            tempSelectedLensMake = lensMake;

<<<<<<< HEAD
<<<<<<< HEAD
            String extenderString = ArtemisActivity.selectedExtender == null ? "" : (" + " + ArtemisActivity.selectedExtender.toString());
=======
            String extenderString = ArtemisActivity.selectedExtender == null ? "" : (" + " + ArtemisActivity.selectedExtender.getModel());
>>>>>>> 5e1520f (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
=======
            String extenderString = ArtemisActivity.selectedExtender == null ? "" : (" + " + ArtemisActivity.selectedExtender.getModel());
>>>>>>> d36402c (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
            ((TextView) findViewById(R.id.lensMakeText)).setText(lensMake + extenderString);
            setLensMakeTextAnimation();
        } else {
            tempLensesForMake = _artemisDBHelper.getLensesForMake(lensMake);
            tempSelectedLensMake = lensMake;
        }
        if (saveToAppPreferences) {
            Editor appPrefsEditor = getApplication().getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
                    .edit();
            appPrefsEditor.putString(ArtemisPreferences.SELECTED_LENS_MAKE, lensMake);
            appPrefsEditor.commit();
        }
    }

    private void setSelectedLenses(String lensCSVString,
                                   boolean selectLensMakeLenses, boolean saveToAppPreferences) {
        _artemisMath.setSelectedLenses(_selectedLenses);
        _artemisMath.selectedZoomLens = null;
        if (selectLensMakeLenses) {
            _selectedLenses.clear();
            String[] lensIdStrings = lensCSVString.split(",");
            for (Lens lens : tempLensesForMake) {
                String stringid = lens.getPk().toString();
                for (String selectedId : lensIdStrings) {
                    if (stringid.equals(selectedId)) {
                        _selectedLenses.add(lens);
                    }
                }
            }
        }
        if (saveToAppPreferences) {
            Editor appPrefsEditor = getApplication().getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
                    .edit();
            appPrefsEditor.putString(ArtemisPreferences.SELECTED_LENS_ROW_CSV, lensCSVString);
            appPrefsEditor
                    .remove(getString(R.string.preference_key_selectedzoomlens));
            appPrefsEditor.commit();
        }
    }

    private void setSelectedLensIndex(int selectedLensIndex) {
        _artemisMath.set_selectedLensIndex(selectedLensIndex);
        _artemisMath.setSelectedLens(_selectedLenses.get(selectedLensIndex));
    }

    private final OnClickListener nextLensClickListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nextLens();
            if (isHapticFeedbackEnabled) {
                buzz(v);
            }
        }
    };

    private final Runnable nextLensRunnable = new Runnable() {
        public void run() {
            if (nextClickBoolean.isDown()) {
                if ((!isZoomLensSelected() && _artemisMath.hasNextLens())
                        || (isZoomLensSelected() && _artemisMath
                        .hasNextZoomLens())) {
                    nextLens();
                    mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
                            + (isZoomLensSelected() ? lensRepeatSpeedCustomLens
                            : lensRepeatSpeedNormal));
                }
            }
        }
    };

    private void nextLens() {
        if (!_artemisMath.isFullscreen()
                || _artemisMath.selectedZoomLens == null) {

            if (_artemisMath.selectNextLens()) {
                mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
                if (_artemisMath.isFullscreen()) {
                    mCameraPreview.calculateZoom(true);
                }
            }
            if (!_artemisMath.hasNextLens()) {
                _nextLensButton.setVisibility(View.INVISIBLE);
            }
            if (_artemisMath.hasPreviousLens()) {
                _prevLensButton.setVisibility(View.VISIBLE);
            }
            saveLensSelectionIndex();
        } else if (_artemisMath.isFullscreen()
                && _artemisMath.selectedZoomLens != null) {
            _artemisMath.incrementFullscreenZoomLens();
            mCameraPreview.calculateZoom(true);

            _lensFocalLengthText.setText(_artemisMath.lensFLNumberFormat
                    .format(_artemisMath.zoomLensFullScreenFL));
            if (!_artemisMath.hasNextZoomLens()) {
                _nextLensButton.setVisibility(View.INVISIBLE);
            }
            if (_artemisMath.hasPreviousZoomLens()) {
                _prevLensButton.setVisibility(View.VISIBLE);
            }
        }
        mCameraAngleDetailView.postInvalidate();
    }


    private void saveLensSelectionIndex() {
        Editor appPrefsEditor = getApplication().getSharedPreferences(
                ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
                .edit();
        appPrefsEditor.putInt(ArtemisPreferences.SELECTED_LENS_INDEX,
                _artemisMath.get_selectedLensIndex());
        appPrefsEditor.apply();
    }

    protected void reconfigureNextAndPreviousLensButtons() {
        if (_artemisMath.selectedZoomLens == null) {
            if (_artemisMath.hasNextLens()) {
                _nextLensButton.setVisibility(View.VISIBLE);
            } else {
                _nextLensButton.setVisibility(View.INVISIBLE);
            }
            if (_artemisMath.hasPreviousLens()) {
                _prevLensButton.setVisibility(View.VISIBLE);
            } else {
                _prevLensButton.setVisibility(View.INVISIBLE);
            }
        } else {
            if (!_artemisMath.hasNextZoomLens()) {
                _nextLensButton.setVisibility(View.INVISIBLE);
            }
            if (_artemisMath.hasPreviousZoomLens()) {
                _prevLensButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private final OnClickListener previousLensClickListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View v) {
            previousLens();
            if (isHapticFeedbackEnabled) {
                buzz(v);
            }
        }
    };

    private boolean isZoomLensSelected() {
        return _artemisMath != null && _artemisMath.selectedZoomLens != null ? true
                : false;
    }

    private final Runnable previousLensRunnable = new Runnable() {
        public void run() {
            if (prevClickBoolean.isDown()) {
                if ((!isZoomLensSelected() && _artemisMath.hasPreviousLens())
                        || (isZoomLensSelected() && _artemisMath
                        .hasPreviousZoomLens())) {
                    previousLens();
                    mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
                            + (isZoomLensSelected() ? lensRepeatSpeedCustomLens
                            : lensRepeatSpeedNormal));

                }
            }
        }
    };

    private void previousLens() {
        if (!_artemisMath.isFullscreen()
                || _artemisMath.selectedZoomLens == null) {
            if (_artemisMath.selectPreviousLens()) {
                mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
                if (_artemisMath.isFullscreen()) {
                    mCameraPreview.calculateZoom(true);
                }
            }
            if (!_artemisMath.hasPreviousLens()) {
                _prevLensButton.setVisibility(View.INVISIBLE);
            }
            if (_artemisMath.hasNextLens()) {
                _nextLensButton.setVisibility(View.VISIBLE);
            }
            saveLensSelectionIndex();
        } else if (_artemisMath.isFullscreen()
                && _artemisMath.selectedZoomLens != null) {
            _artemisMath.decrementFullscreenZoomLens();
            mCameraPreview.calculateZoom(true);

            _lensFocalLengthText.setText(_artemisMath.lensFLNumberFormat
                    .format(_artemisMath.zoomLensFullScreenFL));

            if (!_artemisMath.hasPreviousZoomLens()) {
                _prevLensButton.setVisibility(View.INVISIBLE);
            }
            if (_artemisMath.hasNextZoomLens()) {
                _nextLensButton.setVisibility(View.VISIBLE);
            }

        }
        mCameraAngleDetailView.postInvalidate();
    }

    final class focalLengthLensButtonViewClickListener implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            addCustomLensLayout.setVisibility(View.VISIBLE);

            _lensSettingsFlipper.setInAnimation(null);
            _lensSettingsFlipper.setOutAnimation(null);

            if (_artemisMath.selectedZoomLens == null) {
                // Go to the final lens setting page
                loadLensesForLensMake();
                _lensSettingsFlipper.setDisplayedChild(2);

                currentViewId = R.id.lensSettings;
                viewFlipper.setDisplayedChild(2);

                wasFocalLengthButtonPressed = true;

            } else {
                // Zoom lens selected
                openLensSettingsView();
            }

            if (isHapticFeedbackEnabled) {
                buzz(v);
            }
        }
    }

    public String loadLensesForLensMake() {
        ArrayList<String> focalLengths = new ArrayList<String>();
        ArrayList<Integer> checked = new ArrayList<Integer>();
        int position = 0;
        String lensIdList = "";
        NumberFormat nf = NumberFormat.getInstance();
        for (Lens lens : tempLensesForMake) {
            focalLengths.add(nf.format(lens.getFL()) + " mm");
            if ("1".equals(lens.getLensSet())) {
                // if lensSet is "1" then the item is checked
                checked.add(position);
                lensIdList += lens.getPk() + ",";
            }
            ++position;
        }
        if (lensIdList.length() > 0)
            lensIdList = lensIdList.substring(0, lensIdList.length() - 1);

        LensArrayAdapter lensArrayAdapter = new LensArrayAdapter(this, tempLensesForMake, _lensListView);

        _lensListView.setAdapter(lensArrayAdapter);
//        _lensListView.setTextFilterEnabled(true);
        _lensListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        final TextView saveLensesButton = (TextView) findViewById(R.id.saveLenses);
        saveLensesButton
                .setOnClickListener(new LenseSelectionSaveClickListener());

        _lensListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long id) {
                System.out.println("checked Item count: " + _lensListView.getCheckedItemPositions());
                if (_lensListView.getCheckedItemCount() > 0) {
                    saveLensesButton.setEnabled(true);
                } else {
                    saveLensesButton.setEnabled(false);
                }
                _lensListView.invalidate();
                lensArrayAdapter.notifyDataSetChanged();
            }
        });


        // set the checked items
        for (Integer pos : checked) {
            _lensListView.setItemChecked(pos, true);
        }
        if (_lensListView.getCheckedItemCount() == 0) {
            findViewById(R.id.saveLenses).setEnabled(false);
        }

        return lensIdList;
    }

    private void updateLensesInDB() {
        for (Lens lensInSelectList : tempLensesForMake) {
            if (_selectedLenses.contains(lensInSelectList)) {
                lensInSelectList.setLensSet("1");
            } else {
                lensInSelectList.setLensSet("0");
            }
        }
        _artemisDBHelper.updateLensSelections(tempLensesForMake);
    }

    final OnClickListener fullscreenViewClickListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (_artemisMath.isFullscreen()) {
                // non fullscreen with all lens boxes
                _artemisMath.setFullscreen(false);
                _artemisMath.onFullscreenOffSelectLens();
                mCameraAngleDetailView.postInvalidate();
            } else {
                _artemisMath.setFullscreen(true);
                if (_artemisMath.selectedZoomLens != null) {
                    _artemisMath.onFullscreenSetupZoomLens();
                }
            }
            reconfigureNextAndPreviousLensButtons();
            mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
            mCameraPreview.calculateZoom(true);

            if (isHapticFeedbackEnabled) {
                buzz(v);
            }
        }
    };

    final OnClickListener takePictureClickListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "Artemis taking a picture");
            shutterButtonPressed(v);
        }
    };

    private void shutterButtonPressed(View sender) {
        if (lastKnownLocation != null)
            pictureSaveLocation = lastKnownLocation;

        pictureSaveHeadingTiltString = headingTiltText.getText().toString();

        if (CameraPreview21.quickshotEnabled) {
            mCameraPreview.takePicture();
            return;
        }

        savePictureViewFlipper.setInAnimation(null);
        savePictureViewFlipper.setDisplayedChild(0);
        // normal take picture (not quickshot)
        mCameraPreview.takePicture();

        if (isHapticFeedbackEnabled) {
            buzz(sender);
        }
    }

    final class CancelSavePictureClickListener implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isEditingPictureDetailsOnly) {
                // was just editing the picture details from the main screen
                isEditingPictureDetailsOnly = false;
            } else {
                // just previously took a picture
                pictureSavePreview.setImageBitmap(null);
            }

            savePictureViewFlipper.setDisplayedChild(0); // reset to first view
            openArtemisCameraPreviewView();
        }
    }

    final class SavePictureClickListener implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Toast toast = Toast.makeText(ArtemisActivity.this,
                    getString(R.string.image_saved_success), Toast.LENGTH_LONG);
            toast.show();

            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCameraPreview.renderPictureDetailsAndSave();
                        }
                    });
                    System.gc();
                    return "";
                }

                // @Override
                // protected void onPostExecute(String result) {
                // toast.cancel();
                // sendBroadcast(new Intent(
                // Intent.ACTION_MEDIA_MOUNTED,
                // Uri.parse("file:/"
                // + Environment.getExternalStorageDirectory())));
                // }
            }.execute();

            openArtemisCameraPreviewView();
        }
    }

    final class EditPictureDetailsClickListener implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            pictureSavePreview.setImageBitmap(null);
            System.gc();

            loadPictureDetailsSettings();

            savePictureViewFlipper.showNext();
        }
    }

    private void loadPictureDetailsSettings() {
        if (!gpsEnabled) {
            ToggleButton gpsDetails = (ToggleButton) findViewById(R.id.gpsCoordinatesToggle);
            gpsDetails.setEnabled(false);
            gpsDetails.setChecked(false);
        }

        SharedPreferences artemisPrefs = getSharedPreferences(
                ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);

        ((EditText) findViewById(R.id.imageTitle))
                .setText(artemisPrefs.getString(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE, ""));
        ((EditText) findViewById(R.id.imageNotes)).setText(artemisPrefs
                .getString(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, ""));
        ((EditText) findViewById(R.id.imageContactName)).setText(artemisPrefs
                .getString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME, ""));
        ((EditText) findViewById(R.id.imageContactEmail)).setText(artemisPrefs
                .getString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL, ""));
        ((ToggleButton) findViewById(R.id.cameraDetailsToggle))
                .setChecked(artemisPrefs.getBoolean(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS,
                        true));
        ((ToggleButton) findViewById(R.id.lensDetailsToggle))
                .setChecked(artemisPrefs
                        .getBoolean(
                                ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS,
                                true));
        ((ToggleButton) findViewById(R.id.gpsCoordinatesToggle))
                .setChecked(artemisPrefs.getBoolean(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION, true));
        ((ToggleButton) findViewById(R.id.gpsAddressToggle))
                .setChecked(artemisPrefs
                        .getBoolean(
                                ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_ADDRESS,
                                true));
        ((ToggleButton) findViewById(R.id.sunrise_and_sunset_toggle))
                .setChecked(artemisPrefs.getBoolean(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_SUNRISE_AND_SUNSET, true));
        final EditText sunriseAndSunsetEditText = findViewById(R.id.sunrise_and_sunset);
        sunriseAndSunsetEditText.setText(artemisPrefs.getString(
                ArtemisPreferences.SAVE_PICTURE_SUNRISE_AND_SUNSET, ""));
        sunriseAndSunsetEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sunrise_and_sunset:
                        final Calendar c = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(ArtemisActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                final int actualMonth = month + 1;
                                String formattedDay = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                                String formattedMonth = (actualMonth < 10) ? "0" + actualMonth : String.valueOf(actualMonth);
                                Calendar dateSelected = Calendar.getInstance();
                                dateSelected.set(year, month, dayOfMonth);

                                com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(ArtemisActivity.pictureSaveLocation
                                        .getLatitude(), ArtemisActivity.pictureSaveLocation
                                        .getLongitude());
                                SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, Calendar.getInstance().getTimeZone());

                                String sunriseAndSunsetString = "Date " + year + "-" + formattedMonth + "-" + formattedDay +
                                        ": Sunrise " + calculator.getOfficialSunriseForDate(dateSelected) +
                                        " Sunset " + calculator.getOfficialSunsetForDate(dateSelected);
                                sunriseAndSunsetEditText.setText(sunriseAndSunsetString);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                        break;
                }
            }
        });
        ToggleButton tiltAndDirectionToggle = (ToggleButton) findViewById(R.id.tilt_and_direction_toggle);
        tiltAndDirectionToggle.setChecked(artemisPrefs.getBoolean(
                ArtemisPreferences.SAVE_PICTURE_SHOW_TILT_AND_DIRECTION, true));
        if (ArtemisActivity.headingDisplaySelection == 0) {
            tiltAndDirectionToggle.setEnabled(false);
        }
        ((ToggleButton) findViewById(R.id.exposure_toggle))
                .setChecked(artemisPrefs.getBoolean(
                        ArtemisPreferences.SAVE_PICTURE_SHOW_EXPOSURE, true));

    }

    final class SavePictureEditDetailsClickListener implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {

            Editor editor = getApplication().getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(),
                    Context.MODE_PRIVATE).edit();

            editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_TITLE,
                    ((EditText) findViewById(R.id.imageTitle)).getText()
                            .toString());
            editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES,
                    ((EditText) findViewById(R.id.imageNotes)).getText()
                            .toString());
            editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_NAME,
                    ((EditText) findViewById(R.id.imageContactName)).getText()
                            .toString());
            editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_CONTACT_EMAIL,
                    ((EditText) findViewById(R.id.imageContactEmail)).getText()
                            .toString());
            editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION,
                    ((ToggleButton) findViewById(R.id.gpsCoordinatesToggle))
                            .isChecked());
            editor.putBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_ADDRESS,
                    ((ToggleButton) findViewById(R.id.gpsAddressToggle))
                            .isChecked());
            editor.putBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS,
                    ((ToggleButton) findViewById(R.id.cameraDetailsToggle))
                            .isChecked());
            editor.putBoolean(
                    ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS,
                    ((ToggleButton) findViewById(R.id.lensDetailsToggle))
                            .isChecked());
            editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_SUNRISE_AND_SUNSET,
                    ((ToggleButton) findViewById(R.id.sunrise_and_sunset_toggle))
                            .isChecked());
            editor.putString(ArtemisPreferences.SAVE_PICTURE_SUNRISE_AND_SUNSET,
                    ((EditText) findViewById(R.id.sunrise_and_sunset)).getText()
                            .toString());
            editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_TILT_AND_DIRECTION,
                    ((ToggleButton) findViewById(R.id.tilt_and_direction_toggle))
                            .isChecked());
            editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_EXPOSURE,
                    ((ToggleButton) findViewById(R.id.exposure_toggle))
                            .isChecked());
            editor.commit();

            if (isEditingPictureDetailsOnly) {
                // Do nothing else -- used for the state when the user presses
                // the pencil icon in the main screen
                isEditingPictureDetailsOnly = false;
            } else {
                final Toast toast = Toast.makeText(ArtemisActivity.this,
                        getString(R.string.image_saved_success),
                        Toast.LENGTH_LONG);
//				toast.setDuration(2500);
                toast.show();

                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCameraPreview.renderPictureDetailsAndSave();
                            }
                        });
                        System.gc();
                        return "";
                    }

                    // @Override
                    // protected void onPostExecute(String result) {
                    // toast.cancel();
                    // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    // Uri.parse("file://"
                    // + Environment
                    // .getExternalStorageDirectory())));
                    // }
                }.execute(new String[]{});

            }

            savePictureViewFlipper.setDisplayedChild(0);
            savePictureViewFlipper.stopFlipping();
            openArtemisCameraPreviewView();
        }
    }

    public static String[] getGPSLocationDetailStrings(Context context) {
        String gpsDetails = "";
        String gpsLocationString = "";

        String latitude = "";
        String longitude = "";

        if (pictureSaveLocation != null && gpsEnabled) {
            latitude = Location.convert(pictureSaveLocation.getLatitude(), Location.FORMAT_DEGREES);
            longitude = Location.convert(pictureSaveLocation.getLongitude(), Location.FORMAT_DEGREES);
            gpsDetails = "Lat: " + latitude + ", Long: " + longitude;
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> addressList = geocoder.getFromLocation(
                        pictureSaveLocation.getLatitude(),
                        pictureSaveLocation.getLongitude(), 1);
                if (addressList.iterator().hasNext()) {
                    Address addr = addressList.iterator().next();
                    int nItems = 0;
                    String bullet = " " + context.getString(R.string.bullet)
                            + " ";
                    if (addr.getAddressLine(0) != null) {
                        if (nItems > 0)
                            gpsLocationString += bullet;
                        gpsLocationString += addr.getAddressLine(0);
                        ++nItems;
                    }
                    if (addr.getLocality() != null) {
                        if (nItems > 0)
                            gpsLocationString += bullet;
                        gpsLocationString += addr.getLocality();
                        ++nItems;
                    }
                    if (addr.getAdminArea() != null) {
                        if (nItems > 0)
                            gpsLocationString += bullet;
                        gpsLocationString += addr.getAdminArea();
                        ++nItems;
                    }
                    if (addr.getPostalCode() != null) {
                        if (nItems > 0)
                            gpsLocationString += bullet;
                        gpsLocationString += addr.getPostalCode();
                        ++nItems;
                    }
                    if (addr.getCountryName() != null) {
                        if (nItems > 0)
                            gpsLocationString += bullet;
                        gpsLocationString += addr.getCountryCode();
                        ++nItems;
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error retrieving geocoder location data");
            }
        }
        return new String[]{gpsDetails, gpsLocationString, latitude, longitude};
    }

    final class CustomCameraClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View selectedItem,
                                int selectedIndex, long arg3) {
            TextView selectedTextView = ((RelativeLayout) selectedItem).findViewById(R.id.text);
            String selectedFormat = selectedTextView.getText().toString();

            if (getString(R.string.custom_camera_new_camera).equals(
                    selectedFormat)) {
                // New custom camera was selected...
                _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                        R.anim.slide_in_right);
                _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                        R.anim.slide_out_left);
                _cameraSettingsFlipper.setDisplayedChild(5);
                return;
            } else if (customCameras != null && customCameras.size() > 0) {
                CustomCamera selectedCustomCamera = customCameras
                        .get(selectedIndex);
                // Create a Camera object to hold the CustomCamera and act as a
                // regular camera
                Camera selectedCam = new Camera(selectedCustomCamera);
                // selectedCam.setSqz(1f);
                // camera
                //
                _selectedCamera = selectedCam;
                tempSelectedCamera = selectedCam;
                _artemisMath.setSelectedCamera(_selectedCamera);
                // // _selectedLenses.clear();
                // // set it to a negative value so we know to look in custom
                // // camera table
                // // setSelectedCamera(-selectedCustomCamera.getRowid(), true,
                // // false);
                //
                Editor appPrefsEditor = getApplication().getSharedPreferences(
                        ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
                        .edit();
                appPrefsEditor.putInt(ArtemisPreferences.SELECTED_CAMERA_ROW,
                        -selectedCustomCamera.getPk());
                appPrefsEditor.commit();

//                setSelectedLensMake(DEFAULT_LENS_MAKE, false, false);
                setSelectedLenses(loadLensesForLensMake(), true, true);


                _cameraDetailsText.setText(selectedCustomCamera.getName() + " "
                        + _selectedCamera.getRatio());
                String text = selectedCustomCamera.getName() + " " + _selectedCamera.getRatio();
                setCameraDetailsTextAnimation();
                //
                // // Recalculate the view for new camera and lenses
                _artemisMath.setFullscreen(false);
                _artemisMath.calculateLargestLens();
                // // call twice fixes the refresh bug after select lenses.
                // // otherwise it doesn't redraw, strange looking boxes appear
                // ???
                mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
                _artemisMath.selectFirstMeaningFullLens();
                _artemisMath.onFullscreenOffSelectLens();
                _artemisMath.resetTouchToCenter();
                mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
                mCameraAngleDetailView.postInvalidate();
                openLensSettingsView();
            }

        }
    }

    private final OnClickListener addCustomCameraClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText nameText = (EditText) findViewById(R.id.custom_camera_active_sensor_name);
            String cameraName = nameText.getText().toString();
            if (cameraName.length() == 0) {
                cameraName = getString(R.string.untitle_custom_cam);
            }
//            EditText squeezeText = (EditText) findViewById(R.id.custom_camera_squeezeratio_as);
//            if (squeezeText.getText().toString().length() == 0) {
//                squeezeText.setText("1");
//            }
            EditText widthText = (EditText) findViewById(R.id.custom_camera_width);
            EditText heightText = (EditText) findViewById(R.id.custom_camera_height);

            try {
                float cameraWidth = Float.parseFloat(widthText.getText()
                        .toString());
                float cameraHeight = Float.parseFloat(heightText.getText()
                        .toString());
                float squeeze = 1;

                // insert the new camera
                CustomCamera customCam = new CustomCamera();
                customCam.setName(cameraName);
                customCam.setSensorwidth(cameraWidth);
                customCam.setSensorheight(cameraHeight);
                customCam.setSqueeze(squeeze);
                _artemisDBHelper.insertCustomCamera(customCam);

                // refresh the list in previous page
                refreshCustomCameraList();

                // clear the text off on success
                nameText.setText("");
//                squeezeText.setText("");
                widthText.setText("");
                heightText.setText("");

                // flip back to the previous custom camera selection page
                _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                        R.anim.slide_in_left);
                _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                        R.anim.slide_out_right);
                // set the displayed child to the last page ( == num pages)
                _cameraSettingsFlipper.setDisplayedChild(NUM_CAMERA_PAGES);

            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog errorDialog = new AlertDialog.Builder(
                        ArtemisActivity.this)
                        .setTitle(R.string.custom_camera_error)
                        .setMessage(R.string.custom_camera_error_message)
                        .setNegativeButton(R.string.ok, null).create();
                errorDialog.show();
            }

        }

    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (data != null && data.getBooleanExtra("newcustomcam", false)) {
                refreshCustomCameraList();

                _cameraSettingsFlipper.setInAnimation(null);
                _cameraSettingsFlipper.setOutAnimation(null);
                _cameraSettingsFlipper.setDisplayedChild(_cameraSettingsFlipper
                        .getDisplayedChild() - 1);
            }
        } else if(requestCode == CHOOSE_MODEL_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null && data.getIntExtra("model", CHOOSEN_MODEL_DEFAULT_VALUE) != CHOOSEN_MODEL_DEFAULT_VALUE) {
                    String modelName = getModelFromCode(data.getIntExtra("model", 0));
                    setup3DModels(modelName);
                }
<<<<<<< HEAD
=======
            } else if(resultCode == RESULT_CANCELED) {
                findViewById(R.id.editVirtualStandInMenu).setVisibility(View.GONE);
                mainMenu.setVisibility(View.VISIBLE);
                TouchController.isEditing = false;
<<<<<<< HEAD
>>>>>>> 5e1520f (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
=======
>>>>>>> d36402c (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
            }
        }
    }

    private String getModelFromCode(int modelCode) {
        if(modelCode % 2 == 0){
            return "Female.obj";
        } else {
            return "FinalBaseMesh.obj";
        }
    }

    private void refreshCustomCameraList() {
        customCameras = _artemisDBHelper.getCustomCameras();
        CustomCamera addnew = new CustomCamera();
        addnew.setName(getString(R.string.custom_camera_new_camera));
        customCameras.add(addnew);
        ListView customCameraList = (ListView) findViewById(R.id.customCameraList);
//        ArrayAdapter<CustomCamera> adapter = new ArrayAdapter<CustomCamera>(
//                ArtemisActivity.this, R.layout.text_list_item, customCameras);
        CustomCameraAdapter adapter = new CustomCameraAdapter(ArtemisActivity.this, customCameras, customCameraList);
        customCameraList.setAdapter(adapter);
    }

    private void refreshZoomLensList() {
        zoomLenses = _artemisDBHelper.getZoomLenses();
        ZoomLens addnew = new ZoomLens();
        addnew.setName(getString(R.string.new_zoom_lens));
        zoomLenses.add(addnew);
        ListView zoomLensList = (ListView) findViewById(R.id.customZoomLensList);
//        ArrayAdapter<ZoomLens> adapter = new ArrayAdapter<ZoomLens>(
//                ArtemisActivity.this, R.layout.text_list_item, zoomLenses);
        ZoomLensesAdapter adapter = new ZoomLensesAdapter(ArtemisActivity.this, zoomLenses, zoomLensList);
        zoomLensList.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenuInfo contextMenuInfo) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) contextMenuInfo;
        Log.d(TAG, String.format("%s %s", view.toString(), view.getParent()
                .toString()));
        if (((View) view.getParent()).getId() == R.id.camera_settings_flipper
                && menuInfo.position < customCameras.size() - 1) {
            menu.setHeaderTitle(getString(R.string.custom_camera_options));
            menu.add(0, 0, 0, getString(R.string.custom_camera_edit_camera));
            menu.add(0, 1, 1, getString(R.string.custom_camera_delete_camera));
        } else if (((View) view.getParent()).getId() == R.id.lens_settings_flipper
                && menuInfo.position < zoomLenses.size() - 1) {
            menu.setHeaderTitle(getString(R.string.zoom_lens_options));
            menu.add(1, 0, 0, getString(R.string.zoom_lens_edit));
            menu.add(1, 1, 1, getString(R.string.zoom_lens_delete));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getGroupId() == 0 && item.getItemId() == 0) {
            // edit custom camera was selected...
            CustomCamera editCamera = customCameras.get(menuInfo.position);

            ((Button) findViewById(R.id.save_custom_camera_active_sensor))
                    .setOnClickListener(new UpdateCustomCameraClickListener(
                            editCamera));

            ((EditText) findViewById(R.id.custom_camera_active_sensor_name))
                    .setText(editCamera.getName());
//            EditText squeezeText = (EditText) findViewById(R.id.custom_camera_squeezeratio_as);
//            if (squeezeText.getText().toString().length() == 0) {
//                squeezeText.setText("1");
//            }

            ((EditText) findViewById(R.id.custom_camera_width))
                    .setText(editCamera.getSensorwidth() + "");
            ((EditText) findViewById(R.id.custom_camera_height))
                    .setText(editCamera.getSensorheight() + "");

            _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                    R.anim.slide_in_right);
            _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                    R.anim.slide_out_left);
            _cameraSettingsFlipper.setDisplayedChild(5);
        } else if (item.getGroupId() == 0 && item.getItemId() == 1) {
            // delete custom camera
            _artemisDBHelper.deleteCustomCameraByRowId(customCameras.get(
                    menuInfo.position).getPk());
            refreshCustomCameraList();
        } else if (item.getGroupId() == 1 && item.getItemId() == 0) {
            // edit zoom lens
            final ZoomLens edited = zoomLenses.get(menuInfo.position);
            View alertView = View.inflate(ArtemisActivity.this,
                    R.layout.create_zoom_lens_dialog, null);
            ((EditText) alertView.findViewById(R.id.zoomLensName))
                    .setText(edited.getName());
            ((EditText) alertView.findViewById(R.id.zoomLensMinFL))
                    .setText(edited.getMinFL() + "");
            EditText finalEditText = (EditText) alertView
                    .findViewById(R.id.zoomLensMaxFL);
            finalEditText.setText(edited.getMaxFL() + "");
            finalEditText
                    .setOnEditorActionListener(new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int action,
                                                      KeyEvent event) {
                            if (action == EditorInfo.IME_ACTION_DONE) {
                                ((View) v.getParent().getParent())
                                        .requestFocus();
                            }
                            return false;
                        }
                    });

            final AlertDialog addZoomLensDialog = new AlertDialog.Builder(
                    ArtemisActivity.this)
                    .setView(alertView)
                    .setTitle(R.string.edit_zoom_lens)
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            }).create();
            addZoomLensDialog.setButton(Dialog.BUTTON_POSITIVE,
                    getString(R.string.save),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String name = ((EditText) addZoomLensDialog
                                    .findViewById(R.id.zoomLensName)).getText()
                                    .toString();
                            if (name.isEmpty()) {
                                name = getString(R.string.untitled_zoom_lens);
                            }

                            String min = ((EditText) addZoomLensDialog
                                    .findViewById(R.id.zoomLensMinFL))
                                    .getText().toString();
                            String max = ((EditText) addZoomLensDialog
                                    .findViewById(R.id.zoomLensMaxFL))
                                    .getText().toString();

                            Float minFL = null;
                            try {
                                minFL = Float.parseFloat(min);
                            } catch (NumberFormatException e) {
                            }
                            Float maxFL = null;
                            try {
                                maxFL = Float.parseFloat(max);
                            } catch (NumberFormatException e) {
                            }

                            if (minFL == null || maxFL == null || minFL <= 0
                                    || maxFL <= 0 || minFL >= maxFL) {
                                new AlertDialog.Builder(ArtemisActivity.this)
                                        .setTitle(
                                                R.string.custom_zoom_lens_error)
                                        .setMessage(
                                                R.string.custom_zoom_lens_error_message)
                                        .setPositiveButton(
                                                R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        addZoomLensDialog
                                                                .show();
                                                    }
                                                }).create().show();

                            } else {
                                edited.setName(name);
                                edited.setMinFL(minFL);
                                edited.setMaxFL(maxFL);
                                _artemisDBHelper.updateZoomLens(edited);
                                ((ArrayAdapter<?>) ((ListView) findViewById(R.id.customZoomLensList))
                                        .getAdapter()).notifyDataSetChanged();
                            }
                        }
                    });
            addZoomLensDialog.show();

        } else if (item.getGroupId() == 1 && item.getItemId() == 1) {
            // delete zoom lens
            _artemisDBHelper.deleteZoomLensByPK(zoomLenses.get(
                    menuInfo.position).getPk());
            refreshZoomLensList();

        }

        return super.onContextItemSelected(item);
    }

    final class UpdateCustomCameraClickListener implements
            android.view.View.OnClickListener {

        private CustomCamera updateCamera;

        public UpdateCustomCameraClickListener(CustomCamera camera) {
            updateCamera = camera;
        }

        @Override
        public void onClick(View v) {
            EditText nameText = (EditText) findViewById(R.id.custom_camera_active_sensor_name);
            String cameraName = nameText.getText().toString();
//            EditText squeezeText = (EditText) findViewById(R.id.custom_camera_squeezeratio_as);
            EditText widthText = (EditText) findViewById(R.id.custom_camera_width);
            EditText heightText = (EditText) findViewById(R.id.custom_camera_height);

            try {
                float cameraWidth = Float.parseFloat(widthText.getText()
                        .toString());
                float cameraHeight = Float.parseFloat(heightText.getText()
                        .toString());
//                if (squeezeText.getText().toString().length() == 0) {
//                    squeezeText.setText("1");
//                }
                float squeeze = 1;

                // // update the new camera
                updateCamera.setName(cameraName);
                //
                // // calculate the viewing angles
                updateCamera.setSensorwidth(cameraWidth);
                updateCamera.setSensorheight(cameraHeight);
                updateCamera.setSqueeze(squeeze);
                _artemisDBHelper.updateCustomCamera(updateCamera);

                // refresh the list in previous page
                refreshCustomCameraList();

                // clear the text off on success
                nameText.setText("");
                widthText.setText("");
                heightText.setText("");
//                squeezeText.setText("");

                // flip back to the previous custom camera selection page
                _cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
                        R.anim.slide_in_left);
                _cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
                        R.anim.slide_out_right);
                // set the displayed child to the last page ( == num pages)
                _cameraSettingsFlipper.setDisplayedChild(NUM_CAMERA_PAGES);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ;

    private OnClickListener addCustomLensClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText input = new EditText(ArtemisActivity.this);
            input.setRawInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            new AlertDialog.Builder(ArtemisActivity.this)
                    .setTitle(getString(R.string.add_custom_lens))
                    .setMessage(getString(R.string.input_focal_length))
                    .setView(input)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    String value = input.getText().toString();
                                    Float focalLength;
                                    try {
                                        focalLength = Float.parseFloat(value);
                                    } catch (NumberFormatException nfe) {
                                        AlertDialog errorDialog = new AlertDialog.Builder(
                                                ArtemisActivity.this)
                                                .setTitle(
                                                        R.string.custom_lens_error)
                                                .setMessage(
                                                        R.string.custom_lens_error_message)
                                                .setNegativeButton(R.string.ok,
                                                        null).create();
                                        errorDialog.show();
                                        return;
                                    }

                                    Lens customLens = new Lens(_artemisDBHelper
                                            .getLensByRowId(_artemisMath
                                                    .getSelectedLenses().get(0)
                                                    .getPk()));
                                    customLens.setCustomLens(true);
                                    customLens.setFL(focalLength);
                                    customLens.setSqueeze(1);
                                    customLens
                                            .setLensMake(tempSelectedLensMake);
                                    customLens.setLensSet("1");
                                    _artemisDBHelper.addCustomLens(customLens);

                                    setSelectedLensMake(
                                            customLens.getLensMake(), true,
                                            false);
                                    setSelectedLenses(loadLensesForLensMake(),
                                            false, true);

                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                }
                            }).show();
        }
    };

    private void openHelpFile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri
                .parse("https://docs.google.com/document/d/1aS4i-ipOhgPgQRC47QCYq7Tji-dxeIQLJzhdgd_mNL8/edit?hl=en&pli=1&rm=demo"));
        startActivity(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    pictureSaveHeadingTiltString = headingTiltText.getText()
                            .toString();
                    if (volumeUpAutoFocusAndPicture) {
                        mCameraPreview.autofocusCamera(true);
                    } else if (volumeUpPicture) {
                        mCameraPreview.takePicture();
                    } else if (volumeUpAutoFocus) {
                        mCameraPreview.autofocusCamera(false);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    pictureSaveHeadingTiltString = headingTiltText.getText()
                            .toString();
                    if (volumeDownAutoFocusAndPicture) {
                        mCameraPreview.autofocusCamera(true);
                    } else if (volumeDownPicture) {
                        mCameraPreview.takePicture();
                    } else if (volumeDownAutoFocus) {
                        mCameraPreview.autofocusCamera(false);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_CAMERA:
                if (action == KeyEvent.ACTION_DOWN && mCameraPreview != null) {
                    // just pass a view to the shutter event
                    pictureSaveHeadingTiltString = headingTiltText.getText()
                            .toString();
                    shutterButtonPressed(_lensFocalLengthText);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_settings:
                openCameraSettingsView();
                return true;
            case R.id.lens_settings:
                openLensSettingsView();
                return true;
            case R.id.artemis_settings:
                openArtemisSettings();
                return true;
            case R.id.artemis_about:
                openArtemisAboutView();
                return true;
            case R.id.artemis_gallery:
                openGallery();
                return true;
            case R.id.artemis_quit:
                finish();
                return false;
        }

        return false;
    }

    @Override
    public void finish() {
        deleteModelFile();
        super.finish();
    }

    public void deleteModelFile(){
        File modelFile = ImageUtils.getModelFile(getApplicationContext());
        if (modelFile.exists()) {
            modelFile.delete();
        }
    }

    private void openArtemisAboutView() {
        Intent i = new Intent(ArtemisActivity.this, AboutActivity.class);
        startActivityForResult(i, 1);
    }

    private void openArtemisSettings() {
        Intent settingsIntent = new Intent(ArtemisActivity.this,
                SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private Frameline buildDefaultFrameline() {
        ArrayList<FramelineRate> framelineRates = _artemisDBHelper.getFramelineRates();
        Frameline frameline = new Frameline();
        frameline.setRate(framelineRates.get(0));
        frameline.setScale(100);
        frameline.setShading(0);
        frameline.setVerticalOffset(0);
        frameline.setHorizontalOffset(0);
        frameline.setDotted(false);

        frameline.setFramelineType(1);
        frameline.setColor(Color.WHITE);
        frameline.setLineWidth(1);
        frameline.setCenterMarkerType(0);
        frameline.setCenterMarkerLineWidth(1);
        frameline.setApplied(false);

        return frameline;
    }

    private void showFramelineMenu(Frameline frameline) {
        mCameraOverlay.currentFrameline = frameline;
        openArtemisCameraPreviewView();
        findViewById(R.id.cameraMenuTop).setVisibility(View.GONE);
        findViewById(R.id.mainMenu).setVisibility(View.GONE);

        _artemisMath.setFullscreen(true);
        if (_artemisMath.selectedZoomLens != null) {
            _artemisMath.onFullscreenSetupZoomLens();
        }

        findViewById(R.id.framelineTopMenu).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineMenu).setVisibility(View.VISIBLE);
    }

    private void hideFramelineMenu() {
        findViewById(R.id.cameraMenuTop).setVisibility(View.VISIBLE);
        findViewById(R.id.mainMenu).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineTopMenu).setVisibility(View.GONE);
        findViewById(R.id.framelineMenu).setVisibility(View.GONE);
    }

    private void showFramelineRateMenu() {
        findViewById(R.id.framelineTopMenu).setVisibility(View.GONE);
        findViewById(R.id.framelineMenu).setVisibility(View.GONE);

        findViewById(R.id.framelineTopBack).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineRateMenu).setVisibility(View.VISIBLE);

        listFramelineRates();
    }

    private void showFramelineScaleMenu() {
        findViewById(R.id.framelineTopMenu).setVisibility(View.GONE);
        findViewById(R.id.framelineMenu).setVisibility(View.GONE);

        findViewById(R.id.framelineScaleTop).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineScaleMenu).setVisibility(View.VISIBLE);

        ((SeekBar) findViewById(R.id.scaleBar)).setProgress(mCameraOverlay.currentFrameline.getScale());
    }

    private void showFramelineShadingMenu() {
        findViewById(R.id.framelineTopMenu).setVisibility(View.GONE);
        findViewById(R.id.framelineMenu).setVisibility(View.GONE);

        findViewById(R.id.framelineShadingTop).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineShadingMenu).setVisibility(View.VISIBLE);
    }

    private void showFramelineStyleMenu() {
        findViewById(R.id.framelineTopMenu).setVisibility(View.GONE);
        findViewById(R.id.framelineMenu).setVisibility(View.GONE);

        findViewById(R.id.framelineStyleTop).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineStyleMenu).setVisibility(View.VISIBLE);
    }

    private void showFramelineLineWidthMenu() {
        findViewById(R.id.framelineTopMenu).setVisibility(View.GONE);
        findViewById(R.id.framelineMenu).setVisibility(View.GONE);

        findViewById(R.id.framelineLineWidthTop).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineLineWidthMenu).setVisibility(View.VISIBLE);
    }

    private void showFramelineCenterMarkerStyleMenu() {
        findViewById(R.id.framelineTopMenu).setVisibility(View.GONE);
        findViewById(R.id.framelineMenu).setVisibility(View.GONE);

        findViewById(R.id.framelineCenterMarkerStyleTop).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineCenterMarkerStyleMenu).setVisibility(View.VISIBLE);
    }

    public void openColorPickerDialog() {
        findViewById(R.id.colorPickerMenu).setVisibility(View.VISIBLE);

        ColorPickerView colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.fireColorListener(mCameraOverlay.currentFrameline.getColor(), false);
        final BrightnessSlideBar brightnessSlideBar = findViewById(R.id.brightnessSlide);
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);
        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                Log.i("MYTAG", "ENVELOP COLOR: " + envelope.getHexCode());
                AlphaTileView alphaTileView = findViewById(R.id.alphaTileView);
                alphaTileView.setPaintColor(envelope.getColor());
            }
        });
    }

    private void showFramelineOffsetMenu() {
        findViewById(R.id.framelineTopMenu).setVisibility(View.GONE);
        findViewById(R.id.framelineMenu).setVisibility(View.GONE);

        findViewById(R.id.framelineOffsetTop).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineOffsetMenu).setVisibility(View.VISIBLE);

        findViewById(R.id.framelineOffsetTopButtonContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineOffsetBottomButtonContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineOffsetLeftButtonContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.framelineOffsetRightButtonContainer).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.txtFramelineHorizontalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getHorizontalOffset()));
        ((TextView) findViewById(R.id.txtFramelineVerticalOffset)).setText(String.valueOf(mCameraOverlay.currentFrameline.getVerticalOffset()));
    }

    private void listFramelineRates() {
        List<FramelineRate> rates = _artemisDBHelper.getFramelineRates();
        RecyclerView recyclerView = findViewById(R.id.framelineRatesRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ArtemisActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new FramelineRatesAdapter(rates, ArtemisActivity.this, ArtemisActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void selectedFramelineRate(FramelineRate framelineRate) {
        Log.i("MyTag", "selectedFramelineRate " + framelineRate.toString());
    }

    @Override
    public void deleteFramelineRate(FramelineRate framelineRate) {
        Log.i("MyTag", "deleteFramelineRate " + framelineRate.toString());
    }

    public void openAddCustomFramelineRateDialog() {
        Log.i("MyTag", "openAddCustomFramelineRateDialog ");
        // todo - show dialog to enter a custom rate and then save it and update the list of frameline rates
    }

    public void saveCurrentFramelineToDb(Frameline frameline) {
        _artemisDBHelper.saveFrameline(frameline);
    }
}
